package net.xrrocha.scala.spellbound.ngram

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.FunSuite

class SettingsTest extends FunSuite with LazyLogging {

  val config: Config = new GlobalConfigSettings {}.globalConfig

  test("Parses application.conf correctly and completely") {
    assert(config.getInt("spellingResources.ngramLength") == 3)
    assert(config.getString("spellingResources.dictionaryLocation") == "data/dictionary.tsv")
    assert(config.getString("spellingResources.ngramLocation") == "data/ngrams.tsv")
    assert(config.getDouble("stringMetric.maxDistance") == .275)
    assert(config.getString("stringMetric.stringMetric") == "damerau")
  }
}
