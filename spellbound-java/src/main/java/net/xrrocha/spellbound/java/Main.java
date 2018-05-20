package net.xrrocha.spellbound.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

/**
 * Exercise Norvig spelling corrector by passing a dictionary filename and zero
 * or more textual content filenames.
 */
public class Main {

  /**
   * <p>
   * The dictionary file must be a tab-delimited text file where the first field
   * contains a valid word and the second one the word's ranking. The lower the
   * ranking, the more commonly used the word. Thus, for instance, <em>the</em>
   * has rank <code>1</code> while <em>triose</em> has rank <code>106295</code>.
   * </p>
   * <p>
   * Command-line arguments following the dictionary filename must point to
   * textual files whose words are to be validated. These files are concatenated
   * and processed as a whole. If no content filenames are provided, the operating
   * system's standard input will be used.
   * </p>
   * <p>
   * Results are always produced onto the operating system's standard output. Each
   * tab-delimited output line contains a typo and a comma-separated list of
   * correction suggestions. For example:
   * </p>
   * <blockquote>
   *
   * <pre>
   * <code>adios,radios,audios,agios,ados,adits,aido</code>
   * </pre>
   *
   * </blockquote>
   *
   * @param args The command-line arguments containing a dictionary filename and zero
   *             or more textual content filenames.
   */
  public static void main(String[] args) {

    if (args.length < 1) {
      onError("Usage: " + Main.class.getName() + " <dictionaryFilename> [ file1 file2 ... ]");
      throw new IllegalStateException("Return from System.exit(), df?");
    }

    // The first argument points to the file containing a tab-delimited
    // (word/rank) dictionary
    var dictionaryFilename = args[0];

    // Create a (possibly empty) stream of filenames to process
    var filenames = Arrays.stream(args, 1, args.length);
    // Create a lazily-collected stream of lines from the input files (or the
    // operating system's standard input)
    Stream<String> inputLines = loadInputLines(filenames);

    try {

      // Load the dictionary from the given file
      var dictionary = loadDictionary(getLinesFrom(dictionaryFilename));
      // Create a spelling corrector instance from the dictionary
      var spellingCorrector = new SpellingCorrector(dictionary);

      // Extract & validate to suggest words onto tab-delimited standard output
      processInputLines(
          inputLines,
          spellingCorrector,
          (word, suggestions) -> word + "\t" + suggestions.stream().collect(joining(",")))
          .forEach(System.out::println);

    } catch (Exception e) {
      onError("Unexpected error: " + e.toString());
    }
  }

  /**
   * Consume a stream of textual lines extracting typos (i.e., words not present
   * in the passed <code>spellingCorrector</code>'s dictionary) and generating a
   * list of correction suggestions per typo. Each typo/suggestion list is then
   * passed to a user-provided consumer for use case-specific processing.
   *
   * @param inputLines        The stream of lines to be parsed and validated
   * @param spellingCorrector The spelling corrector used to yield suggestions
   * @param process           The user-supplied lambda to process typos
   */
  static <T> Stream<T> processInputLines(Stream<String> inputLines, SpellingCorrector spellingCorrector,
                                         BiFunction<String, List<String>, T> process) {

    return inputLines
        // Split lines into space-delimited words
        .flatMap(line -> Arrays.stream(line.split("\\s+")))
        // Filter only strictly ascii-alphabetic words
        .filter(SpellingCorrector::isAlphabetic)
        // Remove duplicates
        .distinct()
        // Generate suggestions for each word
        .map(word -> {
          List<String> corrections = spellingCorrector.getCorrections(word).stream()
              .flatMap(Collection::stream)
              .collect(toList());
          return new SimpleEntry<>(word, corrections);
        })
        // Suppress non-typo, dictionary words having no suggestions
        .filter(entry -> !entry.getValue().isEmpty())
        // Pass word/suggestions p!entry.getValue().isEmpty()air to user-supplied lambda
        .map(entry -> process.apply(entry.getKey(), entry.getValue()));
  }

  /**
   * Transform a stream of filenames into a stream of lines drawn for each file in
   * turn. Files are read in stream order. If the filename stream is empty,
   * fallback to the operating system's standard input.
   *
   * @param filenames The (possibly empty) list of filenames
   * @return The concatenated stream of lines
   */
  static Stream<String> loadInputLines(Stream<String> filenames) {
    return filenames
        // Pass each filename to the <code>getLinesFrom</code> method so as to
        // convert it to a stream of lines
        .map(Main::getLinesFrom)
        // Coalesce all input streams into one
        .reduce(Stream::concat)
        // On empty stream fall back to operating system's standard input
        .orElseGet(() -> new BufferedReader(new InputStreamReader(System.in)).lines());
  }

  /**
   * Read, parse and build a dictionary from a stream of tab-delimited lines.
   *
   * @param lines A stream of lines containing a word/rank pair per line
   * @return The resulting of word-to-rank mappings
   */
  static Map<String, Integer> loadDictionary(Stream<String> lines) {
    return lines
        .map(line -> {
          // Split tab-delimited line into fields
          var fields = line.split("\\t", 2);
          // First field contains word
          var word = fields[0];
          // Second field contains rank
          var rank = Integer.parseInt(fields[1]);
          // Return word/rank pair as a <code>Map.Entry<String, Integer></code>
          return new SimpleEntry<>(word, rank);
        })
        // Skip invalid words or ranks
        .filter(entry -> SpellingCorrector.isAlphabetic(entry.getKey()) && entry.getValue() > 0)
        // Convert stream of word/rank entries into a map
        .collect(toMap(Entry::getKey, Entry::getValue));
  }

  /**
   * Given its name, open a text file as a stream of lines.
   *
   * @param filename A name pointing to a text file
   * @return The stream of lines contained in the named file
   */
  static Stream<String> getLinesFrom(String filename) {
    try {
      // Convert filename to path
      var path = FileSystems.getDefault().getPath(filename);
      // Create stream of lines from path
      return Files.lines(path);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  /**
   * Print an error message on the operating system's standard error and exit
   * program abnormally.
   *
   * @param message The error message to be printed
   */
  static void onError(String message) {
    System.err.println(message);
    System.exit(1);
  }
}
