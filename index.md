---
layout: home
title: Introduction
---

# Fun I/O

[![Release Notes](https://img.shields.io/github/release/christian-schlichtherle/fun-io.svg?maxAge=3600)](https://github.com/christian-schlichtherle/fun-io/releases/latest) [![Maven Central](https://img.shields.io/maven-central/v/global.namespace.fun-io/fun-io-api.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22global.namespace.fun-io%22) [![Apache License 2.0](https://img.shields.io/github/license/christian-schlichtherle/neuron-di.svg?maxAge=3600)](https://www.apache.org/licenses/LICENSE-2.0) [![Build Status](https://api.travis-ci.org/christian-schlichtherle/fun-io.svg)](https://travis-ci.org/christian-schlichtherle/fun-io)

Fun I/O provides functional, high level abstractions for codecs, sources, sinks, stores, archives, filters, sockets et 
al.
Fun I/O supports Java 8 or later and Scala 2.10, 2.11 and 2.12 equally well via dedicated APIs and is covered by the 
Apache License, version 2.0.

## Introduction

Fun I/O composes the low level classes `InputStream` and `OutputStream` into powerful, high level abstractions like 
`Codec`, `Source`, `Sink`, `Store`, `ArchiveStore`, `Socket`, `Filter` et al.
These abstractions are:

+ Easy to use and implement.
+ Stateless and therefore reusable and thread-safe by design (except for their observable side effects of course, e.g.
  writing data to a file).
+ Composable into stateless I/O subsystems.
+ Interoperable with any code which deals with an `InputStream` or `OutputStream` directly.

Fun I/O takes proper care of resource management: All created `InputStream` and `OutputStream` instances are properly 
closed, even if there is an exception in a nested constructor.
Say goodbye to resource leaks and try-with-resources statements!

**&raquo;** [Module Structure And Features]({{ site.baseurl }}{% link module-structure-and-features.md %})
