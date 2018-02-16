package net.xrrocha.spellbound.kotlin


import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SpellingCorrectorTest {

  val spellingCorrector = SpellingCorrector(mapOf(
          "centry" to 93832,
          "contra" to 13242,
          "country" to 393,
          "ricksha" to 1000000,
          "sleeping" to 5216,
          "sliping" to 1000000,
          "sloping" to 27280,
          "spelling" to 7302,
          "spewing" to 41780,
          "spiling" to 1000000
  ))

  @Test
  fun yieldsNullOnDictionaryWord() {
    assertNull(spellingCorrector.getCorrections("spelling"))
  }

  @Test
  fun yieldsCorrectionsOnTypo() {
    val expectedCorrections = listOf(
        "spelling", "spewing", "spiling"
    )
    val actualCorrections = spellingCorrector.getCorrections("speling")
    assertNotNull(actualCorrections)
    assertEquals(expectedCorrections, actualCorrections)
  }
}