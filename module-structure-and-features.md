---
title: Module Structure And Features
---

Fun I/O has a modular structure.
Its artifacts are hosted on Maven Central with the common group ID 
[`global.namespace.fun-io`](http://search.maven.org/#search%7Cga%7C1%7Cglobal.namespace.fun-io).
The following diagram shows the module structure:

![Module Structure]({{ site.baseurl }}{% link assets/module-structure.svg %})

## API Modules

API modules provide powerful abstractions for simple and safe synchronous I/O operations:

+ [![Fun I/O API](https://img.shields.io/maven-central/v/global.namespace.fun-io/fun-io-api.svg?label=Fun%20I/O%20API&maxAge=3600)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22global.namespace.fun-io%22%20AND%20a%3A%22fun-io-api%22)
  Provides interfaces like `Codec`, `Source`, `Sink`, `Store`, `ArchiveSource`, `ArchiveSink`, `ArchiveStore`, `Filter`, 
  `Socket` et al.
+ [![Fun I/O Scala API](https://img.shields.io/maven-central/v/global.namespace.fun-io/fun-io-scala-api_2.12.svg?label=Fun%20I/O%20Scala%20API&maxAge=3600)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22global.namespace.fun-io%22%20AND%20a%3A%22fun-io-scala-api_2.12%22)
  Extends the Java API with operators and implicit conversions to improve the development experience in Scala.

## Implementation Modules

Each implementation module provides a single facade class which contains many static factory methods for different 
implementations of the abstractions provided by the `fun-io-api` module:

+ [![Fun I/O BIOS](https://img.shields.io/maven-central/v/global.namespace.fun-io/fun-io-bios.svg?label=Fun%20I/O%20BIOS&maxAge=3600)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22global.namespace.fun-io%22%20AND%20a%3A%22fun-io-bios%22)
  The Basic Input/Output System (pun intended) provides basic implementations of the Fun I/O API.
  + The `BIOS` class is a facade which provides the following `Codec` functions:
    + `serialization` serializes/deserializes objects using `ObjectOutputStream`/`ObjectInputStream`.
    + `xml` encodes/decodes objects using `XMLEncoder`/`XMLDecoder`.
  + It also provides the following `Filter` functions:
    + `base64` encodes/decodes data using Base64.
    + `buffer` buffers I/O operations.
    + `cipher` encrypts/decrypts data using a function which provides initialized `javax.security.Cipher` objects.
    + `deflate` compresses/decompresses data using a ZIP deflater/inflater.
    + `gzip` compresses/decompresses data using the GZIP format.
    + `identity` is a no-op, forming filters into a [Monoid] under the operations `Filter.andThen` and `Filter.compose`.
    + `inflate` decompresses/compresses data using a ZIP inflater/deflater.
  + It also provides the following `Source` functions:
    + `resource` reads a resource from the class path.
    + `stdin` reads the standard input.
    + `stream` reads an arbitrary input stream without ever closing it.
    + `url` reads the content of an URL.
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
    + `deflate` compresses/decompresses data using a ZIP deflater/inflater.
    + `framedLZ4` compresses/decompresses data using the LZ4 frame format.
    + `framedSnappy` compresses/decompresses data using the Snappy frame format.
    + `gzip` compresses/decompresses data using the GZIP format.
    + `lzma` compresses/decompresses data using the LZMA format.
    + `lzma2` compresses/decompresses data using the LZMA2 format.
  + It also provides the following `ArchiveStore` functions:
    + `jar` provides read/write access to JAR files.
    + `sevenz` provides copy-only access to 7zip files.
    + `tar` provides copy-only access to TAR files.
    + `zip` provides read/write access to ZIP files.
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
    + `lzma` compresses/decompresses data using the LZMA format.
    + `lzma2` compresses/decompresses data using the LZMA2 format.
    + `xz` compresses/decompresses data using the XZ format.
+ [![Fun I/O Zstd](https://img.shields.io/maven-central/v/global.namespace.fun-io/fun-io-zstd.svg?label=Fun%20I/O%20Zstd&maxAge=3600)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22global.namespace.fun-io%22%20AND%20a%3A%22fun-io-zstd%22)
  Depends on [Zstd-jni] to provide implementations of the Fun I/O API.
  + The `Zstd` class is a facade which provides the following `Filter` functions:
    + `zstd` compresses/decompresses data using the Zstd format.

## Application Modules

Application modules depend on the API and expect some implementation to be injected for a particular task.
Like implementation modules, they provide a single facade class containing static methods to solve their particular 
task: 

+ [![Fun I/O Delta](https://img.shields.io/maven-central/v/global.namespace.fun-io/fun-io-delta.svg?label=Fun%20I/O%20Delta&maxAge=3600)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22global.namespace.fun-io%22%20AND%20a%3A%22fun-io-delta%22)
  Provides utility functions for diffing and patching archive files or directories.
  + The `Delta` class is a facade which provides the following utility functions:
    + `diff` compares two archive files or directories to compute a delta archive file or directory or model.
    + `patch` patches an archive file or directory with a delta archive file or directory.

<div class="btn-group d-flex justify-content-center" role="group" aria-label="Pagination">
  <button type="button" class="btn btn-light"><a href="{{ site.baseurl }}{% link index.md %}">&laquo; Introduction</a></button>
  <button type="button" class="btn btn-light"><a href="{{ site.baseurl }}{% link basic-usage.md %}">Basic Usage &raquo;</a></button>
</div>

[Apache Commons Compress]: https://commons.apache.org/proper/commons-compress/
[Jackson Databind]: http://wiki.fasterxml.com/JacksonHome
[JAXB]: https://javaee.github.io/jaxb-v2/
[Monoid]: https://en.wikipedia.org/wiki/Monoid
[XZ for Java]: https://tukaani.org/xz/
[Zstd-jni]: https://github.com/luben/zstd-jni
