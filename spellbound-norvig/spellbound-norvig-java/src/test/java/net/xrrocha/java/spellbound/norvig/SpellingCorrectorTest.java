package net.xrrocha.java.spellbound.norvig;

import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.*;

public class SpellingCorrectorTest {

  private final SpellingCorrector defaultCorrector = new SpellingCorrector(Map.of("spelling", 1));

  @Test(expected = NullPointerException.class)
  public void rejectsNullAlphabetic() {
    defaultCorrector.isAlphabetic(null);
  }

  @Test
  public void recognizesNonAlphabetic() {
    assertFalse(defaultCorrector.isAlphabetic("neo42"));
  }

  @Test
  public void acceptsAlphabetic() {
    defaultCorrector.isAlphabetic("neo");
    defaultCorrector.isAlphabetic("Neo");
    defaultCorrector.isAlphabetic("NEO");
  }

  @Test(expected = NullPointerException.class)
  public void rejectsNullWord() {
    defaultCorrector.getCorrections(null);
  }

  @Test(expected = NullPointerException.class)
  public void rejectsNullNormalization() {
    defaultCorrector.normalize(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void rejectsNonAlphaNormalization() {
    defaultCorrector.normalize("!@#$");
  }

  @Test
  public void acceptsAlphaNormalization() {
    String normalizedWord = defaultCorrector.normalize(" Neo\t\n");
    assertEquals("neo", normalizedWord);
  }

  @Test(expected = IllegalArgumentException.class)
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
        defaultCorrector.getCorrections(badWord);
        fail("Accepts non-alpha: " + badWord);
      } catch (IllegalArgumentException | NullPointerException e) {
      }
    });
  }

  @Test(expected = NullPointerException.class)
  public void rejectsNullWords() {
    defaultCorrector.getCorrections(null);
  }

  @Test
  public void yieldsEmptyOnDictionaryWord() {
    assertFalse(defaultCorrector.getCorrections("spelling").isPresent());
  }

  @Test
  @Ignore
  public void yieldsNonEmptyCorrectionsOnTypo() {
    Optional<List<String>> corrections = defaultCorrector.getCorrections("speling");
    assertTrue(corrections.isPresent());
    assertTrue(corrections.get().size() > 0);
  }

  @Test
  @Ignore
  public void yieldsEmptyCorrectionsOnGibberish() {
    Optional<List<String>> corrections = defaultCorrector.getCorrections("xwphjwl");
    assertTrue(corrections.isPresent());
    assertTrue(corrections.get().isEmpty());
  }
}
