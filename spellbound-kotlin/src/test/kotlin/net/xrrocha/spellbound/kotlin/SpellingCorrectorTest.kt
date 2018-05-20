package net.xrrocha.spellbound.kotlin

import net.xrrocha.spellbound.kotlin.Edits.WordSplit
import net.xrrocha.spellbound.kotlin.Edits.wordSplits
import net.xrrocha.spellbound.kotlin.SpellingCorrector.Companion.isAlphabetic
import net.xrrocha.spellbound.kotlin.SpellingCorrector.Companion.normalize
import net.xrrocha.spellbound.kotlin.SpellingCorrector.Companion.pack
import org.junit.Test
import java.lang.IllegalArgumentException
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
    fun buildsSplitsCorrectly() {
        val expectedSplits = listOf(
                WordSplit("", "dilbert"),
                WordSplit("d", "ilbert"),
                WordSplit("di", "lbert"),
                WordSplit("dil", "bert"),
                WordSplit("dilb", "ert"),
                WordSplit("dilbe", "rt"),
                WordSplit("dilber", "t"),
                WordSplit("dilbert", "")
        )

        val name = "dilbert"
        val actualSplits = name.wordSplits()

        assertEquals(name.length + 1, actualSplits.count())
        assertEquals(expectedSplits, actualSplits)
    }

    @Test
    fun buildsDeletesCorrectly() {
        val name = "wally"
        val splits = name.wordSplits()

        val expectedDeletes = listOf(
                "ally", "wlly", "waly", "waly", "wall"
        )

        val actualDeletes = Edits.deletes(splits)

        assertEquals(actualDeletes.count(), name.length)
        assertEquals(expectedDeletes, actualDeletes)
    }

    @Test
    fun buildsTransposesCorrectly() {

        val expectedTransposes = listOf(
                "laice", "ailce", "alcie", "aliec"
        )

        val name = "alice"
        val splits = name.wordSplits()
        val actualTransposes = Edits.transposes(splits)

        assertEquals(actualTransposes.count(), name.length - 1)
        assertEquals(expectedTransposes, actualTransposes)
    }

    @Test
    fun buildsReplacesCorrectly() {
        val name = "asok"
        val splits = name.wordSplits()

        val expectedReplaces = listOf(
                "asok", "bsok", "csok", "dsok", "esok", "fsok", "gsok", "hsok",
                "isok", "jsok", "ksok", "lsok", "msok", "nsok", "osok", "psok",
                "qsok", "rsok", "ssok", "tsok", "usok", "vsok", "wsok", "xsok",
                "ysok", "zsok", "aaok", "abok", "acok", "adok", "aeok", "afok",
                "agok", "ahok", "aiok", "ajok", "akok", "alok", "amok", "anok",
                "aook", "apok", "aqok", "arok", "asok", "atok", "auok", "avok",
                "awok", "axok", "ayok", "azok", "asak", "asbk", "asck", "asdk",
                "asek", "asfk", "asgk", "ashk", "asik", "asjk", "askk", "aslk",
                "asmk", "asnk", "asok", "aspk", "asqk", "asrk", "assk", "astk",
                "asuk", "asvk", "aswk", "asxk", "asyk", "aszk", "asoa", "asob",
                "asoc", "asod", "asoe", "asof", "asog", "asoh", "asoi", "asoj",
                "asok", "asol", "asom", "ason", "asoo", "asop", "asoq", "asor",
                "asos", "asot", "asou", "asov", "asow", "asox", "asoy", "asoz"
        )
        val actualReplaces = Edits.replaces(splits)

        assertEquals(actualReplaces.count(), Edits.LETTERS.count() * name.length)
        assertEquals(expectedReplaces, actualReplaces)
    }

    @Test
    fun buildsInsertsCorrectly() {
        val name = "dgbert"
        val splits = name.wordSplits()

        val expectedInserts = listOf(
                "adgbert", "bdgbert", "cdgbert", "ddgbert", "edgbert", "fdgbert",
                "gdgbert", "hdgbert", "idgbert", "jdgbert", "kdgbert", "ldgbert",
                "mdgbert", "ndgbert", "odgbert", "pdgbert", "qdgbert", "rdgbert",
                "sdgbert", "tdgbert", "udgbert", "vdgbert", "wdgbert", "xdgbert",
                "ydgbert", "zdgbert", "dagbert", "dbgbert", "dcgbert", "ddgbert",
                "degbert", "dfgbert", "dggbert", "dhgbert", "digbert", "djgbert",
                "dkgbert", "dlgbert", "dmgbert", "dngbert", "dogbert", "dpgbert",
                "dqgbert", "drgbert", "dsgbert", "dtgbert", "dugbert", "dvgbert",
                "dwgbert", "dxgbert", "dygbert", "dzgbert", "dgabert", "dgbbert",
                "dgcbert", "dgdbert", "dgebert", "dgfbert", "dggbert", "dghbert",
                "dgibert", "dgjbert", "dgkbert", "dglbert", "dgmbert", "dgnbert",
                "dgobert", "dgpbert", "dgqbert", "dgrbert", "dgsbert", "dgtbert",
                "dgubert", "dgvbert", "dgwbert", "dgxbert", "dgybert", "dgzbert",
                "dgbaert", "dgbbert", "dgbcert", "dgbdert", "dgbeert", "dgbfert",
                "dgbgert", "dgbhert", "dgbiert", "dgbjert", "dgbkert", "dgblert",
                "dgbmert", "dgbnert", "dgboert", "dgbpert", "dgbqert", "dgbrert",
                "dgbsert", "dgbtert", "dgbuert", "dgbvert", "dgbwert", "dgbxert",
                "dgbyert", "dgbzert", "dgbeart", "dgbebrt", "dgbecrt", "dgbedrt",
                "dgbeert", "dgbefrt", "dgbegrt", "dgbehrt", "dgbeirt", "dgbejrt",
                "dgbekrt", "dgbelrt", "dgbemrt", "dgbenrt", "dgbeort", "dgbeprt",
                "dgbeqrt", "dgberrt", "dgbesrt", "dgbetrt", "dgbeurt", "dgbevrt",
                "dgbewrt", "dgbexrt", "dgbeyrt", "dgbezrt", "dgberat", "dgberbt",
                "dgberct", "dgberdt", "dgberet", "dgberft", "dgbergt", "dgberht",
                "dgberit", "dgberjt", "dgberkt", "dgberlt", "dgbermt", "dgbernt",
                "dgberot", "dgberpt", "dgberqt", "dgberrt", "dgberst", "dgbertt",
                "dgberut", "dgbervt", "dgberwt", "dgberxt", "dgberyt", "dgberzt",
                "dgberta", "dgbertb", "dgbertc", "dgbertd", "dgberte", "dgbertf",
                "dgbertg", "dgberth", "dgberti", "dgbertj", "dgbertk", "dgbertl",
                "dgbertm", "dgbertn", "dgberto", "dgbertp", "dgbertq", "dgbertr",
                "dgberts", "dgbertt", "dgbertu", "dgbertv", "dgbertw", "dgbertx",
                "dgberty", "dgbertz"
        )
        val actualInserts = Edits.inserts(splits)

        assertEquals(actualInserts.count(), Edits.LETTERS.count() * (name.length + 1))
        assertEquals(expectedInserts, actualInserts)
    }

    @Test
    fun recognizesNonAlphabetic() {
        assertFalse("neo42".isAlphabetic())
    }

    @Test
    fun acceptsAlphabetic() {
        "neo".isAlphabetic()
        "Neo".isAlphabetic()
        "NEO".isAlphabetic()
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
    fun generatesEdit1Properly() {
        assertEquals(
                listOf("spelling", "spewing", "spiling"),
                spellingCorrector.edits1("speling").pack(dictionary))
    }

    @Test
    fun generatesEdit2Properly() {
        assertEquals(
                listOf("sloping", "sliping"),
                spellingCorrector.edits2("slpng").pack(dictionary))
    }

    @Test
    fun packsProperly() {
        val strings = listOf(
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
        assertEquals("word", word.normalize())
    }

    @Test(expected = IllegalArgumentException::class)
    fun normalizeRejectsNonAlphas() {
        "non-alpha".normalize()
    }
}