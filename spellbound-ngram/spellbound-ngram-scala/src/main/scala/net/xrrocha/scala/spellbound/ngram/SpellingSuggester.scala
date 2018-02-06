package net.xrrocha.scala.spellbound.ngram

import com.typesafe.scalalogging.LazyLogging

trait SpellingSuggester {
  def getSuggestions(word: Word): Option[Seq[Word]]
}

trait DefaultSpellingSuggester extends SpellingSuggester with LazyLogging {

  def spellingResource: SpellingResource

  def stringMetric: StringMetric

  /**
    * Return suggested dictionary-curated words for a given word
    *
    * @param word : Option[Seq[Word].
    *             Return `None` if the given work is a valid dictionary work and no suggestions
    *             apply.
    *             Return ordered Some(Seq[Word]). The returned `Seq` may be empty indicating the
    *             word may need to be added to the dictionary or, otherwise, is utterly senseless.
    *             Return a non-empty `Seq[Word]` containing one or more suggested words considered
    *             similar enough by the chosen `StringMetric`
    * @return
    */
  def getSuggestions(word: Word): Option[Seq[Word]] = {

    val normalizedWord = SpellingResource.normalize(word)

    if (spellingResource.dictionary.contains(normalizedWord)) {
      val suggestions = spellingResource.dictionary(normalizedWord)
      if (suggestions.map(_._1).contains(word)) {
        None // Curated dictionary word; no suggestions applicable
      } else {
        Some { // Not in dictionary, suggest sibling words
          suggestions
            .sortWith { (left, right) =>
              val (leftWord, leftRank) = left
              val (rightWord, rightRank) = right
              leftRank < rightRank ||
                (leftRank == rightRank && leftWord < rightWord)
            }
            .map(_._1)
        }
      }
    } else Some {
      // Unknown word; return (possibly empty) list of similar dictionary words : Option[Seq[Word]]

      // 1) Generate list of distinct ngrams in the word: Seq[NGram]
      spellingResource.ngrams(word)
        // 2) Ignore ngrams not present in ngramMap: Seq[NGram]
        .filter(spellingResource.ngramMap.contains)
        // 3) Look up and flatten words associated with each ngram: Seq[NormalizedWord]
        //    Removes duplicate words as its backing collection is a Set
        .flatMap(spellingResource.ngramMap)
        // 4) Retrieve and flatten original words associated with normalized word
        //    Removes duplicate words as its backing collection is a Set
        .flatMap(spellingResource.dictionary)
        // 5) Compute distance for each candidate word and emit as a tuple: Seq[(Word, Distance)]
        .map { case (suggestion, rank) =>
        val distance = stringMetric.stringDistance(normalizedWord, suggestion)
        (suggestion, rank, distance)
      }
        // 6) Select words whose distance to unknown word is below maxDistance: Seq[(Word,Distance)]
        .filter { case (_, _, distance) => distance < stringMetric.maxDistance }
        // 7) Reverse-sort by distance so similar words show up first: Seq[(Word, Distance)]
        .toSeq // Make into Seq so we can sort reversely by distance and then lexically
        .sortWith { (left, right) =>
        val (_, leftRank, leftDistance) = left
        val (_, rightRank, rightDistance) = right
        leftDistance < rightDistance ||
          (leftDistance == rightDistance && leftRank < rightRank)
      }
        // 8) Emit only the ordered (but possibly empty) suggestion list: Seq[Word]
        .map { case (suggestion, _, _) => suggestion }
    }
  }
}
