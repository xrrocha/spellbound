package net.xrrocha.kotlin.spellbound.norvig

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking

class SpellingCorrector(val dictionary: Map<String, Int>) {

  companion object {

    val Alphabetic = "^[a-z]+$".toRegex()

    fun normalize(word: String) =
        if (Alphabetic.containsMatchIn(word)) word.trim().toLowerCase()
        else throw IllegalArgumentException("Non-alpha word: $word")
  }

  fun getCorrections(word: String): Iterable<String>? {

    val normalizedWord = normalize(word)

    if (dictionary.containsKey(normalizedWord)) {
      return null
    }

    val corrections1 = edits1(normalizedWord)
    return if (corrections1.iterator().hasNext()) corrections1
    else {
      val corrections2 = edits2(normalizedWord)
      if (corrections2.iterator().hasNext()) corrections2 else null
    }
  }

  fun edits1(typo: String): Iterable<String> {
    val wordSplits = Edit.splits(typo)
    return Edit.AllEdits
        .pFlatMap { it.candidates(wordSplits) }
        .pack()
  }

  fun edits2(typo: String): Iterable<String> =
      edits1(typo)
          .pFlatMap { edits1(it) }
          .pack()

  fun <A, B> Iterable<A>.pFlatMap(mapper: suspend (A) -> Iterable<B>): Iterable<B> = runBlocking {
    map { async(CommonPool) { mapper(it) } }.flatMap { it.await() }
  }

  fun Iterable<String>.pack(): Iterable<String> =
      this
          .distinct()
          .filter { dictionary.containsKey(it) }
          .map { Pair(it, dictionary[it]) }
          .sortedBy { it.second }
          .map { it.first }
}