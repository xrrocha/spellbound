package net.xrrocha.scala.spellbound.norvig

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

    assert(corrector.getCorrectionsFor("speling").contains(Seq("spelling", "spewing")))

    assert(corrector.getCorrectionsFor("korrectud").contains(Seq("corrected")))

    assert(corrector.getCorrectionsFor("ricsha").contains(Seq("ricksha")))

    assert(corrector.getCorrectionsFor("sleping")
      .contains(Seq("sleeping", "sloping", "slewing", "seeping")))

    assert(corrector.getCorrectionsFor("tougt")
      .contains(Seq("tough", "tout")))
  }
}
