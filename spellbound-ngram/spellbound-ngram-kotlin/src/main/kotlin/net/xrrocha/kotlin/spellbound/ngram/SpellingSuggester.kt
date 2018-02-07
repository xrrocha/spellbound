package net.xrrocha.kotlin.spellbound.ngram

import java.io.File

interface SpellingSuggester {
  fun getSuggestions(word: Word): List<Word>?
}

open class DefaultSpellingSuggester(private val stringMetric: StringMetric,
                                    private val maxDistance: Similarity,
                                    private val dictionaryFilename: Filename,
                                    private val ngramFilename: Filename,
                                    private val ngramLength: Length) : SpellingSuggester {

  private val dictionaryFile by lazy { File(dictionaryFilename) }
  private val ngramFile by lazy { File(ngramFilename) }

  private val loadedDictionary: Pair<Set<Word>, NGramMap> by lazy { loadFromFilesystem() }

  override fun getSuggestions(word: Word): List<Word>? {

    val (dictionary, ngram2words) = loadedDictionary

    val normalizedWord = word.normalize()

    return if (dictionary.contains(normalizedWord)) {
      // Word occurs in dictionary; suggestions not applicable
      null
    } else {
      // Unknown word; return (possibly empty) list of similar dictionary words: List<Word>
      // 1) Generate list of distinct ngrams in the normalized word: List<NGram>
      ngrams(normalizedWord, ngramLength)
          // 2) Ignore ngrams not present in ngram2words map: List<NGram>
          .filter { ngram2words.containsKey(it) }
          // 3) Look up list of words associated with each ngram flattening nested sequences: List<Word>
          .flatMap { ngram2words.getOrDefault(it, listOf()) }
          // 4) Remove duplicate words stemming from multiple matching ngrams: List<Word>
          .distinct()
          // 5) Compute distance index for each candidate word and emit as a pair: List<Pair<Word, Similarity>>
          .map { suggestion ->
            val distance = stringMetric.stringDistance(normalizedWord, suggestion)
            Suggestion(suggestion, distance)
          }
          // 6) Omit words whose distance with the unknown word falls below maxDistance: List<Pair<Word, Similarity>>
          .filter { (_, distance) -> distance < maxDistance }
          // 7) Sort words descendingly by distance so most similar words are shown first: List<Pair<Word, Similarity>>
          .sortedDescending()
          // 8) Remove distance scores producing only the ordered (but possibly empty) suggestion list: Seq[Word]
          .map { (suggestion, _) -> suggestion }
    }
  }

  fun String.normalize() = this.trim().toLowerCase()

  fun loadFromFilesystem(): Pair<Set<Word>, NGramMap> {

    require(dictionaryFile.isFile && dictionaryFile.canRead())

    // Create normalized set of unique dictionary words
    val dictionary: Set<Word> =
        dictionaryFile
            // Each line contains a word and its rank
            .readLines()
            // Normalize each word
            .map {
              it.split("\\t".toRegex())[0].normalize()
            }
            // Remove dups after normalization
            .toSet()

    val ngram2words: NGramMap =
        // If ngram file exists and is current...
        if (ngramFile.isFile && ngramFile.lastModified() > dictionaryFile.lastModified()) {
          ngramFile
              // Read file containing a word per line
              .readLines()
              // Extract multiple (but distinct) ngrams from each word. Flatten nested list: List<Pair<NGram, Word>>
              .flatMap { line ->
                // First field: ngram
                val (ngram, words) = line.split("\t".toRegex())
                // Second field: comma-separated list of associated words
                val wordList = words.split(",".toRegex())
                // Produce pair of ngram and word
                wordList.map { Pair(ngram, it) }
              }
              // Group ngram/word pairs as a map with ngram as key and list of words as value
              .groupBy({ (ngram, _) -> ngram }, { (_, words) -> words })
        } else {
          // If ngram file doesn't exist or is not current...
          val ngram2words =
              dictionary
                  // Extract multiple (but distinct) ngrams from word. Flatten nested list: List<Pair<NGram, Word>>
                  .flatMap { word ->
                    ngrams(word, ngramLength).map { ngram -> Pair(ngram, word) }
                  }
                  // Build map with ngrams as keys and ngram/word pairs as values: Map[NGram, Seq[(NGram, Word)]
                  .groupBy({ (ngram, _) -> ngram }, { (_, words) -> words })

          // Save ngrams file to speed up future runs
          ngramFile.printWriter().use { out ->
            ngram2words.forEach { (ngram, words) ->
              out.println("$ngram\t${words.joinToString(",")}")
            }
          }

          ngram2words
        }

    return Pair(dictionary, ngram2words)
  }

}

data class Suggestion(val word: Word, val distance: Similarity) : Comparable<Suggestion> {
  override fun compareTo(other: Suggestion): Int {
    val similarityComparison = distance.compareTo(other.distance)
    if (similarityComparison != 0) return -similarityComparison
    return word.compareTo(other.word)
  }
}
