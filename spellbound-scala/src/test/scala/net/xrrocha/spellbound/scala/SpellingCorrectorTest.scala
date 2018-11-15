package net.xrrocha.spellbound.scala

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.FunSuite

class SpellingCorrectorTest extends FunSuite with LazyLogging {

  import SpellingCorrector._

  val dictionary = Map(
    "centry" -> 12463,
    "contra" -> 93053,
    "country" -> 105902,
    "ricksha" -> 0,
    "sleeping" -> 101079,
    "sliping" -> 0,
    "sloping" -> 79015,
    "spelling" -> 98993,
    "spewing" -> 64515,
    "spiling" -> 0
  )

  val spellingCorrector = SpellingCorrector(dictionary)

  test("Yields empty on dictionary word") {
    assert(spellingCorrector.getCorrections("spelling").isEmpty)
  }

  test("Yields corrections on one typo") {
    val typo1 = "speling"
    val expectedCorrections = Seq("spelling", "spewing", "spiling")

    val actualCorrections = spellingCorrector.getCorrections(typo1)

    assert(actualCorrections.isDefined)
    assert(expectedCorrections == actualCorrections.get)
  }

  test("Yields corrections on two typos") {
    val typo2 = "spelinmg"
    val expectedCorrections = Seq("spelling", "spewing", "spiling")

    val actualCorrections = spellingCorrector.getCorrections(typo2)

    assert(actualCorrections.isDefined)
    assert(expectedCorrections == actualCorrections.get)
  }

  test("Yields no corrections on gibberish") {
    val corrections = spellingCorrector.getCorrections("xwphjwl")

    assert(corrections.isDefined)
    assert(corrections.get.isEmpty)
  }

  test("Builds word splits correctly") {
    val expectedSplits = Seq(
      ("", "dilbert"),
      ("d", "ilbert"),
      ("di", "lbert"),
      ("dil", "bert"),
      ("dilb", "ert"),
      ("dilbe", "rt"),
      ("dilber", "t"),
      ("dilbert", "")
    )

    val name = "dilbert"
    val actualSplits = wordSplits(name)

    assert(name.length + 1 == actualSplits.length)
    assert(expectedSplits == actualSplits)
  }

  test("Builds deletes correctly") {
    val name = "wally"
    val splits = wordSplits(name)

    val expectedDeletes = Seq("ally", "wlly", "waly", "waly", "wall")

    val actualDeletes = deletes(splits)

    assert(actualDeletes.length == name.length)
    assert(expectedDeletes == actualDeletes)
  }

  test("Builds inserts correctly") {
    val name = "dgbert"
    val splits = wordSplits(name)

    val expectedInserts = Vector(
      "adgbert",
      "bdgbert",
      "cdgbert",
      "ddgbert",
      "edgbert",
      "fdgbert",
      "gdgbert",
      "hdgbert",
      "idgbert",
      "jdgbert",
      "kdgbert",
      "ldgbert",
      "mdgbert",
      "ndgbert",
      "odgbert",
      "pdgbert",
      "qdgbert",
      "rdgbert",
      "sdgbert",
      "tdgbert",
      "udgbert",
      "vdgbert",
      "wdgbert",
      "xdgbert",
      "ydgbert",
      "zdgbert",
      "dagbert",
      "dbgbert",
      "dcgbert",
      "ddgbert",
      "degbert",
      "dfgbert",
      "dggbert",
      "dhgbert",
      "digbert",
      "djgbert",
      "dkgbert",
      "dlgbert",
      "dmgbert",
      "dngbert",
      "dogbert",
      "dpgbert",
      "dqgbert",
      "drgbert",
      "dsgbert",
      "dtgbert",
      "dugbert",
      "dvgbert",
      "dwgbert",
      "dxgbert",
      "dygbert",
      "dzgbert",
      "dgabert",
      "dgbbert",
      "dgcbert",
      "dgdbert",
      "dgebert",
      "dgfbert",
      "dggbert",
      "dghbert",
      "dgibert",
      "dgjbert",
      "dgkbert",
      "dglbert",
      "dgmbert",
      "dgnbert",
      "dgobert",
      "dgpbert",
      "dgqbert",
      "dgrbert",
      "dgsbert",
      "dgtbert",
      "dgubert",
      "dgvbert",
      "dgwbert",
      "dgxbert",
      "dgybert",
      "dgzbert",
      "dgbaert",
      "dgbbert",
      "dgbcert",
      "dgbdert",
      "dgbeert",
      "dgbfert",
      "dgbgert",
      "dgbhert",
      "dgbiert",
      "dgbjert",
      "dgbkert",
      "dgblert",
      "dgbmert",
      "dgbnert",
      "dgboert",
      "dgbpert",
      "dgbqert",
      "dgbrert",
      "dgbsert",
      "dgbtert",
      "dgbuert",
      "dgbvert",
      "dgbwert",
      "dgbxert",
      "dgbyert",
      "dgbzert",
      "dgbeart",
      "dgbebrt",
      "dgbecrt",
      "dgbedrt",
      "dgbeert",
      "dgbefrt",
      "dgbegrt",
      "dgbehrt",
      "dgbeirt",
      "dgbejrt",
      "dgbekrt",
      "dgbelrt",
      "dgbemrt",
      "dgbenrt",
      "dgbeort",
      "dgbeprt",
      "dgbeqrt",
      "dgberrt",
      "dgbesrt",
      "dgbetrt",
      "dgbeurt",
      "dgbevrt",
      "dgbewrt",
      "dgbexrt",
      "dgbeyrt",
      "dgbezrt",
      "dgberat",
      "dgberbt",
      "dgberct",
      "dgberdt",
      "dgberet",
      "dgberft",
      "dgbergt",
      "dgberht",
      "dgberit",
      "dgberjt",
      "dgberkt",
      "dgberlt",
      "dgbermt",
      "dgbernt",
      "dgberot",
      "dgberpt",
      "dgberqt",
      "dgberrt",
      "dgberst",
      "dgbertt",
      "dgberut",
      "dgbervt",
      "dgberwt",
      "dgberxt",
      "dgberyt",
      "dgberzt",
      "dgberta",
      "dgbertb",
      "dgbertc",
      "dgbertd",
      "dgberte",
      "dgbertf",
      "dgbertg",
      "dgberth",
      "dgberti",
      "dgbertj",
      "dgbertk",
      "dgbertl",
      "dgbertm",
      "dgbertn",
      "dgberto",
      "dgbertp",
      "dgbertq",
      "dgbertr",
      "dgberts",
      "dgbertt",
      "dgbertu",
      "dgbertv",
      "dgbertw",
      "dgbertx",
      "dgberty",
      "dgbertz"
    )
    val actualInserts = inserts(splits)

    assert(actualInserts.length == Letters.length * (name.length + 1))
    assert(expectedInserts.sorted == actualInserts.sorted)
  }

