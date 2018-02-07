package net.xrrocha.kotlin.spellbound.ngram

import org.junit.Test
import kotlin.test.assertEquals

class NGramTest {
  @Test
  fun extractsProperNGramsForLength() {
    assertEquals(listOf("jv", "vm"), ngrams("jvm", 2))
    assertEquals(listOf("jav", "ava"), ngrams("java", 3))
  }

  @Test
  fun omitsStringsShorterThanLength() {
    assertEquals(listOf("hell", "ello", "good", "oodb", "odby", "dbye"), ngrams("hello jvm goodbye", 4))
  }

  @Test
  fun emitsNoNgramsForShorterStrings() {
    assertEquals(emptyList(), ngrams("jvm", 4))
  }

  @Test
  fun removesDuplicateNGrams() {
    assertEquals(listOf("ja", "ac", "co", "oc"), ngrams("jacoco", 2))
  }

  @Test
  fun preservesOrderWhileRemovingDups() {
    assertEquals(listOf("se", "en", "ns", "el", "le", "es", "ss", "sn", "ne"), ngrams("senselessness", 2))
  }

  @Test
  fun ignoresBlanks() {
    assertEquals(listOf("ja", "av", "va", "on", "th", "he", "jv", "vm"), ngrams("java on the jvm", 2))
  }

  @Test
  fun treatMultipleContiguousBlanksAsOne() {
    assertEquals(listOf("ja", "av", "va", "on", "th", "he", "jv", "vm"),
        ngrams("java \t on \n the \n jvm", 2))
  }
}