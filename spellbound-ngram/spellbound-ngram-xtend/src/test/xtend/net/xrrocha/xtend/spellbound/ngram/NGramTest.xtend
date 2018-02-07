package net.xrrocha.xtend.spellbound.ngram

import org.junit.Test
import static org.junit.Assert.assertEquals
import static extension net.xrrocha.xtend.spellbound.ngram.NGram.ngrams

class NGramTest {
  @Test
  def extractsProperNGramsForLength() {
    assertEquals(#["jv", "vm"], "jvm".ngrams(2))
    assertEquals(#["jav", "ava"], "java".ngrams(3))
  }

  @Test
  def omitsStringsShorterThanLength() {
    assertEquals(#["hell", "ello", "good", "oodb", "odby", "dbye"], "hello jvm goodbye".ngrams(4))
  }

  @Test
  def emitsNoNgramsForShorterStrings() {
    assertEquals(emptyList(), "jvm".ngrams(4))
  }

  @Test
  def removesDuplicateNGrams() {
    assertEquals(#["ja", "ac", "co", "oc"], "jacoco".ngrams(2))
  }

  @Test
  def preservesOrderWhileRemovingDups() {
    assertEquals(#["se", "en", "ns", "el", "le", "es", "ss", "sn", "ne"], "senselessness".ngrams(2))
  }

  @Test
  def ignoresBlanks() {
    assertEquals(#["ja", "av", "va", "on", "th", "he", "jv", "vm"], "java on the jvm".ngrams(2))
  }

  @Test
  def treatMultipleContiguousBlanksAsOne() {
    assertEquals(#["ja", "av", "va", "on", "th", "he", "jv", "vm"],
    "java \t on \n the \n jvm".ngrams(2))
  }
}