package net.xrrocha.spellbound.scala

import net.xrrocha.spellbound.scala.SpellingCorrector.{normalize, _}

import scala.io.Source

/**
  * Exercise Norvig spelling corrector by passing a dictionary filename and zero
  * or more textual content filenames.
  */
object Main {

  /**
    * The dictionary file must be a tab-delimited text file where the first field
    * contains a valid word and the second one the word's ranking. The lower the
    * ranking, the more commonly used the word. Thus, for instance, ''the''
    * has rank `1` while ''triose'' has rank `106295`.
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
    * {{{adios,radios,audios,agios,ados,adits,aido}}}
    *
    * @param args The command-line arguments containing a dictionary filename and zero
    *             or more textual content filenames.
    */
  def main(args: Array[String]): Unit = {

    if (args.isEmpty) {
      onError("Usage: Main <dictionaryFilename> [ file1 file2 ... ]")
      throw new IllegalStateException("Return from System.exit(), df?")
    }

    // The first argument points to the file containing a tab-delimited
    // (word/rank) dictionary
    val dictionaryFilename = args.head

    // Create a (possibly empty) list of filenames to process
    val filenames = args.tail

    // Create an iterable of lines from the input files (or the
    // operating system's standard input)
    val inputLines = loadInputLines(filenames)

    // Load the dictionary from the given file
    val dictionary = loadDictionary(loadFileLines(dictionaryFilename))
    // Create a spelling corrector instance from the dictionary
    val spellingCorrector = SpellingCorrector(dictionary)

    // Extract words and their corrections onto standard output
    processInputLines(inputLines, spellingCorrector)
      .foreach(println)
  }

  /**
    * Consume a stream of textual lines extracting typos (i.e., words not present
    * in the passed `spellingCorrector`'s dictionary) and generating a
    * list of correction suggestions per typo. Each typo/suggestion list is then
    * passed to a user-provided consumer for use case-specific processing.
    *
    * @param inputLines        The stream of lines to be parsed and validated
    * @param spellingCorrector The spelling corrector used to yield suggestions
    */
  def processInputLines(inputLines: Iterable[String],
                        spellingCorrector: SpellingCorrector): Iterable[String] = {

    inputLines
      // Split lines into space-delimited words
      .flatMap(_.split("\\s+"))
      .toSeq
      // Remove duplicates
      .distinct
      // Weed out non-alphabetic words
      .filter(isAlphabetic)
      // Generate suggestions for each word
      .map(word => word -> spellingCorrector.getCorrections(word))
      // Suppress non-typo, in-dictionary words having no suggestions
      .filter { case (_, corrections) => corrections.isDefined }
      // Build word/suggestion tab-separated line
      .map { case (word, corrections) => word + "\t" + corrections.get.mkString(",") }
  }

  /**
    * Transform a stream of filenames into a stream of lines drawn for each file in
    * turn. Files are read in stream order. If the filename stream is empty,
    * fallback to the operating system's standard input.
    *
    * @param filenames The (possibly empty) list of filenames
    * @return The concatenated stream of lines
    */
  def loadInputLines(filenames: Iterable[String]): Iterable[String] = {
    if (filenames.isEmpty) Source.fromInputStream(System.in).getLines().toSeq
    else filenames.flatMap(loadFileLines)
  }

  def loadFileLines(filename: String): Iterable[String] =
    Source.fromFile(filename).getLines().toSeq

  /**
    * Transform a stream of filenames into a stream of lines drawn for each file in
    * turn. Files are read in stream order. If the filename stream is empty,
    * fallback to the operating system's standard input.
    *
    * @param filenames The (possibly empty) list of filenames
    * @return The concatenated stream of lines
    */
  def createInputLineStream(filenames: Iterable[String]): Iterable[String] = {
    if (filenames.isEmpty) {
      Source.fromInputStream(System.in).getLines().toSeq
    }
    else filenames.flatMap {
      Source.fromFile(_).getLines().toSeq
    }
  }

  /**
    * Read, parse and build a dictionary from a stream of tab-delimited lines.
    *
    * @param lines The lines containing a word/rank pair each
    * @return The resulting of word-to-rank mappings
    */
  def loadDictionary(lines: Iterable[String]): Map[Word, Rank] =
    lines
      .par
      .map { line =>
        val Array(word, rank) = line.split("\\t", 2)
        normalize(word) -> rank.toInt
      }
      .toMap
      .seq

  /**
    * Print an error message on the operating system's standard error and exit
    * program abnormally.
    *
    * @param message The error message to be printed
    */
  def onError(message: String): Unit = {
    System.err.println(message)
    System.exit(1)
  }
}
