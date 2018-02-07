package net.xrrocha.kotlin.spellbound.ngram

fun ngrams(string: String, ngramLength: Int): List<NGram> =
    string
        .trim()
        .toLowerCase()
        .split("\\s+".toRegex())
        .filter { it.length >= ngramLength }
        .flatMap { it.windowed(ngramLength, 1, false).filter { it.length == ngramLength } }
        .distinct()
