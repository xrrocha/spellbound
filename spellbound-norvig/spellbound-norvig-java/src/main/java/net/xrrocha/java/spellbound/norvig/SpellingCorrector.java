package net.xrrocha.java.spellbound.norvig;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyList;

/**
 * Java implementation of PeterNorvig's
 * <a href="http://norvig.com/spell-correct.html">Spelling Corrector</a>.
 * This implementation is based on Java's functional constructs.
 */
public class SpellingCorrector {

  /**
   * The word-to-rank dictionary. The lower the rank the higher the word's
   * occurrence (e.g. <em>the</em> has rank = 1 while <em>triose</em>
   * has rank = 106295).
   */
  private final Map<String, Integer> dictionary;

  /**
   * ASCII-only alphabet (no diacritic/accent support).
   */
  static final Pattern ALPHABETIC = Pattern.compile("^[a-z]+$");

  /**
   * String array with a letter per element.
   */
  static final String[] LETTERS = "abcdefghijklmnopqrstuvwxyz".split("");

  /**
   * List of edits to be applied in tandem to each word split list
   * ("code as data").
   */
  static final List<Function<List<Split>, List<String>>> edits = List.of(
      SpellingCorrector::deletes,
      SpellingCorrector::transposes,
      SpellingCorrector::replaces,
      SpellingCorrector::inserts
  );

  /**
   * Constructor
   *
   * @param dictionary The word-to-rank dictionary to draw valid words from.
   */
  public SpellingCorrector(Map<String, Integer> dictionary) {
    checkNotNull(dictionary);
    checkArgument(!dictionary.isEmpty());
    this.dictionary = dictionary;
  }

  /**
   * Return one or more suggested corrections for a given word.
   * If the word is present in the dictionary then an <code>Optional.empty()</code>
   * is returned indicating no suggestions apply. If the word is <em>not</em>
   * present in the dictionary a (possibly empty) list  of suggested corrections is
   * returned.
   *
   * @param word The word to be validated against dictionary
   * @return <code>Optional.empty()</code> if the word is present in the dictionary
   * or an optional <code>List&lt;String&gt;</code> containing correction suggestions.
   * This list will contain no elements if a gibberish word is passed that resembles no
   * dictionary word.
   */
  public Optional<List<String>> getCorrections(String word) {

    // Ensure word format matches that of the dictionary: lowercase alphabetics
    String normalizedWord = normalize(word);

    // If word occurs in dictionary return no suggestions
    if (dictionary.containsKey(normalizedWord)) {
      return Optional.empty();
    }

    // The correction suggestions to be returned
    final Optional<List<String>> corrections;

    // Suggestions for one-edit typos: most typos contain just one error
    List<String> corrections1 = edits1(normalizedWord);

    // If edit1 yields no dictionary word, let's try with 2 edits.
    // Some typos stem from 2 errors; few come from more than 2
    if (corrections1.isEmpty()) {

      // Apply two-level dictionary word reconstitution
      List<String> corrections2 = edits2(normalizedWord);

      // No results even for 2 edits: return empty list
      if (corrections2.isEmpty()) {

        corrections = Optional.of(emptyList());
      } else {

        // edit2 did produce results, yay!
        corrections = Optional.of(corrections2);
      }
    } else {

      // edit1 did produce results, yay!
      corrections = Optional.of(corrections1);
    }

    // Return (possibly empty) list of suggested corrections
    return corrections;
  }

  /**
   * Locate one or more dictionary words reconstituted by (brute-force) applying
   * reversing edits to word (only once).
   *
   * @param typo The typo to use in regenerating dictionary words
   * @return The list of dictionary words reconstituted from typo
   */
  List<String> edits1(String typo) {

    // Generate all splits for the word so as to account for typos originating
    // in the insertion of a space in the middle of the word
    List<Split> wordSplits = splits(typo);

    // Generate and apply all 4 edits (in parallel) to each split. Packing removes
    // duplicates, ensures result presence in dictionary and orders by rank
    return pack(edits.parallelStream()
        .flatMap(edit -> edit.apply(wordSplits).stream()));
  }

  /**
   * Locate one or more dictionary words reconstituted by (brute-force) applying
   * reversing edits to nested list of words (apply edit1 two times).
   *
   * @param typo The typo to use in regenerating dictionary words
   * @return The (possibly empty) list of dictionary words reconstituted from typo
   */
  List<String> edits2(String typo) {
    // Repeatedly apply all 4 edits twice, and in parallel, to each split.
    // Packing removes duplicates, ensures result presence in dictionary and
    // orders by rank
    return
        pack(edits1(typo).parallelStream()
            .flatMap(w -> edits1(w).stream()));
  }

