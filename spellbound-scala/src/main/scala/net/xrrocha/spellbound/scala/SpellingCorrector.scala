package net.xrrocha.spellbound.scala

import com.typesafe.scalalogging.StrictLogging

import scala.util.matching.Regex

case class SpellingCorrector(dictionary: Map[Word, Rank]) {
  require(dictionary != null && dictionary.nonEmpty)

  def getCorrections(word: Word): Option[Seq[Word]] = {

    require(word != null)

    import SpellingCorrector.{edits1, edits2, normalize}

    val normalizedWord = normalize(word)

    if (dictionary.contains(normalizedWord)) {
      // Curated dictionary word; no corrections needed
      None
    } else { // Try and find typo-originating words

      val corrections = {

        // Inner function to remove duplicates, weed out words not
        // present in dictionary and sort descending by rank
        def known(normalizedWords: Seq[Word]) =
          normalizedWords
            .distinct
            .filter(dictionary.contains)
            .sortBy(-dictionary(_)) // reverse by rank


        // Dictionary words occurring in edits1() results
        val editResults1 = known(edits1(normalizedWord))

        if (editResults1.nonEmpty) {

          editResults1

        } else { // Try edit2() only if edits1() is empty

          known(edits2(normalizedWord))

        }
      }

      Some(corrections)
    }
  }
}

object SpellingCorrector extends StrictLogging {

  def edits1(word: Word): Seq[Word] = {

    // wordSplits("dogbert"):
    //   ("", "dogbert"), ("d", "ogbert"), ("do", "gbert"), ("do", "gbert"),
    //   ("do", "gbert"), ("dog", "bert"), ("dogb", "ert"), ("dogbe", "rt"), ("dogber", "t")
    val splits = wordSplits(word)

    val edits = Seq[Seq[(Word, Word)] => Seq[Word]](
      deletes, // dilbert -> dilbrt, dlbert, ...
      inserts, // wally -> wallyt, walluy, ...
      transposes, // alice -> alcie, aliec ...
      replaces // boss -> bosz, bosd, ...
    )

    edits
      .par // compute separate edits concurrently
      .flatMap(edit => edit(splits))
      .seq
  }

  // For less common but still frequent two-pronged typos
  def edits2(word: Word): Seq[Word] = {
    for {
      e1 <- edits1(word).par
      e2 <- edits1(e1).par
    } yield e2
  }
    .seq

  // Edit operations
  val Letters: String = ('a' to 'z').mkString // ASCII-only

  def wordSplits(word: Word): Seq[(Word, Word)] =
    for (i <- 0 to word.length)
      yield (word.substring(0, i), word.substring(i))

  def deletes(splits: Seq[(Word, Word)]): Seq[Word] =
    for ((left, right) <- splits if !right.isEmpty)
      yield left + right.substring(1)

  def inserts(splits: Seq[(Word, Word)]): Seq[Word] =
    for ((left, right) <- splits; letter <- Letters)
      yield left + letter + right

  def transposes(splits: Seq[(Word, Word)]): Seq[Word] =
    for ((left, right) <- splits if right.length > 1)
      yield left + right(1) + right(0) + right.substring(2)

  def replaces(splits: Seq[(Word, Word)]): Seq[Word] =
    for ((left, right) <- splits if !right.isEmpty; letter <- Letters)
      yield left + letter + right.substring(1)

  val Alphabetic: Regex = "^[\\p{Alpha}]+$".r

  def isAlphabetic(word: Word): Boolean = Alphabetic.findFirstIn(word).isDefined

  def normalize(word: Word): Word = {
    val normalizedWord = word.trim.toLowerCase
    if (isAlphabetic(normalizedWord)) normalizedWord
    else throw new IllegalArgumentException(s"Non-alpha word: $word")
  }
}