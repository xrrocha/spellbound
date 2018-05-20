package net.xrrocha.spellbound.java;

import net.xrrocha.spellbound.java.SpellingCorrector.WordSplit;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static junit.framework.TestCase.fail;
import static net.xrrocha.spellbound.java.SpellingCorrector.LETTERS;
import static net.xrrocha.spellbound.java.SpellingCorrector.deletes;
import static net.xrrocha.spellbound.java.SpellingCorrector.inserts;
import static net.xrrocha.spellbound.java.SpellingCorrector.isAlphabetic;
import static net.xrrocha.spellbound.java.SpellingCorrector.normalize;
import static net.xrrocha.spellbound.java.SpellingCorrector.replaces;
import static net.xrrocha.spellbound.java.SpellingCorrector.splits;
import static net.xrrocha.spellbound.java.SpellingCorrector.transposes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SpellingCorrectorTest {

  private final SpellingCorrector spellingCorrector =
      new SpellingCorrector(Map.of(
          "centry", 12463,
          "contra", 93053,
          "country", 105902,
          "ricksha", 0,
          "sleeping", 101079,
          "sliping", 0,
          "sloping", 79015,
          "spelling", 98993,
          "spewing", 64515,
          "spiling", 0
      ));

  @Test
  public void yieldsEmptyOnDictionaryWord() {
    assertFalse(spellingCorrector.getCorrections("spelling").isPresent());
  }

  @Test
  public void yieldsCorrectionsOnTypo() {
    var expectedCorrections = List.of(
        "spelling", "spewing", "spiling"
    );

    var actualCorrections = spellingCorrector.getCorrections("speling");

    assertTrue(actualCorrections.isPresent());
    assertEquals(expectedCorrections, actualCorrections.get());
  }

  @Test
  public void yieldsNoCorrectionsOnGibberish() {
    var corrections = spellingCorrector.getCorrections("xwphjwl");

    assertTrue(corrections.isPresent());
    assertTrue(corrections.get().isEmpty());
  }

  @Test
  public void buildsSplitsCorrectly() {
    var name = "dilbert";

    var expectedSplits = List.of(
        new WordSplit("", "dilbert"),
        new WordSplit("d", "ilbert"),
        new WordSplit("di", "lbert"),
        new WordSplit("dil", "bert"),
        new WordSplit("dilb", "ert"),
        new WordSplit("dilbe", "rt"),
        new WordSplit("dilber", "t"),
        new WordSplit("dilbert", "")
    );

    var actualSplits = splits(name);

    assertEquals(name.length() + 1, actualSplits.size());
    assertEquals(expectedSplits, actualSplits);
  }

  @Test
  public void buildsDeletesCorrectly() {
    var name = "wally";
    var splits = splits(name);

    var expectedDeletes = List.of(
        "ally", "wlly", "waly", "waly", "wall"
    );
    var actualDeletes = deletes(splits).collect(Collectors.toList());

    assertEquals(actualDeletes.size(), name.length());

    assertEquals(expectedDeletes, actualDeletes);
  }

  @Test
  public void buildsTransposesCorrectly() {
    var name = "alice";
    var splits = splits(name);

    var expectedTransposes = List.of(
        "laice", "ailce", "alcie", "aliec"
    );
    var actualTransposes = transposes(splits).collect(Collectors.toList());

    assertEquals(actualTransposes.size(), name.length() - 1);
    assertEquals(expectedTransposes, actualTransposes);
  }

  @Test
  public void buildsReplacesCorrectly() {
    var name = "asok";
    var splits = splits(name);

    var expectedReplaces = List.of(
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
    );
    var actualReplaces = replaces(splits).collect(Collectors.toList());

    assertEquals(actualReplaces.size(), LETTERS.length * name.length());
    assertEquals(expectedReplaces, actualReplaces);
  }

  @Test
  public void buildsInsertsCorrectly() {
    var name = "dgbert";
    var splits = splits(name);

    var expectedInserts = List.of(
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
    );
    var actualInserts = inserts(splits).collect(Collectors.toList());

    assertEquals(actualInserts.size(), LETTERS.length * (name.length() + 1));
    assertEquals(expectedInserts, actualInserts);
  }

  @Test(expected = NullPointerException.class)
  public void rejectsNullAlphabetic() {
    isAlphabetic(null);
  }

  @Test
  public void recognizesNonAlphabetic() {
    assertFalse(isAlphabetic("neo42"));
  }

  @Test
  public void acceptsAlphabetic() {
    isAlphabetic("neo");
    isAlphabetic("Neo");
    isAlphabetic("NEO");
  }

  @Test(expected = NullPointerException.class)
  public void rejectsNullWord() {
    spellingCorrector.getCorrections(null);
  }

  @Test(expected = NullPointerException.class)
  public void rejectsNullNormalization() {
    normalize(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void rejectsNonAlphaNormalization() {
    normalize("!@#$");
  }

  @Test
  public void acceptsAlphaNormalization() {
    String normalizedWord = normalize(" Neo\t\n");
    assertEquals("neo", normalizedWord);
  }

  @Test(expected = NullPointerException.class)
  public void rejectsNullDictionary() {
    new SpellingCorrector(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void rejectsEmptyDictionary() {
    new SpellingCorrector(Map.of());
  }

  @Test
  public void rejectsInvalidWords() {
    var nonAlphas = List.of(
        "",
        " \t\n",
        "123",
        "number42"
    );
    nonAlphas.forEach(badWord -> {
      try {
        spellingCorrector.getCorrections(badWord);
        fail("Accepts non-alpha: " + badWord);
      } catch (IllegalArgumentException | NullPointerException e) {
      }
    });
  }

  @Test(expected = NullPointerException.class)
  public void rejectsNullWords() {
    spellingCorrector.getCorrections(null);
  }
}
