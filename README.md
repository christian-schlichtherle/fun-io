# Fun I/O [![Maven Central](https://img.shields.io/maven-central/v/global.namespace.fun-io/fun-io-api.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22global.namespace.fun-io%22) [![Build Status](https://api.travis-ci.org/christian-schlichtherle/fun-io.svg)](https://travis-ci.org/christian-schlichtherle/fun-io)

Fun I/O provides functional, high level abstractions for composing ordinary input and output streams into sockets, 
stores, transformations, codecs et al.
The resulting compositions are (re)usable, composable and never leak resources.
Fun I/O supports Java 8 and Scala 2.10, 2.11 and 2.12 and comes with the Apache License, version 2.0.

## Usage

The following Scala code prints `"Hello world!"` - including the quotes:

```scala
import global.namespace.fun.io.bios.BIOS._       // from module `fun-io-bios`
import global.namespace.fun.io.jackson.Jackson._ // from module `fun-io-jackson`
import global.namespace.fun.io.scala.api._       // from module `fun-io-scala-api`

val encoder: Encoder = json encoder stdout
encoder encode "Hello world!"
```

Here's the equivalent in Java:

```java
import global.namespace.fun.io.api.*;
import static global.namespace.fun.io.bios.BIOS.*;
import static global.namespace.fun.io.jackson.Jackson.*;

...

Encoder encoder = json().encoder(stdout());
encoder.encode("Hello world!");
```

The preceding code would encode the string `"Hello world!` to JSON and write it to `System.out`.
The call to `stdout()` wraps `System.out` in a socket which ignores any call to the `OutputStream.close()` method as it 
would be inappropriate to do that on `System.out`.
The `stream` function allows to do the same for any given `InputStream` or `OutputStream`.
 
Note that the `encoder` object is virtually stateless, and hence reusable.

Here is a more realistic, yet incomplete example in Scala:

```scala
import java.nio.file.Paths

def ciphers(forOutput: Boolean): javax.crypto.Cipher = ??? // needs to return an initialized cipher
val store: Store = path(Paths get "hello-world.gz.cipher")
val connectedCodec: ConnectedCodec = json << gzip << cipher(ciphers _) << store
connectedCodec encode "Hello world!"
```

Note that the `<<` operator is associative.

Here's the equivalent in Java: 

```java
import global.namespace.fun.io.api.function.*;
import java.nio.file.Paths;

...

XFunction<Boolean, Cipher> ciphers = forOutput -> { throw new IOException("not implemented"); };
Store store = pathStore(Paths get "hello-world.gz.cipher");
ConnectedCodec connectedCodec = json().map(gzip()).map(cipher(ciphers)).connect(store);
connectedCodec.encode("Hello world!");

```

Note that an `XFunction` is like a `java.util.function.Function`, except that it may throw an `Exception`. 

Assuming a complete implementation of the `ciphers` function, the preceding code would first encode the string 
`"Hello world!"` to JSON, then compress the result using the GZIP format, then encrypt the result using a cipher 
returned from an internal call to `ciphers.apply(true)` and finally save the result to the file `hello-world.gz.cipher`.

Again, note that the `store` and `connectedCodec` objects are virtually stateless, and hence reusable.

## Module Structure

Fun I/O has a modular structure.
Its artifacts are hosted on Maven Central with the common group ID 
[`global.namespace.fun-io`](http://search.maven.org/#search%7Cga%7C1%7Cglobal.namespace.fun-io).
The following diagram shows the module structure:

![Module Structure](module-structure.svg)

The modules are:

+ `fun-io-api`: The API provides interfaces for sockets, stores, transformations, codecs et al, but no implementations.
+ `fun-io-scala-api`: The Scala API wraps the Java API to enhance the syntax with a domain specific language (DSL).
+ `fun-io-bios`: The Basic Input/Output System (pun intended) provides basic implementations for encoding, transforming, 
  storing or streaming data.
  + The `BIOS` class provides the following `Codec` functions:
    + `serialization` serializes/deserializes objects using `ObjectOutputStream`/`ObjectInputStream`.
    + `xml` encodes/decodes objects using `XMLEncoder`/`XMLDecoder`.
  + The `BIOS` class also provides the following `Transformation` functions:
    + `base64` encodes/decodes data to/from Base64.
    + `buffer` buffers I/O operations.
    + `cipher` encrypts/decrypts data using a function which provides initialized `javax.security.Cipher` objects.
    + `deflate` deflates/inflates data using the ZIP compression.
    + `gzip` compresses/decompresses data using the GZIP compression format.
    + `identity` is a no-op, forming transformations into a [Monoid].
    + `inflate` inflates/deflates data using the ZIP compression.
    + `inverse` inverses a given transformation by buffering the entire data to a buffer, e.g. on the heap.
    + `rot` provides the (in)famous ROT transformation, e.g. [ROT13].
  + The `BIOS` class also provides the following `Store` functions:
    + `file` stores data in a file, based on `java.io.File`. 
    + `memory` stores data on the heap. This is primarily used for cloning objects or testing.
    + `path` stores data in a files or any other path, based on `java.nio.file.Path`.
    + `preferences` stores data in a preferences node using a given key.
    + `systemPreferences` stores data in a system preferences nodes representing a given class.
    + `userPreferences` stores data in a user preferences nodes representing a given class.
  + The `BIOS` class also provides the following utility functions:
    + `stream` encapsulates a given `InputStream` or `OutputStream` as a `Source` or `Sink` for interoperability with 
      the rest of this API.
    + `copy` is a high performance algorithm for copying data from a `Source` to a `Sink`, including `Store`s. 
+ `fun-io-commons-compress`: Depends on [Apache Commons Compress] to provide the following compression `Transformation` 
  functions in the `CommonsCompress` class:
  + `blockLZ4` compresses/decompresses data using the LZ4 block format.
  + `bzip2` compresses decompresses data using the BZIP2 format.
  + `deflate` deflates/inflates data using the ZIP compression.
  + `framedLZ4` compresses/decompresses data using the LZ4 frame format.
  + `framedSnappy` compresses/decompresses data using the Snappy frame format.
  + `gzip` compresses/decompresses data using the GZIP compression format.
  + `lzma` compresses/decompresses data using the LZMA compression format.
  + `lzma2` compresses/decompresses data using the LZMA2 compression format.
+ `fun-io-jackson`: Depends on [Jackson Databind] to provide the following `Codec` functions in the `Jackson` class:
  + `json` marshals/unmarshals objects to/from JSON using Jackson.
+ `fun-io-jaxb`: Depends on [JAXB] to provide the following `Codec` functions in the `JAXB` class:
  + `xml` marshals/unmarshals objects to/from XML using JAXB.
+ `fun-io-xz`: Depends on [XZ for Java] to provide the following compression `Transformation` functions in the `XZ` 
  class:
  + `lzma2` compresses/decompresses data using the LZMA2 compression format.
  + `xz` compresses/decompresses data using the XZ compression format.

A typical Java application has a single dependency on `fun-io-bios`.
Additional module dependencies may be added to the mix for their features.

A typical Scala application has the same dependency/dependencies as a Java application plus an additional dependency on
`fun-io-scala-api` to improve the accessibility of Fun I/O in Scala code. 

[Apache Commons Compress]: https://commons.apache.org/proper/commons-compress/
[Jackson Databind]: http://wiki.fasterxml.com/JacksonHome
[JAXB]: https://javaee.github.io/jaxb-v2/
[Monoid]: https://en.wikipedia.org/wiki/Monoid
[ROT13]: https://en.wikipedia.org/wiki/ROT13
[XZ for Java]: https://tukaani.org/xz/
