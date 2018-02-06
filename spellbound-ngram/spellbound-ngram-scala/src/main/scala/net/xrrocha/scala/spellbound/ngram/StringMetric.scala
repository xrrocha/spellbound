package net.xrrocha.scala.spellbound.ngram

import info.debatty.java.stringsimilarity.Damerau
import org.apache.lucene.search.spell.{JaroWinklerDistance, LevensteinDistance}

trait StringMetric {
  def maxDistance: Double

  def stringDistance(s1: String, s2: String): Double
}

trait LuceneStringMetric extends StringMetric {
  def stringDistance: org.apache.lucene.search.spell.StringDistance

  def stringDistance(s1: String, s2: String): Double =
    1.0 - stringDistance.getDistance(s1, s2)
}

object LevenshteinStringMetric extends LuceneStringMetric {
  lazy val maxDistance = .75
  lazy val stringDistance = new LevensteinDistance
}

object JaroWinklerStringMetric extends LuceneStringMetric {
  lazy val maxDistance = .85
  lazy val stringDistance = new JaroWinklerDistance
}

object DamerauStringMetric extends StringMetric {
  lazy val maxDistance = .275
  lazy val damerau = new Damerau

  def stringDistance(s1: String, s2: String) = {
    val maxLength = math.max(s1.length, s2.length)
    1.0 - (maxLength - damerau.distance(s1, s2)) / maxLength
  }
}

trait StringMetricConfig extends ConfigSettings {
  self: StringMetric =>

  lazy val configName = "stringMetric"

  lazy val maxDistance: Double = config.getDouble("maxDistance")

  lazy val stringMetric: StringMetric =
    if (!config.hasPath("stringMetricType")) {
      DamerauStringMetric
    } else {
      lazy val stringMetricName: String = config.getString("stringMetricType")
      stringMetricName.toLowerCase match {
        case "damerau" => DamerauStringMetric
        case "levenstein" => LevenshteinStringMetric
        case "jarowinkler" => JaroWinklerStringMetric
        case _ =>
          Class.forName(stringMetricName).asInstanceOf[StringMetric]
      }
    }
}



