package net.xrrocha.scala.spellbound.ngram

import java.io.{FileWriter, PrintWriter}

import com.typesafe.scalalogging.StrictLogging

import scala.io.Source
import scala.util.{Failure, Success}

object NGramStats extends StrictLogging {
  def main(args: Array[String]): Unit = {

    import SpellingResource._

    val spellingResource = new SpellingResource with SpellingResourceConfig
    val ngramMap = spellingResource.ngramMap
    val dictionary = spellingResource.dictionary

    def measure(word: String): (String, Int, Int, Long, String) = {

      val wordNgrams: Set[NGram] = ngrams(word, spellingResource.ngramLength)
      logger.debug(s"Word '$word' has ${wordNgrams.size} ngrams: ${wordNgrams.mkString(",")}")

      val ngramToWords: Map[NGram, Set[NormalizedWord]] =
        wordNgrams
          .filter(ngramMap.contains)
          .flatMap { ngram =>
            ngramMap(ngram).map { word =>
              ngram -> word
            }
          }
          .groupBy(_._1)
          .mapValues(_.map(_._2))
      ngramToWords.foreach { case (ngram, words) =>
        logger.debug(s"ngram '$ngram': ${words.size}. ${words.mkString(",")}")
      }

      val candidateWords: Set[NormalizedWord] =
        ngramToWords
          .values
          .flatten
          .toSet
      logger.debug(s"candidateWords: ${candidateWords.size}")

      val (trySimilarWords, elapsedTime) = time {
        candidateWords
          .map { candidateWord =>
            candidateWord -> DamerauStringMetric.stringDistance(word, candidateWord)
          }
          .filter(_._2 <= DamerauStringMetric.maxDistance)
          .flatMap(t => dictionary(t._1))
      }
      logger.debug(
        s"${elapsedTime / 1000D} seconds elapsed comparing ${candidateWords.size} candidate words")

      val similarWordEntries: Set[(Word, Rank)] =
        trySimilarWords match {
          case Success(similarwords) => similarwords
          case Failure(error) =>
            throw error
        }

      {
        val similarWords = similarWordEntries.toSeq.sortBy(-_._2).mkString(",")
        logger.debug(s"${similarWordEntries.size} similar words: $similarWords")
      }

      val similarWords =
        similarWordEntries
          .toSeq
          .sortBy(-_._2)
          .map(_._1)
          .filterNot(_ == word)
          .mkString(",")

      (word, candidateWords.size, similarWordEntries.size - 1, elapsedTime, similarWords)
    }

    val (_, runtime) = time {

      val out = new PrintWriter(new FileWriter("data/ngram-stats.tsv"), true)

      logger.info(s"Populating performance file")
      Source
        .fromFile("data/dictionary.tsv")
        .getLines()
        .map { line =>
          val Array(word, _) = line.split("\\t")
          word
        }
        .map(measure)
        .foreach { case (word, candidateCount, similarCount, elapsedTime, similarWords) =>
          val time = elapsedTime / 1000D
          out.println(s"$word\t$candidateCount\t$similarCount\t$time\t$similarWords")
        }

      out.close()
    }
    logger.info(s"${dictionary.size} words analyzed in ${runtime / 1000D} seconds")
  }
}
