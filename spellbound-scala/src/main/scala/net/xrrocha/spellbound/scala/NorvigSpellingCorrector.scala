package net.xrrocha.spellbound.scala

import java.io.{File, FileInputStream, InputStream}

import com.typesafe.scalalogging.StrictLogging

import scala.io.Source

case class NorvigSpellingCorrector(dictionary: Map[Word, Rank]) {
  def getCorrectionsFor(word: Word): Option[Seq[Word]] =
    NorvigSpellingCorrector.getCorrectionsFor(word, dictionary)
}

object NorvigSpellingCorrector extends StrictLogging {

  def apply(file: File): NorvigSpellingCorrector =
    apply(new FileInputStream(file))

  def apply(is: InputStream): NorvigSpellingCorrector = {
    val dictionary = loadDictionary(is)
    logger.debug(s"Loaded ${dictionary.size} word frequencies")
    new NorvigSpellingCorrector(dictionary)
  }

  def normalizeWord(word: Word): Word = word.trim.toLowerCase

  def getCorrectionsFor(word: Word, dictionary: Map[Word, Rank]): Option[Seq[Word]] = {

    val normalizedWord = normalizeWord(word)

    if (dictionary.contains(normalizedWord)) {
      // Curated dictionary word; no corrections needed
      None
    } else { // Try and find typo-originating word

      val corrections = {
        // Filters out words not present in dictionary, sorts descendingly by rank
        def known(normalizedWords: Seq[Word]) = {
          normalizedWords
            .filter(dictionary.contains)
            .sortBy(-dictionary(_)) // reverse by rank
        }

        // Dictionary words occurring in edits1() results
        val editResults1: Seq[String] = known(edits1(normalizedWord))
        if (editResults1.nonEmpty) {
          logger.debug(s"edits1: ${editResults1.length}")
          editResults1
        } else { // Try edit2() only if edits1() is empty
          // Dictionary words occurring in edits2() results. May be empty!
          val editResults2: Seq[String] = known(edits2(normalizedWord))
          logger.debug(s"edits2: ${editResults2.length}")
          editResults2
        }
      }

      {
        val suggestions = corrections.mkString(",")
        logger.debug(s"${corrections.length} corrections for '$word/$normalizedWord': $suggestions")
      }

      Some(corrections)
    }
  }

  def loadDictionary(in: InputStream): Map[Word, Rank] =
    Source
      .fromInputStream(in)
      .getLines()
      .toVector
      .par
      .map { line =>
        val Array(word, rank) = line.split("\\t", 2)
        normalizeWord(word) -> rank.toInt
      }
      .toMap
      .seq

  def edits1(word: Word): Seq[Word] = {

    // splits("dogbert"):
    //   ("", "dogbert"), ("d", "ogbert"), ("do", "gbert"), ("do", "gbert"),
    //   ("do", "gbert"), ("dog", "bert"), ("dogb", "ert"), ("dogbe", "rt"), ("dogber", "t")
    val wordSplits: Seq[(Word, Word)] = splits(word)

    val edits = Seq[Seq[(Word, Word)] => Seq[String]](
      deletes, // dilbert -> dilbrt, dlbert, ...
      inserts, // wally -> wallyt, walluy, ...
      transposes, // alice -> alcie, aliec ...
      replaces // boss -> bosz, bosd, ...
    )

    edits
      .par // compute separate edits concurrently
      .flatMap(edit => edit(wordSplits))
      .distinct
      .seq
  }

  // For less common but still frequent two-pronged typos
  def edits2(word: String): Seq[String] = {
    for {
      e1 <- edits1(word).par
      e2 <- edits1(e1).par
    } yield e2
  }
    .distinct
    .seq

  // Edit operations
  val Letters: String = ('a' until 'z').mkString // ASCII-only

  def splits(word: Word): Seq[(Word, Word)] =
    for (i <- word.indices)
      yield (word.substring(0, i), word.substring(i))

  def deletes(splits: Seq[(Word, Word)]): Seq[Word] =
    for ((left, right) <- splits if !right.isEmpty)
      yield left + right.substring(1)

  def transposes(splits: Seq[(Word, Word)]): Seq[Word] =
    for ((left, right) <- splits if right.length > 1)
      yield left + right(1) + right(0) + right.substring(2)

  def replaces(splits: Seq[(Word, Word)]): Seq[Word] =
    for ((left, right) <- splits if !right.isEmpty; letter <- Letters)
      yield left + letter + right.substring(1)

  def inserts(splits: Seq[(Word, Word)]): Seq[Word] =
    for ((left, right) <- splits; letter <- Letters)
      yield left + letter + right
}