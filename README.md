## Spellbound: Spelling Suggestion Implemented in Multiple JVM languages

<img src="https://raw.githubusercontent.com/xrrocha/spellbound/master/spellbound-snippets/static/images/spelling-suggestion.png" style="float: left; margin-right: 16px;" width="25%" height="25%">
This tutorial project implements a simple (but functional) spelling suggestion service.
Accompanying blog entries start at [Spellbound's xrrocha.net blog](https://xrrocha.net/post/spelling-jvm-1.0/).

Spelling suggestion is a familiar, well-understood domain (though industrial-strength
implementations can prove quite nuanced). This simplicity allows Java developers to focus on the
alternative JVM languages as such, unencumbered by needless complexity.

For the sake of brevity we follow a happy path approach to design where we focus on the core
functionality omitting otherwise useful but non-essential behavior. We also accept less than ideal
performance in the name of implementation simplicity.

In this scenario, spelling suggestion makes an excellent vehicle to illustrate and contrast various
"better Java" JVM languages. Features discussed include aspects such as functional programming,
type inference, extension methods, compile-time code generation and other niceties well beyond
vanilla Java usage.

Examples and new concepts are first introduced first in Java 9 so they're immediately understandable
to the experienced Java programmer. Crucially, all Java examples are accompanied by equivalent but
_idiomatic_ implementations in today's most relevant alternative JVM languages:
[Kotlin](https://kotlinlang.org/),
[Scala](http://scala-lang.org/) and
[Xtend](http://www.eclipse.org/xtend/).




### Two Approaches to Spelling Suggestion

This projects illustrates two simple approaches to spelling suggestion, each implemented in the
three above-mentioned languages (Java, Kotlin, Scala and Xtend). A separate tutorial document is
written for each approach/language pair contrasting the Java solution with that of each
corresponding JVM language.

<img src="https://raw.githubusercontent.com/xrrocha/spellbound/master/spellbound-snippets/static/images/peter-norvig.png" style="float: left; margin-right: 16px;" width="25%" height="25%">
The first approach (which we refer to as [Norvig](https://en.wikipedia.org/wiki/Peter_Norvig)'s
approach) identifies top four typo-inducing mistakes (deletes, transposes, replaces and
inserts). The intuition here is that such edit operations are _commutative_ so that, when applied
to a typo, they can reconstitute one or more valid dictionary words. This approach is illustrated
in [Norvig's Python script](http://norvig.com/spell-correct.html). The algorithm is simple and
performant but occasionally misses sensible corrections.

<img src="https://raw.githubusercontent.com/xrrocha/spellbound/master/spellbound-snippets/static/images/ngram2word.png" style="float: left; margin-right: 16px;" width="25%" height="25%">
The second approach (which we refer to as as the [ngram-based](https://en.wikipedia.org/wiki/N-gram)
approach) is based on the notion that a dictionary word and most of its typos share a number of
common ngrams. When an unknown word is found, a set of dictionary words is retrieved that share one
or more ngrams with the typo. Words sharing ngrams with the typo are then compared to it by means
of some
[string distance metric](https://en.wikipedia.org/wiki/String_metric)
(often the [Damerau–Levenshtein](https://en.wikipedia.org/wiki/Damerau%E2%80%93Levenshtein_distance)
metric). If the comparison is below a metric-specific maximum threshold then the matching dictionary
word is deemed a correction suggestion. In the worst case, this approach may require thousands of 
such comparisons and yet retrieve only a few candidate words. Despite this, its performance is 
acceptable and (unlike Norvig's approach) produces an almost exhaustive list of suggestions.
