package net.xrrocha.kotlin.spellbound.ngram

typealias Word = String
typealias NGram = String
typealias Similarity = Double // 0 <= s <= 1
typealias Filename = String
typealias Length = Int // 0 < l
typealias NGramMap = Map<NGram, List<Word>>
