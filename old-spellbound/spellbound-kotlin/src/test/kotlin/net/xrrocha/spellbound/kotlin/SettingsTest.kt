package net.xrrocha.spellbound.kotlin

import net.xrrocha.spellbound.kotlin.Settings.config
import org.junit.Test
import kotlin.test.assertEquals

class SettingsTest {

  @Test
  fun parsesApplicationConf() {
    assertEquals("../data/ngram2words.txt", config.getString("files.ngramFilename"))
    assertEquals("../data/dictionary.txt", config.getString("files.dictionaryFilename"))
    assertEquals(3, config.getInt("parameters.ngramLength"))
    assertEquals(.75, config.getDouble("parameters.maxDistance"))
    assertEquals("levenshtein", config.getString("parameters.stringMetric"))
  }
}