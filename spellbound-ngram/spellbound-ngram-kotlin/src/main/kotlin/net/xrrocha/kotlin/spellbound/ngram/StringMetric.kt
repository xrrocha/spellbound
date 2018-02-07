package net.xrrocha.kotlin.spellbound.ngram

import info.debatty.java.stringsimilarity.Damerau
import org.apache.lucene.search.spell.JaroWinklerDistance
import org.apache.lucene.search.spell.LevensteinDistance
import org.apache.lucene.search.spell.StringDistance

interface StringMetric {
  fun stringDistance(s1: String, s2: String): Double
}

object DamerauStringMetric : StringMetric {
  private val damerau = Damerau()

  override fun stringDistance(s1: String, s2: String): Double {
    val maxLength = maxOf(s1.length, s2.length).toDouble()
    return 1.0 - (maxLength - damerau.distance(s1, s2)) / maxLength
  }
}

interface LuceneStringMetric : StringMetric {
  val stringDistance: StringDistance

  override fun stringDistance(s1: String, s2: String) =
      1.0 - stringDistance.getDistance(s1, s2).toDouble()
}

object LevensteinStringMetric : LuceneStringMetric {
  override val stringDistance = LevensteinDistance()
}

object JaroWinklerStringMetric : LuceneStringMetric {
  override val stringDistance = JaroWinklerDistance()
}
