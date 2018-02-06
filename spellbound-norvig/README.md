## Spellbound: A Spelling Suggestion Service implemented in Several JVM languages

This tutorial project implements a simple (but usefully functional!) _spelling suggestion service_. 

While spelling suggestion is a familiar domain, its actual implementation, though not complex, is not trivial either and
provides an excellent vehicle to illustrate and leverage modern constructs in JVM languages.

These constructs include aspects such as functional programming, type inference, extension methods, compile-time code generation
and many other niceties well beyond "traditional" Java usage.

Examples and new concepts are first introduced first in Java 9 so they're immediately understandable to the experienced Java
programmer. This initial Java formulation, though, aims at illustrating recent additions to the Java language such as modules,
improved type inference and enhanced lambda support, to name a few.

Importantly, all Java examples are accompanied by equivalent _and idiomatic_ implementations in today's most relevant
alternative JVM languages: Kotlin, Scala and Xtend. These languages were chosen both because of how readily Java programmers
can pick their syntax and semantics as well as because how widely they're accepted in the JVM community at large.

In this tutorial special attention is given to Kotlin and Scala as they both compile to Javascript thus enabling their use as
both backend and frontend languages. Xtend is different from other JVM languages in that it compiles to (readable) Java source
code. Since Xtend's semantics are the same of Java, Java programmers can learn to read the language in hours and use it
productively in a few days.