  /**
   * Pack results of dictionary word reconstitution by:
   * <ul>
   * <li>Coalescing duplicates</li>
   * <li>Filtering out non-dictionary words</li>
   * <li>Ordering by rank</li>
   * <li>Collecting as <code>List&lt;String&gt;</code></String></code></li>
   * </ul>
   *
   * @param editResults The (possibly empty) list of dictionary words reconstituted
   *                    from typo
   * @return The <code>List&lt;String&gt;</code> resulting from stream processing
   */
  List<String> pack(Stream<String> editResults) {
    return editResults
        // Remove duplicates
        .distinct()
        // Select only words present in dictionary
        .filter(dictionary::containsKey)
        // Sort by word rank so more frequent words show first
        .sorted(Comparator.comparing(dictionary::get))
        .collect(Collectors.toList());
  }

  /**
   * Generate all possible splits from a word. The first split has the
   * empty string on the left and the complete word on the right. The
   * last split contains the complete word on the left and the empty
   * string on the right. Intermediate splits contain everything in-between;
   * e.g. the first 4 letters on the left and the substring starting at the
   * 5th element on the right). This operation yields
   * <code>word.length() + 1</code> split pairs.
   *
   * @param word The word to build splits from
   * @return The list of left/right word splits
   */
  static List<Split> splits(String word) {
    return IntStream
        .rangeClosed(0, word.length()).boxed()
        .map(i -> {
          String left = word.substring(0, i);
          String right = word.substring(i);
          return new Split(left, right);
        })
        .collect(Collectors.toList());
  }

  /**
   * Generates all possible deletes from left/right pairs in one or
   * more splits. This split yields <code>word.length()</code> words.
   *
   * @param splits A list of left/right splits
   * @return The list of words resulting from 1-character deletions
   * applied to every split
   */
  static List<String> deletes(List<Split> splits) {
    return splits.stream()
        .filter(split -> !split.right.isEmpty())
        .map(split -> split.left + split.right.substring(1))
        .collect(Collectors.toList());
  }

  /**
   * Generates all possible transposes from left/right pairs in one or
   * more splits. This edit generates <code>name.length() - 1</code>
   * words.
   *
   * @param splits A list of left/right splits
   * @return The list of 1-character inversions applied to every split
   */
  static List<String> transposes(List<Split> splits) {
    return splits.stream()
        .filter(split -> split.right.length() > 1)
        .map(split ->
            split.left + split.right.substring(1, 2) +
                split.right.substring(0, 1) +
                split.right.substring(2))
        .collect(Collectors.toList());
  }

  /**
   * Generates all possible replaces from left/right pairs in one or more
   * splits by replacing each character with each letter in the alphabet.
   * This edit yields <code>LETTERS.length * name.length()</code> words.
   *
   * @param splits A list of left/right splits
   * @return The list of 1-character substitutions applied to every split
   */
  static List<String> replaces(List<Split> splits) {
    return splits.stream()
        .filter(split -> !split.right.isEmpty())
        .flatMap(split ->
            Arrays.stream(LETTERS).map(letter ->
                split.left + letter + split.right.substring(1)
            )
        )
        .collect(Collectors.toList());
  }

  /**
   * Generates all possible inserts from left/right pairs in one or more
   * splits by inserting every letter between every character in the word
   * splits. This prolific edit generates
   * <code>LETTERS.length * (word.length() + 1)</code> words.
   *
   * @param splits A list of left/right splits
   * @return The list of 1-letter substitutions applied to every split
   */
  static List<String> inserts(List<Split> splits) {
    return splits.stream()
        .flatMap(split ->
            Arrays.stream(LETTERS).map(letter ->
                split.left + letter + split.right
            )
        )
        .collect(Collectors.toList());
  }

  /**
   * Normalize incoming words by removing any surrounding whitespace, converting to
   * lower case and validating strict alphabetic composition.
   *
   * @param word The word to be normalized
   * @return The normalized word
   */
  static String normalize(String word) {
    checkNotNull(word);
    String normalizedWord = word.trim().toLowerCase();
    checkArgument(isAlphabetic(normalizedWord));
    return normalizedWord;
  }

  static boolean isAlphabetic(String word) {
    return ALPHABETIC.matcher(word).matches();
  }

  /**
   * Immutable data class embodying a left/right pair corresponding to a word split
   * at a given position.
   */
  static class Split {
    /**
     * The (possibly empty) left word fragment.
     */
    final String left;
    /**
     * The (possibly empty) right word fragment.
     */
    final String right;

    /**
     * The constructor.
     *
     * @param left  The left word fragment
     * @param right The right word fragment
     */
    public Split(String left, String right) {
      checkNotNull(left);
      checkNotNull(right);
      this.left = left;
      this.right = right;
    }

    /**
     * Check split equality (mostly for testing purposes).
     *
     * @param obj The object being compared against
     * @return Whether the other split has same left and right fragments
     */
    @Override
    public boolean equals(Object obj) {
      if (obj == null || !(obj instanceof Split)) {
        return false;
      }
      Split that = (Split) obj;
      return this.left.equals(that.left) && this.right.equals(that.right);
    }
  }
}
