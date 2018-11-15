package net.xrrocha.spellbound.java;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Java implementation of PeterNorvig's
 * <a href="http://norvig.com/spell-correct.html">Spelling Corrector</a>.
 * This implementation is based on Java's functional constructs.
 */
public class SpellingCorrector {

    /**
     * The word-to-rank dictionary. The higher the rank the higher the word's
     * occurrence (e.g. <em>the</em> has rank <code>106295</code> while
     * <em>triose</em> has rank <code>1</code>).
     */
    private final Map<String, Integer> dictionary;

    /**
     * Alphabetics only.
     */
    private static final Pattern ALPHABETIC = Pattern.compile("^[\\p{Alpha}]+$");

    /**
     * String array with a letter per element.
     */
    static final String[] LETTERS = "abcdefghijklmnopqrstuvwxyz".split("");

    /**
     * List of edits to be applied in tandem to each word split list
     * ("code as data").
     */
    private static final List<Function<List<WordSplit>, Stream<String>>> edits =
        List.of(
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
     * If the word is present in the dictionary then an
     * <code>Optional.empty()</code> is returned indicating no suggestions
     * apply. If the word is <em>not</em> present in the dictionary a (possibly
     * empty) list  of suggested corrections is returned.
     *
     * @param word The word to be validated against dictionary
     * @return <code>Optional.empty()</code> if the word is present in the
     * dictionary or an optional <code>List&lt;String&gt;</code>
     * containing correction suggestions. This list will contain no
     * elements if a gibberish word is passed that resembles no
     * dictionary word.
     */
    public Optional<List<String>> getCorrections(String word) {

        // Ensure word format matches that of the dictionary: lowercase alphabetics
        var normalizedWord = normalize(word);

        // If word occurs in dictionary then return no suggestions
        if (dictionary.containsKey(normalizedWord)) {
            return Optional.empty();
        }

        // Corrections for one-edit typos; most typos contain just one error.
        // Method known removes duplicates, ensures presence in dictionary
        // and orders by rank
        var corrections = known(edits1(normalizedWord));

        // If edit1 yields no in-dictionary word, try with edits2.
        // Some typos stem from 2 errors; few come from more than 2
        if (corrections.isEmpty()) {
            corrections = known(edits2(normalizedWord));
        }

        // Return (possibly empty) list of suggested corrections
        return Optional.of(corrections);
    }

    /**
     * Locate one or more dictionary words reconstituted by (brute-force) applying
     * reversing edits to word (only once).
     *
     * @param typo The typo to use in regenerating dictionary words
     * @return The list of dictionary words reconstituted from typo
     */
    static Stream<String> edits1(String typo) {

        // Generate all wordSplits for typo
        var wordSplits = wordSplits(typo);

        // Generate and apply all 4 edits (in parallel) to each split
        return edits.parallelStream().flatMap(edit -> edit.apply(wordSplits));
    }

    /**
     * Locate one or more dictionary words reconstituted by (brute-force) applying
     * reversing edits to nested list of words (apply edit1 two times).
     *
     * @param typo The typo to use in regenerating dictionary words
     * @return The (possibly empty) list of dictionary words re-created from typo
     */
    static Stream<String> edits2(String typo) {

        // Apply all 4 edits twice, and in parallel, to each split
        return edits1(typo).flatMap(SpellingCorrector::edits1);
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
    List<String> known(Stream<String> editResults) {
        return editResults
            // Remove duplicates
            .distinct()
            // Select only words present in dictionary
            .filter(dictionary::containsKey)
            // Sort descending by word rank so more frequent words show first
            .sorted((word1, word2) -> dictionary.get(word2).compareTo(dictionary.get(word1)))
            .collect(toList());
    }

    /**
     * Generate all possible wordSplits from a word. The first split has the
     * empty string on the left and the complete word on the right. The
     * last split contains the complete word on the left and the empty
     * string on the right. Intermediate wordSplits contain everything in-between;
     * e.g. the first 4 letters on the left and the substring starting at the
     * 5th element on the right). This operation yields
     * <code>word.length() + 1</code> split pairs.
     *
     * @param word The word to build wordSplits from
     * @return The list of left/right word wordSplits
     */
    static List<WordSplit> wordSplits(String word) {
        return IntStream
            .rangeClosed(0, word.length()).boxed()
            .map(i -> {
                String left = word.substring(0, i);
                String right = word.substring(i);
                return new WordSplit(left, right);
            })
            .collect(toList());
    }

    /**
     * Generates all possible deletes from left/right pairs in one or
     * more wordSplits. This split yields <code>word.length()</code> words.
     *
     * @param splits A list of left/right wordSplits
     * @return The list of words resulting from 1-character deletions
     * applied to every split
     */
    static Stream<String> deletes(List<WordSplit> splits) {
        return splits.stream()
            .filter(split -> !split.right.isEmpty())
            .map(split -> split.left + split.right.substring(1));
    }

    /**
     * Generates all possible inserts from left/right pairs in one or more
     * wordSplits by inserting every letter between every character in the word
     * wordSplits. This prolific edit generates
     * <code>LETTERS.length * (word.length() + 1)</code> words.
     *
     * @param splits A list of left/right wordSplits
     * @return The list of 1-letter substitutions applied to every split
     */
    static Stream<String> inserts(List<WordSplit> splits) {
        return splits.stream()
            .flatMap(split ->
                         Arrays.stream(LETTERS).map(letter ->
                                                        split.left + letter + split.right));
    }

    /**
     * Generates all possible transposes from left/right pairs in one or
     * more wordSplits. This edit generates <code>name.length() - 1</code>
     * words.
     *
     * @param splits A list of left/right wordSplits
     * @return The list of 1-character inversions applied to every split
     */
    static Stream<String> transposes(List<WordSplit> splits) {
        return splits.stream()
            .filter(split -> split.right.length() > 1)
            .map(split ->
                     split.left +
                         split.right.substring(1, 2) +
                         split.right.substring(0, 1) +
                         split.right.substring(2));
    }

    /**
     * Generates all possible replaces from left/right pairs in one or more
     * wordSplits by replacing each character with each letter in the alphabet.
     * This edit yields <code>LETTERS.length * name.length()</code> words.
     *
     * @param splits A list of left/right wordSplits
     * @return The list of 1-character substitutions applied to every split
     */
    static Stream<String> replaces(List<WordSplit> splits) {
        return splits.stream()
            .filter(split -> !split.right.isEmpty())
            .flatMap(split ->
                         Arrays.stream(LETTERS).map(letter ->
                                                        split.left + letter + split.right.substring(1)));
    }

    /**
     * Normalize incoming words by removing any surrounding whitespace, converting
     * to lower case and validating strict alphabetic composition.
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

    /**
     * Verify whether a string is alphabetic.
     *
     * @param word The string to be tested
     * @return whether the string is alphabetic
     */
    static boolean isAlphabetic(String word) {
        return ALPHABETIC.matcher(word).matches();
    }

    /**
     * Make a sequential <code>Stream&lt;T&gt;</code> from an <code>Iterable&lt;T&gt;</code>.
     *
     * @param iterable The iterable to be rephrased as a stream
     * @param <T>      The generic type of the given iterable and the resulting stream
     * @return The resulting stream
     */
    static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    /**
     * Immutable data class embodying a left/right pair corresponding to a word
     * split at a given position.
     */
    static class WordSplit {
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
        WordSplit(String left, String right) {
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
            if (!(obj instanceof WordSplit)) {
                return false;
            }
            var that = (WordSplit) obj;
            return this.left.equals(that.left) && this.right.equals(that.right);
        }
    }
}
