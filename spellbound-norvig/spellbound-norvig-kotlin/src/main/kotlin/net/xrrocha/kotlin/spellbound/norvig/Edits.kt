package net.xrrocha.kotlin.spellbound.norvig

data class WordSplit(val left: String, val right: String)

interface Edit {
  fun candidates(wordSplits: Iterable<WordSplit>): Iterable<String>

  companion object {
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
          SpellingCorrector.Letters.map { split.left + it + split.right }
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
        .filter { !it.right.isEmpty() }
        .flatMap { split ->
          SpellingCorrector.Letters.map { split.left + it + split.right.substring(1) }
        }
  }
}

