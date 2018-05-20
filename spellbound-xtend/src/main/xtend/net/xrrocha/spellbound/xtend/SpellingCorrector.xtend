package net.xrrocha.spellbound.xtend

import java.util.List
import java.util.Map
import java.util.Optional
import java.util.regex.Pattern
import java.util.stream.Stream
import java.util.stream.StreamSupport
import org.eclipse.xtend.lib.annotations.Data

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Preconditions.checkNotNull
import static java.util.Collections.emptyList
import static java.util.stream.Collectors.toList

/**
 * Xtend implementation of PeterNorvig's
 * <a href='http://norvig.com/spell-correct.html'>Spelling Corrector</a>.
 * This implementation is based on Xtends's functional constructs.
 */
class SpellingCorrector {

  /**
   * The word-to-rank dictionary. The higher the rank the higher the word's
   * occurrence (e.g. <em>the</em> has rank <code>106295</code> while
   * <em>triose</em> has rank <code>1</code>).
   */
  val Map<String, Integer> dictionary

  /**
   * ASCII-only alphabet (no diacritic/accent support).
   */
  package static val ALPHABETIC = Pattern.compile('^[a-z]+$')

  /**
   * String array with a letter per element.
   */
  package static val LETTERS = 'abcdefghijklmnopqrstuvwxyz'.split('')

