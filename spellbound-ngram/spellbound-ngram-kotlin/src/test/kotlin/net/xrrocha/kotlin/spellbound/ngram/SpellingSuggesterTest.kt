package net.xrrocha.kotlin.spellbound.ngram

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SpellingSuggesterTest {

  object LevensteinSuggester : DefaultSpellingSuggester(
      DamerauStringMetric,
      .275,
      "../data/dictionary.tsv",
      "../data/ngrams.tsv",
      3)


  @Test
  fun returnsNoSuggestionsOnDictionaryWord() {
    assertEquals(null, LevensteinSuggester.getSuggestions("senselessness"))
  }

  @Test
  fun returnsAppropriateSuggestions() {
    val ricshaSuggestions = LevensteinSuggester.getSuggestions("ricsha")
    assertNotEquals(null, ricshaSuggestions)
    assertEquals(listOf("ricksha", "rickshaw"), ricshaSuggestions)
    val slepingSuggestions = LevensteinSuggester.getSuggestions("sleping")
    assertEquals(listOf("sleeping", "sloping", "slewing", "seeping", "sweeping", "stepping",
        "steeping", "slurping", "slumping", "slopping", "slipping", "sledging", "sledding",
        "slapping", "bleeping"),
        slepingSuggestions)
  }

  @Test
  fun returnsEmptyListOnNoMatchingNGram() {
    assertEquals(listOf(), LevensteinSuggester.getSuggestions("dftba"))
  }

}