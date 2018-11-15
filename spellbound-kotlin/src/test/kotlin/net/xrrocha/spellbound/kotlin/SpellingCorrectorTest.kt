package net.xrrocha.spellbound.kotlin

import net.xrrocha.spellbound.kotlin.SpellingCorrector.Companion.isAlphabetic
import net.xrrocha.spellbound.kotlin.SpellingCorrector.Companion.normalize
import org.junit.Test
import kotlin.test.*

class SpellingCorrectorTest {

    private val dictionary = mapOf(
            "centry" to 12463,
            "contra" to 93053,
            "country" to 105902,
            "ricksha" to 0,
            "sleeping" to 101079,
            "sliping" to 0,
            "sloping" to 79015,
            "spelling" to 98993,
            "spewing" to 64515,
            "spiling" to 0
    )

    private val spellingCorrector = SpellingCorrector(dictionary)

    @Test
    fun yieldsNullOnDictionaryWord() {
        assertNull(spellingCorrector.getCorrections("spelling"))
    }

    @Test
    fun yieldsCorrectionsOnOneTypo() {
        val typo1 = "speling"
        val expectedCorrections = listOf(
                "spelling", "spewing", "spiling"
        )

        val actualCorrections = spellingCorrector.getCorrections(typo1)

        assertNotNull(actualCorrections)
        assertEquals(expectedCorrections, actualCorrections)
    }

    @Test
    fun yieldsCorrectionsOnTwoTypos() {
        val typo2 = "spelinmg"
        val expectedCorrections = listOf(
                "spelling", "spewing", "spiling"
        )

        val actualCorrections = spellingCorrector.getCorrections(typo2)

        assertNotNull(actualCorrections)
        assertEquals(expectedCorrections, actualCorrections)
    }

    @Test
    fun yieldsNoCorrectionsOnGibberish() {
        val corrections = spellingCorrector.getCorrections("xwphjwl")

        assertNotNull(corrections)
        assertTrue(corrections!!.none())
    }

    @Test
    fun recognizesNonAlphabetic() {
        assertFalse("neo42".isAlphabetic())
    }

    @Test
    fun acceptsAlphabetic() {
        assertTrue("neo".isAlphabetic())
        assertTrue("Neo".isAlphabetic())
        assertTrue("NEO".isAlphabetic())
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsNonAlphaNormalization() {
        "!@#$".normalize()
    }

    @Test
    fun acceptsAlphaNormalization() {
        val normalizedWord = " Neo\t\n".normalize()
        assertEquals("neo", normalizedWord)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsEmptyDictionary() {
        SpellingCorrector(mapOf())
    }

    @Test
    fun rejectsInvalidWords() {
        val nonAlphas = listOf(
                "",
                " \t\n",
                "123",
                "number42"
        )
        nonAlphas.forEach { badWord ->
            try {
                spellingCorrector.getCorrections(badWord)
                fail("Accepts non-alpha: $badWord")
            } catch (e: Exception) {
            }
        }
    }

    @Test
    fun normalizesWhitespace() {
        val word = " \tword\r\n"
        assertEquals("word", word.normalize())
    }

    @Test(expected = IllegalArgumentException::class)
    fun normalizeRejectsNonAlphas() {
        "non-alpha".normalize()
    }
}
