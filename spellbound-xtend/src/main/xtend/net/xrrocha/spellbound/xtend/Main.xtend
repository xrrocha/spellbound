package net.xrrocha.spellbound.xtend

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.FileSystems
import java.nio.file.Files
import java.util.List
import java.util.Map

import static java.util.stream.Collectors.toList

import static extension java.lang.Integer.parseInt
import static extension net.xrrocha.spellbound.xtend.SpellingCorrector.isAlphabetic

/**
 * Exercise Norvig spelling corrector by passing a dictionary filename and
 * zero or more textual content filenames.
 */
class Main {

  /**
   * <p>
   * The dictionary file must be a tab-delimited text file where the first
   * field contains a valid  word and the second one the word's ranking.
   * The lower the ranking, the more commonly used the word. Thus, for
   * instance, <em>the</em> has rank <code>1</code> while <em>triose</em>
   * has rank <code>106295</code>.
   * </p>
   * <p>
   * Command-line arguments following the dictionary filename must point
   * to textual files whose words are to be validated. These files are
   * concatenated and processed as a whole. If no content filenames are
   * provided, the operating system's standard input will be used.
   * </p>
   * <p>
   * Results are always produced onto the operating system's standard output.
   * Each tab-delimited output line contains a typo and a comma-separated
   * list of correction suggestions. For example:
   * </p>
   * <blockquote>
   * <pre><code>adios,radios,audios,agios,ados,adits,aido</code></pre>
   * </blockquote>
   * 
   * @param args The command-line arguments containing a dictionary filename
   *             and zero or more
   *             textual content filenames.
   */
  static def main(String... args) {

    if (args.length < 1) {
      onError('''Usage: «Main.name» <dictionaryFilename> [ file1 file2 ... ]''')
      throw new IllegalStateException('Return from System.exit(), df?')
    }

    // The first argument points to the file containing a tab-delimited
    // (word/rank) dictionary
    val dictionaryFilename = args.head

    // Create a (possibly empty) iterable of filenames to process
    val filenames = args.tail

    // Create an iterable of lines from the input files
    // (or the operating system's standard input)
    val inputLines = loadInputLines(filenames)

    try {

      // Load the dictionary from the given file
      val dictionary = loadDictionary(getLinesFrom(dictionaryFilename))
      
      // Create a spelling corrector instance from the dictionary
      val spellingCorrector = new SpellingCorrector(dictionary)

      // Extract & validate to suggest words onto tab-delimited standard output
      processInputLines(
        inputLines, 
        spellingCorrector, 
        [word, suggestions | word + '\t' + suggestions.join(',')]  
      )
      .forEach[println(it)]

  } catch (Exception e) {
      onError('''Unexpected error: «e»''')
    }
  }

  /**
   * <p>
   * Consume an iterable of textual lines extracting typos (i.e., words not
   * present in the passed <code>spellingCorrector</code>'s dictionary) and
   * generating a list of correction suggestions per typo. Each
   * typo/suggestion list is then passed to a user-provided consumer for use
   * case-specific processing.
   * </p>
   * 
   * @param inputLines        The iterable of lines to be parsed and
   *                          validated
   * @param spellingCorrector The spelling corrector used to yield suggestions
   * @param process           The user-supplied lambda to process typos
   */
  static def <T> Iterable<T> processInputLines(
    Iterable<String> inputLines, 
    SpellingCorrector spellingCorrector,
    (String, List<String>) => T process) {

    inputLines 
      // Split lines into space-delimited words
      .flatMap[split('\\s+').toList]
      // Filter only strictly ascii-alphabetic words
      .filter[isAlphabetic] 
      // Remove duplicates
      .toSet
      // Generate suggestions for each word
      .map [it -> spellingCorrector.getCorrections(it)] 
      // Suppress non-typo, dictionary words having no suggestions
      .filter[value.isPresent]
      // Pass word/suggestions pair to user-supplied lambda
      .map[process.apply(key, value.get)]
  }

  /**
   * Transform an iterableof filenames into an iterableof lines drawn for
   * each file in turn. Files are read in iterable order. If the filename iterable
   * is empty, fallback to the operating system's standard input.
   * 
   * @param filenames The (possibly empty) list of filenames
   * @return The concatenated iterable of lines
   */
  static def Iterable<String> loadInputLines(Iterable<String> filenames) {
    if (filenames.empty) {
      // Use stdin if no file names specified
      new BufferedReader(new InputStreamReader(System.in)).lines.collect(toList)
    } else {
    // Pass each filename to the <code>getLinesFrom</code> method so as to
    // convert it to an iterableof lines
    filenames.flatMap[getLinesFrom] // Coalesce all input iterables into one
    }
  }

  /**
   * Read, parse and build a dictionary from an iterableof tab-delimited
   * lines.
   * 
   * @param lines an iterableof lines containing a word/rank pair per line
   * @return The resulting of word-to-rank mappings
   */
  static def Map<String, Integer> loadDictionary(Iterable<String> lines) {
    lines.map [
      // Split tab-delimited line into fields
      val fields = split('\\t', 2)
      // First field contains word
      val word = fields.head
      // Second field contains rank
      val rank = fields.get(1).parseInt
      // Return word/rank pair as a <code>Pair<String, Integer></code>
      word -> rank
    ]
    // Skip invalid words or ranks
    .filter[key.isAlphabetic && value > 0]
    // Convert iterable of word/rank entries into a map
    .toMap[key]
    .mapValues[value]
  }

  /**
   * Given its name, open a text file as an iterableof lines.
   * 
   * @param filename A name pointing to a text file
   * @return The iterable of lines contained in the named file
   */
  static def Iterable<String> getLinesFrom(String filename) {
      // Convert filename to path
      val path = FileSystems.getDefault.getPath(filename)
      // Create iterable of lines from path
      Files.readAllLines(path)
  }

  /**
   * Print an error message on the operating system's standard error and exit
   * program abnormally.
   * 
   * @param message The error message to be printed
   */
  static def onError(String message) {
    System.err.println(message)
    System.exit(1)
  }
}
