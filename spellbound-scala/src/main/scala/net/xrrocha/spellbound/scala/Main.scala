package net.xrrocha.spellbound.scala

import java.io.InputStream

import net.xrrocha.spellbound.scala.SpellingCorrector.normalize

import scala.io.Source

class Main extends App {

  def loadDictionary(in: InputStream): Map[Word, Rank] =
    Source
      .fromInputStream(in)
      .getLines()
      .toVector
      .par
      .map { line =>
        val Array(word, rank) = line.split("\\t", 2)
        normalize(word) -> rank.toInt
      }
      .toMap
      .seq
}
