package net.xrrocha.spellbound.kotlin

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SpellingSuggesterTest {

  object LevensteinSuggester : DefaultSpellingSuggester(
      LevensteinStringMetric,
      .75,
      "../data/dictionary.txt",
      "../data/ngram2words.txt",
      3)


  @Test
  fun returnsNoSuggestionsOnDictionaryWord() {
    assertEquals(null, LevensteinSuggester.getSuggestions("senselessness"))
  }

  @Test
  fun returnsAppropriateSuggestions() {
    val ricshaSuggestions = LevensteinSuggester.getSuggestions("ricsha")
    assertNotEquals(null, ricshaSuggestions)
    assertEquals(listOf("ricksha", "rickshas", "rickshaw"), ricshaSuggestions)
    val slepingSuggestions = LevensteinSuggester.getSuggestions("sleping")
    assertEquals(listOf("sleeping", "seeping", "slewing", "sloping", "shlepping", "sleepings", "bleeping", "slapping",
        "sledding", "sledging", "sleeking", "sleeting", "slipping", "slopping", "slumping", "slurping", "steeping", "stepping",
        "sweeping"),
        slepingSuggestions)
  }

  @Test
  fun returnsEmptyListOnNoMatchingNGram() {
    assertEquals(listOf(), LevensteinSuggester.getSuggestions("dftba"))
  }

}