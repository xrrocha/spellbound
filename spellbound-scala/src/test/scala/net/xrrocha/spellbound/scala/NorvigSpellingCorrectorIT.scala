package net.xrrocha.spellbound.scala

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.FunSuite

class NorvigSpellingCorrectorIT extends FunSuite with LazyLogging {
  val corrector: NorvigSpellingCorrector = {
    val dictionaryFilename = "../data/dictionary.tsv"
    NorvigSpellingCorrector(new File(dictionaryFilename))
  }

  test("Returns no corrections on curated dictionary word") {
    assert(corrector.getCorrectionsFor("dilbert").isEmpty)
  }

  test("Suggests appropriate corrections") {

    def typoYields(typo: Word, words: Seq[Word]) = {
      val set = corrector.getCorrectionsFor(typo).get.toSet
      words.forall(set.contains)
    }

    assert(typoYields("speling", Seq("spelling", "spewing")))

    assert(typoYields("korrectud", Seq("corrected")))

    assert(typoYields("ricsha", Seq("ricksha")))

    assert(typoYields("sleping", Seq("sleeping", "sloping", "slewing", "seeping")))

    assert(typoYields("tougt", Seq("tough", "tout")))
  }
}
