package net.xrrocha.scala.spellbound.ngram

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.FunSuite

class DamerauMetricTest extends FunSuite with LazyLogging {
  test("Compares correctly using Damerau-Levenstein") {
    assert(LevenshteinStringMetric.stringDistance("same", "same") == 0.0)
    assert(LevenshteinStringMetric.stringDistance("amor", "amar") == .25)
    assert(LevenshteinStringMetric.stringDistance("almost", "unrelated") == 0.8888888955116272)
    assert(LevenshteinStringMetric.stringDistance("no", "cigar") == 1.0)
  }
}

class LuceneStringMetricTest extends FunSuite with LazyLogging {
  test("Compares correctly using Levenstein") {
    assert(LevenshteinStringMetric.stringDistance("same", "same") == 0.0)
    assert(LevenshteinStringMetric.stringDistance("amor", "amar") == .25)
    assert(LevenshteinStringMetric.stringDistance("almost", "unrelated") == 0.8888888955116272)
    assert(LevenshteinStringMetric.stringDistance("no", "cigar") == 1.0)
  }
}

class JaroWinklerStringMetricTest extends FunSuite with LazyLogging {
  test("Compares correctly using JaroWinkler") {
    assert(JaroWinklerStringMetric.stringDistance("same", "same") == 0.0)
    assert(JaroWinklerStringMetric.stringDistance("amor", "amar") == 0.13333332538604736)
    assert(JaroWinklerStringMetric.stringDistance("almost", "unrelated") == 0.48148149251937866)
    assert(JaroWinklerStringMetric.stringDistance("no", "cigar") == 1.0)
  }
}
