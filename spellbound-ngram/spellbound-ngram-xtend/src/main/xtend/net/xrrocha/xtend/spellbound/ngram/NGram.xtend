package net.xrrocha.xtend.spellbound.ngram

import java.util.List

class NGram {
  def static List<String> ngrams(String string, int ngramLength) {
    string
    .trim
    .toLowerCase
    .split("\\s+")
    .filter[it.length >= ngramLength]
    .flatMap[ word |
      (0..word.length - ngramLength).map[ start |
        word.substring(start, start + ngramLength)
      ]
    ]
    .toSet
    .toList
  }
}
