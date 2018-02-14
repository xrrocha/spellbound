## Spellbound: Spelling Suggestion Implemented in Multiple JVM languages

<img src="https://raw.githubusercontent.com/xrrocha/spellbound/master/spellbound-snippets/static/images/spelling-suggestion-strip.png" align="left">

This tutorial project implements a simple spelling suggestion service in
multiple JVM languages. Accompanying blog entries are published at
[my blog](https://xrrocha.net/post/spelling-jvm-1-introduction/).

Spelling suggestion is a familiar, well-understood domain (though
industrial-strength implementations can prove quite nuanced). By embracing 
ease of implementation over optimal performance we allow readers to focus on 
comparing the JVM languages and their implementations, unencumbered by 
needless complexity.

Within these relaxed constraints, spelling suggestion provides an excellent 
subject to illustrate and contrast the various "better Java" JVM languages. 
Features discussed include  functional programming, type inference, 
extension methods, compile-time code generation and other niceties well beyond 
vanilla Java usage.

Examples and new concepts are first introduced first in Java 9 so they're 
immediately understandable to the experienced Java programmer. The Java 
implementation is then replicated, _idiomatically_, in today's most relevant 
alternative JVM languages:
[Kotlin](https://kotlinlang.org/),
[Scala](http://scala-lang.org/) and
[Xtend](http://www.eclipse.org/xtend/).
<br style="clear: both">

<img src="https://raw.githubusercontent.com/xrrocha/spellbound/master/spellbound-snippets/static/images/small-norvig.png" align="left">
[Peter Norvig](https://en.wikipedia.org/wiki/Peter_Norvig)'s simple (but 
powerful) [spelling corrector](http://norvig.com/spell-correct.html) is
implemented in Java, Kotlin, Scala and Xtend. A separate tutorial blog post is 
written for each language that contrasts it with the Java "reference" 
implementation.
