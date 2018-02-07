package net.xrrocha.spellbound.kotlin

import org.junit.Test
import kotlin.test.assertEquals

class LuceneStringMetricTest {
  @Test
  fun comparesCorrectlyUsingLevenstein() {
    assertEquals(1.0, LevensteinStringMetric.stringSimilarity("same", "same"))
    assertEquals(.75, LevensteinStringMetric.stringSimilarity("amor", "amar"))
    assertEquals(0.1111111044883728, LevensteinStringMetric.stringSimilarity("almost", "unrelated"))
    assertEquals(.0, LevensteinStringMetric.stringSimilarity("no", "cigar"))
  }
}

class JaroWinklerStringMetricTest {
  @Test
  fun comparesCorrectlyUsingJaroWinkler() {
    assertEquals(1.0, JaroWinklerStringMetric.stringSimilarity("same", "same"))
    assertEquals(0.8666666746139526, JaroWinklerStringMetric.stringSimilarity("amor", "amar"))
    assertEquals(0.5185185074806213, JaroWinklerStringMetric.stringSimilarity("almost", "unrelated"))
    assertEquals(.0, JaroWinklerStringMetric.stringSimilarity("no", "cigar"))
  }
}
