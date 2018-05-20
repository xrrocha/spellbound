package net.xrrocha.spellbound.kotlin

object Edits {

  data class WordSplit(val left: String, val right: String)

  val LETTERS = CharRange('a', 'z')

  val ALL_EDITS: List<(Iterable<WordSplit>) -> Iterable<String>> = listOf(
      // deletes
      fun(wordSplits) = wordSplits
          .filterNot { it.right.isEmpty() }
          .map { it.left + it.right.substring(1) },
      // inserts
      fun(wordSplits) = wordSplits
          .flatMap { wordSplit ->
            LETTERS.map { wordSplit.left + it + wordSplit.right }
          },
      // transposes
      fun(wordSplits) = wordSplits
          .filter { it.right.length > 1 }
          .map {
            it.left +
                it.right.substring(1, 2) +
                it.right.substring(0, 1) +
                it.right.substring(2)
          },
      // replaces
      fun(wordSplits) = wordSplits
          .filterNot { it.right.isEmpty() }
          .flatMap { wordSplit ->
            LETTERS.map { wordSplit.left + it + wordSplit.right.substring(1) }
          }
  )

  val deletes = ALL_EDITS[0]
  val inserts = ALL_EDITS[1]
  val transposes = ALL_EDITS[2]
  val replaces = ALL_EDITS[3]

  fun String.wordSplits(): Iterable<WordSplit> =
      IntRange(0, this.length).map {
        WordSplit(this.substring(0, it), this.substring(it))
      }
}
