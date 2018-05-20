package net.xrrocha.spellbound.kotlin

import net.xrrocha.spellbound.kotlin.Edits.WordSplit
import net.xrrocha.spellbound.kotlin.Edits.wordSplits
import org.junit.Test
import kotlin.test.assertEquals

class EditsTest {

  val deletes = Edits.ALL_EDITS[0]
  val inserts = Edits.ALL_EDITS[1]
  val transposes = Edits.ALL_EDITS[2]
  val replaces = Edits.ALL_EDITS[3]

  @Test
  fun splitsWordsCorrectly() {

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

    val actualDeletes = deletes(splits)

    assertEquals(actualDeletes.count(), name.length)
    assertEquals(expectedDeletes, actualDeletes)
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
    val actualInserts = inserts(splits)

    assertEquals(actualInserts.count(), Edits.LETTERS.count() * (name.length + 1))
    assertEquals(expectedInserts, actualInserts)
  }

  @Test
  fun buildsTransposesCorrectly() {

    val expectedTransposes = listOf(
        "laice", "ailce", "alcie", "aliec"
    )

    val name = "alice"
    val splits = name.wordSplits()
    val actualTransposes = transposes(splits)

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
    val actualReplaces = replaces(splits)

    assertEquals(actualReplaces.count(), Edits.LETTERS.count() * name.length)
    assertEquals(expectedReplaces, actualReplaces)
  }
}