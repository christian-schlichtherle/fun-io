---
title: Module Structure And Features
---

Fun I/O has a modular structure.
Its artifacts are hosted on Maven Central with the common group ID 
[`global.namespace.fun-io`](http://search.maven.org/#search%7Cga%7C1%7Cglobal.namespace.fun-io).
The following diagram shows the module structure:

![Module Structure]({{ site.baseurl }}{% link assets/module-structure.svg %})

The modules are:

+ [![Fun I/O API](https://img.shields.io/maven-central/v/global.namespace.fun-io/fun-io-api.svg?label=Fun%20I/O%20API&maxAge=3600)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22global.namespace.fun-io%22%20AND%20a%3A%22fun-io-api%22)
  The API provides interfaces like `Codec`, `Source`, `Sink`, `Store`, `ArchiveSource`, `ArchiveSink`, `ArchiveStore`, 
  `Filter`, `Socket` et al.
+ [![Fun I/O Scala API](https://img.shields.io/maven-central/v/global.namespace.fun-io/fun-io-scala-api_2.12.svg?label=Fun%20I/O%20Scala%20API&maxAge=3600)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22global.namespace.fun-io%22%20AND%20a%3A%22fun-io-scala-api_2.12%22)
  The Scala API extends the Java API with operators and implicit conversions to improvie the user experience in Scala.
+ [![Fun I/O BIOS](https://img.shields.io/maven-central/v/global.namespace.fun-io/fun-io-bios.svg?label=Fun%20I/O%20BIOS&maxAge=3600)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22global.namespace.fun-io%22%20AND%20a%3A%22fun-io-bios%22)
  The Basic Input/Output System (pun intended) provides basic implementations of the Fun I/O API.
  + The `BIOS` class is a facade which provides the following `Codec` functions:
    + `serialization` serializes/deserializes objects using `ObjectOutputStream`/`ObjectInputStream`.
    + `xml` encodes/decodes objects using `XMLEncoder`/`XMLDecoder`.
  + It also provides the following `Filter` functions:
    + `base64` encodes/decodes data to/from Base64.
    + `buffer` buffers I/O operations.
    + `cipher` encrypts/decrypts data using a function which provides initialized `javax.security.Cipher` objects.
    + `deflate` deflates/inflates data using the ZIP compression.
    + `gzip` compresses/decompresses data using the GZIP compression format.
    + `identity` is a no-op, forming filters into a [Monoid] under the operations `Filter.andThen` and `Filter.compose`.
    + `inflate` inflates/deflates data using the ZIP compression.
  + It also provides the following `Source` functions:
    + `resource` reads a resource from the class path.
    + `stdin` reads the standard input.
    + `stream` reads an arbitrary input stream without ever closing it.
  + It also provides the following `Sink` functions:
    + `stderr` writes to standard error.
    + `stdout` writes to standard output.
    + `stream` write to an arbitrary output stream without ever closing it.
  + It also provides the following `Store` functions:
    + `file` stores data in a file, based on `java.io.File`. 
    + `memory` stores data on the heap. This is primarily used for cloning objects or testing.
    + `path` stores data in a files or any other path, based on `java.nio.file.Path`.
    + `preferences` stores data in a preferences node using a given key.
    + `systemPreferences` stores data in a system preferences nodes representing a given class.
    + `userPreferences` stores data in a user preferences nodes representing a given class.
  + It also provides the following `ArchiveStore` functions:
    + `directory` provides read/write access to a directory as if it were an archive file.
    + `jar` provides read/write access to JAR files.
    + `zip` provides read/write access to ZIP files.
  + It also provides the following utility functions:
    + `copy` is a high performance algorithm for copying data from a `Source` to a `Sink`, including `Store`, or from 
      an `ArchiveSource` to an `ArchiveSink`, including `ArchiveStore`.
    + `clone` duplicates an object by serializing it to memory and decoding it again.  
+ [![Fun I/O Commons Compress](https://img.shields.io/maven-central/v/global.namespace.fun-io/fun-io-commons-compress.svg?label=Fun%20I/O%20Commons%20Compress&maxAge=3600)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22global.namespace.fun-io%22%20AND%20a%3A%22fun-io-commons-compress%22)
  Depends on [Apache Commons Compress] to provide implementations of the Fun I/O API.
  + The `CommonsCompress` class is a facade which provides the following `Filter` functions: 
    + `blockLZ4` compresses/decompresses data using the LZ4 block format.
    + `bzip2` compresses decompresses data using the BZIP2 format.
    + `deflate` deflates/inflates data using the ZIP compression.
    + `framedLZ4` compresses/decompresses data using the LZ4 frame format.
    + `framedSnappy` compresses/decompresses data using the Snappy frame format.
    + `gzip` compresses/decompresses data using the GZIP compression format.
    + `lzma` compresses/decompresses data using the LZMA compression format.
    + `lzma2` compresses/decompresses data using the LZMA2 compression format.
  + It also provides the following `ArchiveStore` functions:
    + `jar` provides read/write access to JAR files.
    + `tar` provides copy-only access to TAR files.
    + `zip` provides read/write access to ZIP files.
+ [![Fun I/O Delta](https://img.shields.io/maven-central/v/global.namespace.fun-io/fun-io-delta.svg?label=Fun%20I/O%20Delta&maxAge=3600)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22global.namespace.fun-io%22%20AND%20a%3A%22fun-io-delta%22)
  Provides utility functions for diffing and patching archive files or directories.
  + The `Delta` class is a facade which provides the following utility functions:
    + `diff` compares two archive files or directories to compute a delta archive file or directory or model.
    + `patch` patches an archive file or directory with a delta archive file or directory.
+ [![Fun I/O Jackson](https://img.shields.io/maven-central/v/global.namespace.fun-io/fun-io-jackson.svg?label=Fun%20I/O%20Jackson&maxAge=3600)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22global.namespace.fun-io%22%20AND%20a%3A%22fun-io-jackson%22)
  Depends on [Jackson Databind] to provide implementations of the Fun I/O API. 
  + The `Jackson` class is a facade which provides the following `Codec` functions:
    + `json` marshals/unmarshals objects to/from JSON using Jackson Databind.
+ [![Fun I/O JAXB](https://img.shields.io/maven-central/v/global.namespace.fun-io/fun-io-jaxb.svg?label=Fun%20I/O%20JAXB&maxAge=3600)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22global.namespace.fun-io%22%20AND%20a%3A%22fun-io-jaxb%22)
  Depends on [JAXB] to provide implementations of the Fun I/O API.
  + The `JAXB` class is a facade which provides the following `Codec` functions:
    + `xml` marshals/unmarshals objects to/from XML using the JAXB reference implementation.
+ [![Fun I/O XZ](https://img.shields.io/maven-central/v/global.namespace.fun-io/fun-io-xz.svg?label=Fun%20I/O%20XZ&maxAge=3600)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22global.namespace.fun-io%22%20AND%20a%3A%22fun-io-xz%22)
  Depends on [XZ for Java] to provide implementations of the Fun I/O API.
  + The `XZ` class is a facade which provides the following `Filter` functions:
    + `lzma2` compresses/decompresses data using the LZMA2 compression format.
    + `xz` compresses/decompresses data using the XZ compression format.

A typical Java application has a single dependency on `fun-io-bios`.
Additional module dependencies may be added to the mix to take advantage of their respective features.

A typical Scala application has the same dependency/dependencies as a Java application plus an additional dependency on
`fun-io-scala-api` to improve the user experience in Scala. 

[Apache Commons Compress]: https://commons.apache.org/proper/commons-compress/
[Jackson Databind]: http://wiki.fasterxml.com/JacksonHome
[JAXB]: https://javaee.github.io/jaxb-v2/
[Monoid]: https://en.wikipedia.org/wiki/Monoid
[XZ for Java]: https://tukaani.org/xz/

**&laquo;** [Introduction]({{ site.baseurl }}{% link index.md %})
**&raquo;** [Basic Usage In Java]({{ site.baseurl }}{% link basic-usage-in-java.md %})
