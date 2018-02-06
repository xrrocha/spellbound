package net.xrrocha.scala.spellbound.ngram

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.FunSuite

class NGramTest extends FunSuite with LazyLogging {

  test("Extracts proper ngrams given length") {
    assert(SpellingResource.ngrams("jvm", 2)() == Set("jv", "vm"))
    assert(SpellingResource.ngrams("java", 3)() == Set("jav", "ava"))
  }

  test("Omits strings shorter ngram length") {
    assert(SpellingResource.ngrams("hello jvm goodbye", 4)() ==
      Set("hell", "ello", "jvm", "good", "oodb", "odby", "dbye"))
  }

  test("Emits shorter ngrams for shorter strings") {
    assert(SpellingResource.ngrams("jvm", 4)() == Set("jvm"))
  }

  test("Removes duplicate ngrams") {
    assert(SpellingResource.ngrams("jacoco", 2)() == Set("ja", "ac", "co", "oc"))
  }

  test("Preserves order while removing duplicate ngrams") {
    assert(SpellingResource.ngrams("senselessness", 2)() ==
      Set("se", "en", "ns", "el", "le",
        "es", "ss",
        "sn", "ne"))
  }

  test("Ignores blanks") {
    assert(SpellingResource.ngrams("java on the jvm", 2)() ==
      Set("ja", "av", "va", "on", "th",
        "he", "jv",
        "vm"))
  }

  test("Treats multiple contiguous blanks as one") {
    assert(SpellingResource.ngrams("java \t on \n the \r jvm", 2)() ==
      Set("ja", "av", "va", "on", "th",
        "he", "jv", "vm"))
  }
}

