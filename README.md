## Spellbound: Spelling Suggestion Implemented in Multiple JVM languages

This tutorial project implements a simple (but functional) _spelling suggestion service_. 

Spelling suggestion is a familiar domain but while its actual implementation, though not complex,
is not trivial either. This makes it an excellent vehicle to illustrate and contrast various JVM 
languages aiming at providing a "better Java".

Constructs in these language include aspects such as functional programming, type inference,
extension methods, compile-time code generation and other niceties well beyond vanilla Java usage.

Examples and new concepts are first introduced first in Java 9 so they're immediately understandable
to the experienced Java programmer.

Importantly, all Java examples are accompanied by equivalent but _idiomatic_ implementations in 
today's most relevant alternative JVM languages:
[Kotlin](https://kotlinlang.org/),
[Scala](http://scala-lang.org/) and
[Xtend](http://www.eclipse.org/xtend/).

### Two Approaches to Spelling Suggestion

This projects illustrates two simple approaches to spelling suggestion, each implemented in all the
languages mentioned above (Java, Kotlin, Scala and Xtend). A separate tutorial document has
been written for each approach/language pair contrasting the Java solution and its alternative 
JVM language one.

The first approach (which we refer to as [Norvig](https://en.wikipedia.org/wiki/Peter_Norvig)'s 
approach) identifies top five typo-inducing mistakes (splits, deletes, transposes, replaces and 
inserts). The intuition here is that such edit operations are _reversible_ so that, when applied
to a typo, they can reconstitute one or more valid dictionary words. This approach is illustrated
in [Norvig's Python script](http://norvig.com/spell-correct.html). The algorithm is simple and
performant but occasionally misses sensible corrections.

The second approach (which we refer to as as the [ngram-based](https://en.wikipedia.org/wiki/N-gram)
approach) is based on the notion that a dictionary word and most of its typos share a number of
common ngrams. When an unknown word is found, a set of dictionary words is retrieved that share one
or more ngrams with the typo. Words sharing ngrams with the typo are then compared to it by means
of some
[string distance metric](https://en.wikipedia.org/wiki/String_metric) 
(often the [Damerau–Levenshtein](https://en.wikipedia
.org/wiki/Damerau%E2%80%93Levenshtein_distance) metric).

If the comparison is below a metric-specific maximum threshold then the matching dictionary word
is deemed a correction suggestion. In the worst case, this approach may require thousands 
of such comparisons and yet retrieve only a few candidate words. Despite this, its performance is
acceptable and (unlike Norvig's approach) produces an almost exhaustive list of suggestions.
