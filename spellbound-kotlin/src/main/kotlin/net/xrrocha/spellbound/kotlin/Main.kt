package net.xrrocha.spellbound.kotlin

import net.xrrocha.spellbound.kotlin.SpellingCorrector.Companion.isAlphabetic
import java.io.File

/**
 * Exercise Norvig spelling corrector by passing a dictionary filename and zero
 * or more textual content filenames.
 *
 * The dictionary file must be a tab-delimited text file where the first field
 * contains a valid word and the second one the word's ranking. The lower the
 * ranking, the more commonly used the word. Thus, for instance, <em>the</em>
 * has rank <code>1</code> while <em>triose</em> has rank <code>106295</code>.
 *
 * Command-line arguments following the dictionary filename must point to
 * textual files whose words are to be validated. These files are concatenated
 * and processed as a whole. If no content filenames are provided, the operating
 * system's standard input will be used.
 *
 * Results are always produced onto the operating system's standard output. Each
 * tab-delimited output line contains a typo and a comma-separated list of
 * correction suggestions. For example:
 *
 * > `adios,radios,audios,agios,ados,adits,aido`
 *
 * @param args The command-line arguments containing a dictionary filename and zero
 *             or more textual content filenames.
 */
fun main(args: Array<String>) {

  if (args.isEmpty()) {
    onError("Usage: Main <dictionaryFilename> [ file1 file2 ... ]")
    throw IllegalStateException("Return from System.exit(), df?")
  }

  // The first argument points to the file containing a tab-delimited
  // (word/rank) dictionary
  val dictionaryFile = File(args[0])
  require(dictionaryFile.isFile && dictionaryFile.canRead())

  // Create a (possibly empty) list of filenames to process
  val filenames = args.sliceArray(1..args.size).toList()

  // Create an iterable of lines from the input files (or the
  // operating system's standard input)
  val inputLines = createInputLineStream(filenames)

  // Load the dictionary from the given file
  val dictionary = loadDictionary(dictionaryFile.readLines())
  // Create a spelling corrector instance from the dictionary
  val spellingCorrector = SpellingCorrector(dictionary)

  // Extract words and their corrections onto standard output
  processInputLines(inputLines, spellingCorrector).forEach { println(it) }
}

/**
 * Consume a stream of textual lines extracting typos (i.e., words not present
 * in the passed `spellingCorrector`'s dictionary) and generating a
 * list of correction suggestions per typo. Each typo/suggestion list is then
 * passed to a user-provided consumer for use case-specific processing.
 *
 * @param inputLines        The stream of lines to be parsed and validated
 * @param spellingCorrector The spelling corrector used to yield suggestions
 * @param process           The user-supplied lambda to process typos
 */
internal fun processInputLines(inputLines: Iterable<String>,
                               spellingCorrector: SpellingCorrector): Iterable<String> {

  return inputLines
      // Split lines into space-delimited words
      .flatMap { it.split("\\s+".toRegex()) }
      // Remove duplicates
      .distinct()
      // Weed out non-alphabetic words
      .filter { it.isAlphabetic() }
      // Generate suggestions for each word
      .map { word ->
        word to spellingCorrector.getCorrections(word)
      }
      // Suppress non-typo, in-dictionary words having no suggestions
      .filter { (_, corrections) -> corrections != null }
      // Build word/suggestion tab-separated line
      .map { (word, corrections) -> word + "\t" + corrections!!.joinToString(",") }
}

/**
 * Transform a stream of filenames into a stream of lines drawn for each file in
 * turn. Files are read in stream order. If the filename stream is empty,
 * fallback to the operating system's standard input.
 *
 * @param filenames The (possibly empty) list of filenames
 * @return The concatenated stream of lines
 */
internal fun createInputLineStream(filenames: Iterable<String>): Iterable<String> {
  return if (filenames.none()) System.`in`.bufferedReader().readLines()
  else filenames.flatMap { File(it).readLines() }
}

/**
 * Read, parse and build a dictionary from a stream of tab-delimited lines.
 *
 * @param lines A stream of lines containing a word/rank pair per line
 * @return The resulting of word-to-rank mappings
 */
internal fun loadDictionary(lines: Iterable<String>): Map<String, Int> {
  return lines
      .map { line ->
        // Split tab-delimited line into fields
        val fields = line.split("\\t".toRegex(), 2)
        // First field contains word
        val word = fields[0]
        // Second field contains rank
        val rank = fields[1].toInt()
        // Return word/rank pair as a <code>Map.Entry<String, Integer></code>
        word to rank
      }
      // Skip invalid words or ranks
      .filter { (word, rank) -> word.isAlphabetic() && rank > 0 }
      .toMap()
}

/**
 * Print an error message on the operating system's standard error and exit
 * program abnormally.
 *
 * @param message The error message to be printed
 */
internal fun onError(message: String) {
  System.err.println(message)
  System.exit(1)
}