  /**
   * List of edits to be applied in tandem to each word split list
   * ('code as data').
   */
  val List<(Iterable<WordSplit>) => Iterable<String>> edits = #[
    [deletes(it)],
    [transposes(it)],
    [replaces(it)],
    [inserts(it)]
  ]
  
  /**
   * Constructor
   * 
   * @param dictionary The word-to-rank dictionary to draw valid words from.
   */
  new(Map<String, Integer> dictionary) {
    checkNotNull(dictionary);
    checkArgument(!dictionary.isEmpty);
    this.dictionary = dictionary
  }

  /**
   * Return one or more suggested corrections for a given word.
   * If the word is present in the dictionary then an
   * <code>Optional.empty</code> is returned indicating no suggestions
   * apply. If the word is <em>not</em> present in the dictionary a (possibly
   * empty) list  of suggested corrections is returned.
   * 
   * @param word The word to be validated against dictionary
   * @return <code>Optional.empty</code> if the word is present in the
   * dictionary or an optional <code>List&lt;String&gt;</code>
   * containing correction suggestions. This list will contain no
   * elements if a gibberish word is passed that resembles no
   * dictionary word.
   */
  def Optional<List<String>> getCorrections(String word) {
    // Ensure word format matches that of the dictionary: lowercase alphabetics
    val normalizedWord = normalize(word)

    // If word occurs in dictionary return no suggestions
    if (dictionary.containsKey(normalizedWord)) {
      Optional.empty

    } else { // Word is not present in dictionary
    // Suggestions for one-edit typos: most typos contain just one error
      val corrections1 = edits1(normalizedWord)

      // Assign the correction suggestions to be returned
      val corrections = if (!corrections1.isEmpty) {
          // edit1 did produce results, yay!
          corrections1
        } else {
          // edits1 yielded no dictionary words; let's try with edits2.
          // Some typos stem from 2 errors; few come from more than 2
          // Apply two-level dictionary word reconstitution
          val corrections2 = edits2(normalizedWord)

          // No results even for 2 edits: return empty list
          if (!corrections2.isEmpty) {
            // edit2 did produce results, yay!
            corrections2
          } else {
            emptyList
          }
        }

      // Return (possibly empty) list of suggested corrections
      Optional.of(corrections)
    }
  }

  /**
   * Locate one or more dictionary words reconstituted by (brute-force) applying
   * reversing edits to word (only once).
   * 
   * @param typo The typo to use in regenerating dictionary words
   * @return The list of dictionary words reconstituted from typo
   */
  def List<String> edits1(String typo) {

    // Generate all splits for the word so as to account for typos originating
    // in the insertion of a space in the middle of the word
    val wordSplits = splits(typo)

    // Generate and apply all 4 edits (in parallel) to each split. Packing removes
    // duplicates, ensures result presence in dictionary and orders by rank
    return edits.parallelStream.flatMap[it.apply(wordSplits).stream].pack
  }

  /**
   * Locate one or more dictionary words reconstituted by (brute-force) applying
   * reversing edits to nested list of words (apply edit1 two times).
   * 
   * @param typo The typo to use in regenerating dictionary words
   * @return The (possibly empty) list of dictionary words re-created from typo
   */
  def List<String> edits2(String typo) {
    // Repeatedly apply all 4 edits twice, and in parallel, to each split.
    // Packing removes duplicates, ensures result presence in dictionary and
    // orders by rank
    edits1(typo).parallelStream.flatMap[edits1(it).stream].pack
  }

  /**
   * Generates all possible deletes from left/right pairs in one or
   * more splits. This split yields <code>word.length</code> words.
   * 
   * @param splits A list of left/right splits
   * @return The list of words resulting from 1-character deletions
   * applied to every split
   */
  static def Iterable<String> deletes(Iterable<WordSplit> splits) {
    splits
    .filter[!it.wordRight.isEmpty]
    .map[it.wordLeft + it.wordRight.substring(1)]
  }

  /**
   * Generates all possible transposes from left/right pairs in one or
   * more splits. This edit generates <code>name.length - 1</code>
   * words.
   * 
   * @param splits A list of left/right splits
   * @return The list of 1-character inversions applied to every split
   */
  static def Iterable<String> transposes(Iterable<WordSplit> splits) {
    splits
    .filter[it.wordRight.length > 1]
    .map [
      it.wordLeft + it.wordRight.substring(1, 2) + it.wordRight.substring(0, 1) + it.wordRight.substring(2)
    ]
  }

  /**
   * Generates all possible replaces from left/right pairs in one or more
   * splits by replacing each character with each letter in the alphabet.
   * This edit yields <code>LETTERS.length * name.length</code> words.
   * 
   * @param splits A list of left/right splits
   * @return The list of 1-character substitutions applied to every split
   */
  static def Iterable<String> replaces(Iterable<WordSplit> splits) {
    splits
    .filter[!it.wordRight.isEmpty]
    .flatMap [ split |
      LETTERS.map[letter|split.wordLeft + letter + split.wordRight.substring(1)]
    ]
  }

  /**
   * Generates all possible inserts from left/right pairs in one or more
   * splits by inserting every letter between every character in the word
   * splits. This prolific edit generates
   * <code>LETTERS.length * (word.length + 1)</code> words.
   * 
   * @param splits A list of left/right splits
   * @return The list of 1-letter substitutions applied to every split
   */
  static def Iterable<String> inserts(Iterable<WordSplit> splits) {
    splits
    .flatMap[split|LETTERS.map[letter|split.wordLeft + letter + split.wordRight]]
  }

  /**
   * Normalize incoming words by removing any surrounding whitespace, converting
   * to lower case and validating strict alphabetic composition.
   * 
   * @param word The word to be normalized
   * @return The normalized word
   */
  static def String normalize(String word) {
    checkNotNull(word)
    val normalizedWord = word.trim.toLowerCase
    checkArgument(normalizedWord.isAlphabetic)
    normalizedWord
  }

  static def boolean isAlphabetic(String word) {
    ALPHABETIC.matcher(word).matches
  }

  /**
   * Generate all possible splits from a word. The first split has the
   * empty string on the left and the complete word on the right. The
   * last split contains the complete word on the left and the empty
   * string on the right. Intermediate splits contain everything in-between
   * e.g. the first 4 letters on the left and the substring starting at the
   * 5th element on the right). This operation yields
   * <code>word.length + 1</code> split pairs.
   * 
   * @param word The word to build splits from
   * @return The list of left/right word splits
   */
  static def Iterable<WordSplit> splits(String word) {
    (0 .. word.length).map [
      new WordSplit(word.substring(0, it), word.substring(it))
    ]
  }

  /**
   * Pack results of dictionary word reconstitution by:
   * <ul>
   * <li>Coalescing duplicates</li>
   * <li>Filtering out non-dictionary words</li>
   * <li>Ordering (descending) by rank</li>
   * <li>Collecting as <code>List&lt;String&gt;</code></li>
   * </ul>
   * 
   * @param editResults The (possibly empty) list of dictionary words reconstituted
   *                    from typo
   * @return The <code>List&lt;String&gt;</code> resulting from stream processing
   */
  private def List<String> pack(Stream<String> editResults) {
    editResults
    // Remove duplicates
    .distinct 
    // Select only words present in dictionary
    .filter[dictionary.containsKey(it)] 
    // Sort descending by word rank so more frequent words show first
    .sorted[word1, word2|dictionary.get(word2).compareTo(dictionary.get(word1))]
    .collect(toList)
  }

  /**
   * Converts an <code>Iterable&lt;T&gt;</code> to a sequential <code>Stream&lt;T&gt;</code>
   */
  def <T> Stream<T> stream(Iterable<T> iterable) {
    StreamSupport.stream(iterable.spliterator, false)
  }

/**
 * Data class holding two segments of a word.
 */
  @Data
  static class WordSplit {
    val String wordLeft
    val String wordRight
  }
}
