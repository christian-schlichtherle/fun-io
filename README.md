# Fun I/O [![Release Notes](https://img.shields.io/github/release/christian-schlichtherle/fun-io.svg?maxAge=3600)](https://github.com/christian-schlichtherle/fun-io/releases/latest) [![Maven Central](https://img.shields.io/maven-central/v/global.namespace.fun-io/fun-io-api.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22global.namespace.fun-io%22) [![Apache License 2.0](https://img.shields.io/github/license/christian-schlichtherle/neuron-di.svg?maxAge=3600)](https://www.apache.org/licenses/LICENSE-2.0) [![Build Status](https://api.travis-ci.org/christian-schlichtherle/fun-io.svg)](https://travis-ci.org/christian-schlichtherle/fun-io)

Fun I/O provides functional, high level abstractions for codecs, sources, sinks, stores, archives, filters, sockets et 
al.
Fun I/O supports Java 8 or later and Scala 2.10, 2.11 and 2.12 and is covered by the Apache License, version 2.0.

## Features

+ Composes low level `InputStream`s and `OutputStream`s into high level `Codec`s, `Source`s, `Sink`s, `Store`s, 
  `ArchiveStore`s, `Socket`s, `Filter`s et al.
  These abstractions are:
  + Easy to implement.
  + Stateless and hence reusable and thread-safe by design (except for their observable side effects, e.g. writing to a 
    file).
  + Composable into stateless I/O subsystems.
  + Interoperable with any code which deals with `InputStream`s and `OutputStream`s directly.
+ Proper resource management: Streams are properly closed, even if there is an exception in a nested constructor.
  Say goodbye to resource leaks and try-with-resources statements!
+ Supports Java 8 or later and Scala equally well via dedicated APIs.

## User Documentation

User documentation is available at https://christian-schlichtherle.github.io/fun-io/ .
