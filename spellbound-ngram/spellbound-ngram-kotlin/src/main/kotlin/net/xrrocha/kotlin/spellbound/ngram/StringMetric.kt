package net.xrrocha.kotlin.spellbound.ngram

import org.apache.lucene.search.spell.JaroWinklerDistance
import org.apache.lucene.search.spell.LevensteinDistance
import org.apache.lucene.search.spell.StringDistance

interface StringMetric {
  fun stringSimilarity(s1: String, s2: String): Double
}

interface LuceneStringMetric : StringMetric {
  val stringDistance: StringDistance

  override fun stringSimilarity(s1: String, s2: String) = stringDistance.getDistance(s1, s2).toDouble()
}

object LevensteinStringMetric : LuceneStringMetric {
  override val stringDistance = LevensteinDistance()
}

object JaroWinklerStringMetric : LuceneStringMetric {
  override val stringDistance = JaroWinklerDistance()
}
