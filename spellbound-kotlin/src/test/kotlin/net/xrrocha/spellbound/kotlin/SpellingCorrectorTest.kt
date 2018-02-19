package net.xrrocha.spellbound.kotlin


import net.xrrocha.spellbound.kotlin.SpellingCorrector.Companion.pack
import org.junit.Test
import java.lang.IllegalArgumentException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SpellingCorrectorTest {

    private val dictionary = mapOf(
            "centry" to 93832,
            "contra" to 13242,
            "country" to 393,
            "ricksha" to 1000000,
            "sleeping" to 5216,
            "sliping" to 1000000,
            "sloping" to 27280,
            "spelling" to 7302,
            "spewing" to 41780,
            "spiling" to 1000000)

    private val spellingCorrector = SpellingCorrector(dictionary)

    @Test
    fun yieldsNullOnDictionaryWord() {
        assertNull(spellingCorrector.getCorrections("spelling"))
    }

    @Test
    fun yieldsCorrectionsOnTypo() {
        val typo = "speling"
        val expectedCorrections = listOf(
                "spelling", "spewing", "spiling"
        )
        val actualCorrections = spellingCorrector.getCorrections(typo)
        assertNotNull(actualCorrections)
        assertEquals(expectedCorrections, actualCorrections)
    }

    @Test
    fun generatesEdit1Properly() {
        assertEquals(
                listOf("spelling", "spewing", "spiling"),
                spellingCorrector.edits1("speling"))
    }

    @Test
    fun generatesEdit2Properly() {
        assertEquals(
                listOf("sloping", "sliping"),
                spellingCorrector.edits2("slping"))
    }

    @Test
    fun packsProperly() {
        val strings: Iterable<String> = listOf(
                "spewing",
                "spiling",
                "spewing",
                "spelling"
        )
        assertEquals(
                listOf("spelling", "spewing", "spiling"),
                strings.pack(dictionary))
    }

    @Test
    fun normalizesWhitespace() {
        val word = " \tword\r\n"
        assertEquals("word", SpellingCorrector.normalize(word))
    }

    @Test(expected = IllegalArgumentException::class)
    fun normalizeRejectsNonAlphas() {
        SpellingCorrector.normalize("non-alpha")
    }
}