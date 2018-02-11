## Spellbound: Spelling Suggestion Implemented in Multiple JVM languages

<img src="https://raw.githubusercontent.com/xrrocha/spellbound/master/spellbound-snippets/static/images/spelling-suggestion-strip.png" align="left">

This tutorial project implements a simple spelling suggestion service in multiple JVM languages.
Accompanying blog entries are cross-published at
[medium.com](https://medium.com/@xrrocha/spelling-suggester-implemented-in-multiple-jvm-languages-faa733cab249/)
and [my blog](http://xrrocha.net/).

Spelling suggestion is a familiar, well-understood domain (though industrial-strength
implementations can prove quite nuanced). By embracing ease of implementation over optimal 
performance we allow readers to focus on comparing the JVM languages and their implementations,
unencumbered by needless complexity.

Within these relaxed constraints, spelling suggestion provides an excellent subject to illustrate 
and contrast the various "better Java" JVM languages. Features discussed include  functional 
programming, type inference, extension methods, compile-time code generation and other niceties 
well beyond vanilla Java usage.

Examples and new concepts are first introduced first in Java 9 so they're immediately understandable
to the experienced Java programmer. Crucially, all Java examples are accompanied by equivalent )but
_idiomatic_) implementations in today's most relevant alternative JVM languages:
[Kotlin](https://kotlinlang.org/),
[Scala](http://scala-lang.org/) and
[Xtend](http://www.eclipse.org/xtend/).
<br style="clear: both">



### Two Approaches to Spelling Suggestion

This projects illustrates two simple approaches to spelling suggestion, each implemented in the
four above-mentioned languages (Java, Kotlin, Scala and Xtend). A separate tutorial document is
written for each approach/language pair contrasting the Java solution to that of each alternative
JVM language.

<img src="https://raw.githubusercontent.com/xrrocha/spellbound/master/spellbound-snippets/static/images/peter-norvig.png" width="25%" height="25%" align="left">

The first approach (which we refer to as [Norvig](https://en.wikipedia.org/wiki/Peter_Norvig)'s
approach) identifies top four typo-inducing mistakes (deletes, transposes, replaces and
inserts). The intuition here is that such edit operations are _commutative_ so that, when applied
to a typo, they can reconstitute one or more valid dictionary words (for instance, a _delete_ can
be compensated by an _insert_ and viceversa). This approach is illustrated in 
[Norvig's Python script](http://norvig.com/spell-correct.html). The algorithm is simple and
performant but occasionally misses sensible corrections.

> _You may want to take a look at the [Norvig Spelling Corrector Java implementation](https://github.com/xrrocha/spellbound/blob/master/spellbound-norvig/spellbound-norvig-java/src/main/java/net/xrrocha/java/spellbound/norvig/SpellingCorrector.java)_.
<br>
<br style="clear: both">

<img src="https://raw.githubusercontent.com/xrrocha/spellbound/master/spellbound-snippets/static/images/ngram2word.png" width="25%" height="25%" align="left">

The second approach (which we refer to as as the [ngram-based](https://en.wikipedia.org/wiki/N-gram)
approach) is based on the notion that a dictionary word and most of its typos share a number of
common ngrams. When an unknown word is found, a set of dictionary words is retrieved that share one
or more ngrams with the typo. Words sharing ngrams with the typo are then compared to it by means
of some
[string distance metric](https://en.wikipedia.org/wiki/String_metric)
(often the [Damerau–Levenshtein](https://en.wikipedia.org/wiki/Damerau%E2%80%93Levenshtein_distance)
metric). If a comparison falls below a metric-specific maximum threshold
then the matching dictionary word is deemed a correction suggestion. In the
worst case, this approach may require thousands of  such comparisons and yet
retrieve only a few candidate words. Despite this, its performance is 
acceptable and (unlike Norvig's approach) it produces an almost exhaustive
list of suggestions.
<br style="clear: both">
