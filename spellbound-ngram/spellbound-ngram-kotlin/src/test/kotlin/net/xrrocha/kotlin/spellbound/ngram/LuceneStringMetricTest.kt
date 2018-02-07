package net.xrrocha.kotlin.spellbound.ngram

import org.junit.Test
import kotlin.test.assertEquals

class LuceneStringMetricTest {
  @Test
  fun comparesCorrectlyUsingLevenstein() {
    assertEquals(0.0, LevensteinStringMetric.stringDistance("same", "same"))
    assertEquals(.25, LevensteinStringMetric.stringDistance("amor", "amar"))
    assertEquals(0.8888888955116272, LevensteinStringMetric.stringDistance("almost", "unrelated"))
    assertEquals(1.0, LevensteinStringMetric.stringDistance("no", "cigar"))
  }
}

class JaroWinklerStringMetricTest {
  @Test
  fun comparesCorrectlyUsingJaroWinkler() {
    assertEquals(0.0, JaroWinklerStringMetric.stringDistance("same", "same"))
    assertEquals(0.13333332538604736, JaroWinklerStringMetric.stringDistance("amor", "amar"))
    assertEquals(0.48148149251937866, JaroWinklerStringMetric.stringDistance("almost", "unrelated"))
    assertEquals(1.0, JaroWinklerStringMetric.stringDistance("no", "cigar"))
  }
}
