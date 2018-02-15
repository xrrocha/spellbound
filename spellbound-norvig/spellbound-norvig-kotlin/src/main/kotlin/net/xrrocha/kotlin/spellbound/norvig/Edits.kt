package net.xrrocha.kotlin.spellbound.norvig

import net.xrrocha.kotlin.spellbound.norvig.Edit.Companion.Letters

data class WordSplit(val left: String, val right: String)

interface Edit {
  fun candidates(wordSplits: Iterable<WordSplit>): Iterable<String>

  companion object {

    val Letters = CharRange('a', 'z')

    val AllEdits = listOf(Deletes, Inserts, Transposes, Replaces)

    fun splits(word: String): Iterable<WordSplit> =
        IntRange(0, word.length).map {
          WordSplit(word.substring(0, it), word.substring(it))
        }
  }
}

object Deletes : Edit {
  override fun candidates(wordSplits: Iterable<WordSplit>): Iterable<String> {
    return wordSplits
        .filterNot { it.right.isEmpty() }
        .map { it.left + it.right.substring(1) }
  }
}

object Inserts : Edit {
  override fun candidates(wordSplits: Iterable<WordSplit>): Iterable<String> {
    return wordSplits
        .flatMap { split ->
          Letters.map { split.left + it + split.right }
        }
  }
}

object Transposes : Edit {
  override fun candidates(wordSplits: Iterable<WordSplit>): Iterable<String> {
    return wordSplits
        .filter { it.right.length > 1 }
        .map {
          it.left + it.right.substring(1, 2) +
              it.right.substring(0, 1) +
              it.right.substring(2)
        }
  }
}

object Replaces : Edit {
  override fun candidates(wordSplits: Iterable<WordSplit>): Iterable<String> {
    return wordSplits
        .filterNot { it.right.isEmpty() }
        .flatMap { split ->
          Letters.map { split.left + it + split.right.substring(1) }
        }
  }
}

