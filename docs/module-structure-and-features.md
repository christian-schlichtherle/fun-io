# Module Structure And Features

Fun I/O has a modular structure.
Its artifacts are hosted on Maven Central with the common group ID 
[`global.namespace.fun-io`](http://search.maven.org/#search%7Cga%7C1%7Cglobal.namespace.fun-io).
The following diagram shows the module structure:

[![Module Structure]][Module Structure]

## API Modules

API modules provide powerful abstractions for simple and safe synchronous I/O operations.

### Fun I/O API

::: tip
This module is a transitive dependency of all other modules, so you don't need to add it directly.
:::

The module
[`fun-io-api`](https://search.maven.org/search?q=g:global.namespace.fun-io%20AND%20a:fun-io-api)
provides essential abstractions like `Codec`, `Source`, `Sink`, `Store`, `ArchiveSource`, `ArchiveSink`, `ArchiveStore`, 
`Filter`, `Socket` et al.
All other modules (transitively) depend on this module.

### Fun I/O Scala API

::: tip
This module is completely optional: You should add it to Scala applications, however.
:::

The module
[`fun-io-scala-api`](https://search.maven.org/search?q=g:global.namespace.fun-io%20AND%20a:fun-io-scala-api_*)
extends the Java API with operators and implicit conversions to improve the development experience in Scala.

## Implementation Modules

Each implementation module provides a single facade class which contains many static factory methods for different 
implementations of the abstractions provided by the `fun-io-api` module.

### Fun I/O AWS

The module
[`fun-io-aws`](https://search.maven.org/search?q=g:global.namespace.fun-io%20AND%20a:fun-io-aws)
depends on the [AWS SDK for Java 2.0] to provide implementations of the Fun I/O API:

+ The `AWS` class is a facade which provides the following `ArchiveStore` functions:
  + `s3` provides read/write access to an AWS S3 bucket or any sub-tree of it.

### Fun I/O BIOS

::: tip
This is the _default implementation_ module: Most applications have a single dependency on this module.
If you are not sure where to find a feature, look here first.
:::

The module
[`fun-io-bios`](https://search.maven.org/search?q=g:global.namespace.fun-io%20AND%20a:fun-io-bios)
provides basic implementations of the Fun I/O API (BIOS stands for "Basic Input/Output System" - pun intended):

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
  + `url` reads the content of a URL.
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

### Fun I/O Commons Compress

::: tip
Use this module when processing archive files (JAR, 7zip, TAR, ZIP and derivatives).
It provides better performance and more accuracy than the module `fun-io-bios`.
:::

The module
[`fun-io-commons-compress`](https://search.maven.org/search?q=g:global.namespace.fun-io%20AND%20a:fun-io-commons-compress)
depends on [Apache Commons Compress] to provide implementations of the Fun I/O API:

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

### Fun I/O Jackson

The module
[`fun-io-jackson`](https://search.maven.org/search?q=g:global.namespace.fun-io%20AND%20a:fun-io-jackson)
depends on [Jackson Databind] to provide implementations of the Fun I/O API:
 
+ The `Jackson` class is a facade which provides the following `Codec` functions:
  + `json` marshals/unmarshals objects to/from JSON using Jackson Databind.

### Fun I/O JAXB

The module
[`fun-io-jaxb`](https://search.maven.org/search?q=g:global.namespace.fun-io%20AND%20a:fun-io-jaxb)
depends on [JAXB] to provide implementations of the Fun I/O API:

+ The `JAXB` class is a facade which provides the following `Codec` functions:
  + `xml` marshals/unmarshals objects to/from XML using the JAXB reference implementation.

### Fun I/O XZ

The module
[`fun-io-xz`](https://search.maven.org/search?q=g:global.namespace.fun-io%20AND%20a:fun-io-xz)
depends on [XZ for Java] to provide implementations of the Fun I/O API:

+ The `XZ` class is a facade which provides the following `Filter` functions:
  + `lzma` compresses/decompresses data using the LZMA format.
  + `lzma2` compresses/decompresses data using the LZMA2 format.
  + `xz` compresses/decompresses data using the XZ format.

### Fun I/O Zstd

The module
[`fun-io-zstd`](https://search.maven.org/search?q=g:global.namespace.fun-io%20AND%20a:fun-io-zstd)
depends on [Zstd-jni] to provide implementations of the Fun I/O API:

+ The `Zstd` class is a facade which provides the following `Filter` functions:
  + `zstd` compresses/decompresses data using the Zstd format.

## Application Modules

Application modules depend on the API and expect some implementation to be injected for a particular task.
Like implementation modules, they provide a single facade class containing static methods to solve their particular 
task.

### Fun I/O Delta

The module
[`fun-io-delta`](https://search.maven.org/search?q=g:global.namespace.fun-io%20AND%20a:fun-io-delta)
provides utility functions for diffing and patching archive files or directories:

+ The `Delta` class is a facade which provides the following utility functions:
  + `diff` compares two archive files or directories to compute a delta archive file or directory or model.
  + `patch` patches an archive file or directory with a delta archive file or directory.

[Apache Commons Compress]: https://commons.apache.org/proper/commons-compress/
[Jackson Databind]: http://wiki.fasterxml.com/JacksonHome
[JAXB]: https://javaee.github.io/jaxb-v2/
[Monoid]: https://en.wikipedia.org/wiki/Monoid
[XZ for Java]: https://tukaani.org/xz/
[Zstd-jni]: https://github.com/luben/zstd-jni
[Module Structure]: /fun-io/module-structure.svg
[AWS SDK for Java 2.0]: https://github.com/aws/aws-sdk-java-v2
