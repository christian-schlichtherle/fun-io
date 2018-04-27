---
---

# Overview

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

Fun I/O supports Java 8 or later and Scala 2.10, 2.11 and 2.12 equally well via dedicated APIs and is covered by the 
Apache License, version 2.0.