  test("Builds transposes correctly") {

    val expectedTransposes = Seq("laice", "ailce", "alcie", "aliec")

    val name = "alice"
    val splits = wordSplits(name)
    val actualTransposes = transposes(splits)

    assert(actualTransposes.length == name.length - 1)
    assert(expectedTransposes == actualTransposes)
  }

  test("Builds replaces correctly") {
    val name = "asok"
    val splits = wordSplits(name)

    val expectedReplaces = Vector(
      "asok",
      "bsok",
      "csok",
      "dsok",
      "esok",
      "fsok",
      "gsok",
      "hsok",
      "isok",
      "jsok",
      "ksok",
      "lsok",
      "msok",
      "nsok",
      "osok",
      "psok",
      "qsok",
      "rsok",
      "ssok",
      "tsok",
      "usok",
      "vsok",
      "wsok",
      "xsok",
      "ysok",
      "zsok",
      "aaok",
      "abok",
      "acok",
      "adok",
      "aeok",
      "afok",
      "agok",
      "ahok",
      "aiok",
      "ajok",
      "akok",
      "alok",
      "amok",
      "anok",
      "aook",
      "apok",
      "aqok",
      "arok",
      "asok",
      "atok",
      "auok",
      "avok",
      "awok",
      "axok",
      "ayok",
      "azok",
      "asak",
      "asbk",
      "asck",
      "asdk",
      "asek",
      "asfk",
      "asgk",
      "ashk",
      "asik",
      "asjk",
      "askk",
      "aslk",
      "asmk",
      "asnk",
      "asok",
      "aspk",
      "asqk",
      "asrk",
      "assk",
      "astk",
      "asuk",
      "asvk",
      "aswk",
      "asxk",
      "asyk",
      "aszk",
      "asoa",
      "asob",
      "asoc",
      "asod",
      "asoe",
      "asof",
      "asog",
      "asoh",
      "asoi",
      "asoj",
      "asok",
      "asol",
      "asom",
      "ason",
      "asoo",
      "asop",
      "asoq",
      "asor",
      "asos",
      "asot",
      "asou",
      "asov",
      "asow",
      "asox",
      "asoy",
      "asoz"
    )

    val actualReplaces = replaces(splits)

    assert(actualReplaces.length == Letters.length * name.length)
    assert(expectedReplaces.sorted == actualReplaces.sorted)
  }

  test("Recognizes non-alphabetic") {
    assert(!isAlphabetic("neo42"))
  }

  test("Accepts alphabetic") {
    assert(isAlphabetic("neo"))
    assert(isAlphabetic("Neo"))
    assert(isAlphabetic("NEO"))
  }

  test("Rejects non-alpha normalization") {
    intercept[IllegalArgumentException] {
      normalize("!@#$")
    }
  }

  test("Accepts alpha normalization") {
    val normalizedWord = normalize(" Neo\t\n")
    assert("neo" == normalizedWord)
  }

  test("Rejects empty dictionary") {
    intercept[IllegalArgumentException] {
      SpellingCorrector(Map())
    }
  }

  test("Rejects invalid words") {
    val nonAlphas = Seq("", " \t\n", "123", "number42")
    nonAlphas.foreach { badWord =>
      intercept[IllegalArgumentException] {
        spellingCorrector.getCorrections(badWord)
      }
    }
  }

  test("normalizesWhitespace") {
    val word = " \tword\r\n"
    assert("word" == normalize(word))
  }

  test("normalizeRejectsNonAlphas") {
    intercept[IllegalArgumentException] {
      normalize("non-alpha")
    }
  }
}
