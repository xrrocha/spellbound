package net.xrrocha.java.spellbound.norvig;

import net.xrrocha.java.spellbound.norvig.SpellingCorrector.Split;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import static junit.framework.TestCase.fail;
import static net.xrrocha.java.spellbound.norvig.SpellingCorrector.LETTERS;
import static net.xrrocha.java.spellbound.norvig.SpellingCorrector.deletes;
import static net.xrrocha.java.spellbound.norvig.SpellingCorrector.inserts;
import static net.xrrocha.java.spellbound.norvig.SpellingCorrector.isAlphabetic;
import static net.xrrocha.java.spellbound.norvig.SpellingCorrector.normalize;
import static net.xrrocha.java.spellbound.norvig.SpellingCorrector.replaces;
import static net.xrrocha.java.spellbound.norvig.SpellingCorrector.splits;
import static net.xrrocha.java.spellbound.norvig.SpellingCorrector.transposes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SpellingCorrectorTest {

  private final SpellingCorrector spellingCorrector = new SpellingCorrector(Map.of(
      "centry", 93832,
      "contra", 13242,
      "country", 393,
      "ricksha", 1000000,
      "sleeping", 5216,
      "sliping", 1000000,
      "sloping", 27280,
      "spelling", 7302,
      "spewing", 41780,
      "spiling", 1000000
  ));

  @Test
  public void yieldsEmptyOnDictionaryWord() {
    assertFalse(spellingCorrector.getCorrections("spelling").isPresent());
  }

  @Test
  public void yieldsNonEmptyCorrectionsOnTypo() {
    List<String> expectedCorrections = List.of(
        "spelling", "spewing", "spiling"
    );
    Optional<List<String>> actualCorrections = spellingCorrector.getCorrections("speling");
    assertTrue(actualCorrections.isPresent());
    assertTrue(equals(expectedCorrections, actualCorrections.get()));
  }

  @Test
  public void yieldsEmptyCorrectionsOnGibberish() {
    Optional<List<String>> corrections = spellingCorrector.getCorrections("xwphjwl");
    assertTrue(corrections.isPresent());
    assertTrue(corrections.get().isEmpty());
  }

  @Test
  public void buildsSplitsCorrectly() {
    String name = "dilbert";

    List<Split> expectedSplits = List.of(
        new Split("", "dilbert"),
        new Split("d", "ilbert"),
        new Split("di", "lbert"),
        new Split("dil", "bert"),
        new Split("dilb", "ert"),
        new Split("dilbe", "rt"),
        new Split("dilber", "t"),
        new Split("dilbert", "")
    );

    List<Split> actualSplits = splits(name);

    assertEquals(name.length() + 1, actualSplits.size());

    assertTrue(equals(expectedSplits, actualSplits));
  }

  @Test
  public void buildsDeletesCorrectly() {
    String name = "wally";
    List<Split> splits = splits(name);

    List<String> expectedDeletes = List.of(
        "ally", "wlly", "waly", "waly", "wall"
    );
    List<String> actualDeletes = deletes(splits);
    assertEquals(actualDeletes.size(), name.length());

    assertTrue(equals(expectedDeletes, actualDeletes));
  }

  @Test
  public void buildsTransposesCorrectly() {
    String name = "alice";
    List<Split> splits = splits(name);

    List<String> expectedTransposes = List.of(
        "laice", "ailce", "alcie", "aliec"
    );
    List<String> actualTransposes = transposes(splits);
    assertEquals(actualTransposes.size(), name.length() - 1);

    assert (equals(expectedTransposes, actualTransposes));
  }

  @Test
  public void buildsReplacesCorrectly() {
    String name = "asok";
    List<Split> splits = splits(name);

    List<String> expectedReplaces = List.of(
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
    List<String> actualReplaces = replaces(splits);
    assertEquals(actualReplaces.size(), LETTERS.length * name.length());

    assertTrue(equals(expectedReplaces, actualReplaces));
  }

  @Test
  public void buildsInsertsCorrectly() {
    String name = "asok";
    List<Split> splits = splits(name);

    List<String> expectedInserts = List.of(
        "aasok", "basok", "casok", "dasok", "easok", "fasok", "gasok",
        "hasok", "iasok", "jasok", "kasok", "lasok", "masok", "nasok",
        "oasok", "pasok", "qasok", "rasok", "sasok", "tasok", "uasok",
        "vasok", "wasok", "xasok", "yasok", "zasok", "aasok", "absok",
        "acsok", "adsok", "aesok", "afsok", "agsok", "ahsok", "aisok",
        "ajsok", "aksok", "alsok", "amsok", "ansok", "aosok", "apsok",
        "aqsok", "arsok", "assok", "atsok", "ausok", "avsok", "awsok",
        "axsok", "aysok", "azsok", "asaok", "asbok", "ascok", "asdok",
        "aseok", "asfok", "asgok", "ashok", "asiok", "asjok", "askok",
        "aslok", "asmok", "asnok", "asook", "aspok", "asqok", "asrok",
        "assok", "astok", "asuok", "asvok", "aswok", "asxok", "asyok",
        "aszok", "asoak", "asobk", "asock", "asodk", "asoek", "asofk",
        "asogk", "asohk", "asoik", "asojk", "asokk", "asolk", "asomk",
        "asonk", "asook", "asopk", "asoqk", "asork", "asosk", "asotk",
        "asouk", "asovk", "asowk", "asoxk", "asoyk", "asozk", "asoka",
        "asokb", "asokc", "asokd", "asoke", "asokf", "asokg", "asokh",
        "asoki", "asokj", "asokk", "asokl", "asokm", "asokn", "asoko",
        "asokp", "asokq", "asokr", "asoks", "asokt", "asoku", "asokv",
        "asokw", "asokx", "asoky", "asokz"
    );
    List<String> actualInserts = inserts(splits);
    assertEquals(actualInserts.size(), LETTERS.length * (name.length() + 1));

    assertTrue(equals(expectedInserts, actualInserts));
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
    List<String> nonAlphas = List.of(
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

  static <T> boolean equals(List<T> s1, List<T> s2) {
    assertEquals(s1.size(), s2.size());
    return IntStream.range(0, s1.size())
        .allMatch(i -> s1.get(i).equals(s2.get(i)));
  }
}
