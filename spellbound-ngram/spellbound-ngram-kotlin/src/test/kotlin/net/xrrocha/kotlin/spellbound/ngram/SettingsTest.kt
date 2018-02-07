package net.xrrocha.kotlin.spellbound.ngram

import net.xrrocha.kotlin.spellbound.ngram.Settings.config
import org.junit.Test
import kotlin.test.assertEquals

class SettingsTest {

  @Test
  fun parsesApplicationConf() {
    assertEquals("../data/ngrams.tsv", config.getString("files.ngramFilename"))
    assertEquals("../data/dictionary.tsv", config.getString("files.dictionaryFilename"))
    assertEquals(3, config.getInt("parameters.ngramLength"))
    assertEquals(.75, config.getDouble("parameters.maxDistance"))
    assertEquals("levenshtein", config.getString("parameters.stringMetric"))
  }
}