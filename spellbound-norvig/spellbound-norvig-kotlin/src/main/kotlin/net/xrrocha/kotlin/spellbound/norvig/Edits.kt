package net.xrrocha.kotlin.spellbound.norvig

data class WordSplit(val left: String, val right: String)

enum class Edits {
  DELETES {
    override fun candidates(wordSplits: Iterable<WordSplit>): Iterable<String> =
        wordSplits
            .filterNot { it.right.isEmpty() }
            .map { it.left + it.right.substring(1) }
  },
  INSERTS {
    override fun candidates(wordSplits: Iterable<WordSplit>): Iterable<String> =
        wordSplits
            .flatMap { split ->
              LETTERS.map { split.left + it + split.right }
            }
  },
  TRANSPOSES {
    override fun candidates(wordSplits: Iterable<WordSplit>): Iterable<String> =
        wordSplits
            .filter { it.right.length > 1 }
            .map {
              it.left + it.right.substring(1, 2) +
                  it.right.substring(0, 1) +
                  it.right.substring(2)
            }
  },
  REPLACES {
    override fun candidates(wordSplits: Iterable<WordSplit>): Iterable<String> =
        wordSplits
            .filterNot { it.right.isEmpty() }
            .flatMap { split ->
              LETTERS.map { split.left + it + split.right.substring(1) }
            }
  };

  abstract fun candidates(wordSplits: Iterable<WordSplit>): Iterable<String>

  companion object {

    val ALL_EDITS = values().toList()

    val LETTERS = CharRange('a', 'z')

    fun splits(word: String): Iterable<WordSplit> =
        IntRange(0, word.length).map {
          WordSplit(word.substring(0, it), word.substring(it))
        }
  }
}
