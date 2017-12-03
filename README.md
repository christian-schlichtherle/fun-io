# Fun I/O [![Maven Central](https://img.shields.io/maven-central/v/global.namespace.fun-io/fun-io-api.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22global.namespace.fun-io%22) [![Build Status](https://api.travis-ci.org/christian-schlichtherle/fun-io.svg)](https://travis-ci.org/christian-schlichtherle/fun-io)

Fun I/O provides functional, high level abstractions for composing ordinary input and output streams into sockets, 
stores, transformations, codecs et al.
The resulting compositions are (re)usable, versatile and dont leak resources.
Fun I/O supports Java 8 and Scala 2.10, 2.11 and 2.12 and comes with the Apache License, version 2.0.

## Examples

The following Scala code prints `"Hello world!"` - including the quotes:

```scala
import global.namespace.fun.io.bios.BIOS._       // from module `fun-io-bios`
import global.namespace.fun.io.jackson.Jackson._ // from module `fun-io-jackson`
import global.namespace.fun.io.scala.api._       // from module `fun-io-scala-api`

val encoder: Encoder = jsonCodec encoder stream(System.out)
encoder encode "Hello world!"
```

Here's the equivalent in Java:

```java
import global.namespace.fun.io.api.*;
import static global.namespace.fun.io.bios.BIOS.*;
import static global.namespace.fun.io.jackson.Jackson.*;

...

Encoder encoder = jsonCodec().encoder(stream(System.out));
encoder.encode("Hello world!");
```

The preceding code would encode the string `"Hello world!` to JSON and write it to `System.out`.
The call to `stream(System.out)` wraps it in a socket which ignores any call to the `OutputStream.close()` method as it 
would be inappropriate to do that on `System.out`.
 
Note that the `encoder` object is virtually stateless, and hence reusable.

Here is a more realistic, yet incomplete example in Scala:

```scala
def ciphers(forOutput: java.lang.Boolean): javax.crypto.Cipher = ??? // needs to return an initialized cipher
val store: Store = pathStore(java.nio.file.Paths get "hello-world.gz.cipher")
val connectedCodec: ConnectedCodec = jsonCodec << gzip << cipher(ciphers _) << store
connectedCodec encode "Hello world!"
```

Note that the `<<` operator is associative.

Here's the equivalent in Java: 

```java
import global.namespace.fun.io.api.function.*;

...

XFunction<Boolean, Cipher> ciphers = forOutput -> { throw new IOException("not implemented"); };
Store store = pathStore(java.nio.file.Paths.get("hello-world.gz.cipher"));
ConnectedCodec connectedCodec = jsonCodec().map(gzip()).map(cipher(ciphers)).connect(store);
connectedCodec.encode("Hello world!");

```

Note that an `XFunction` is like a `java.util.function.Function`, except that it may throw an `Exception`. 

Assuming a complete implementation of the `ciphers` function, the preceding code would first encode the string 
`"Hello world!"` to JSON, then compress the result using the GZIP format, then encrypt the result using a cipher 
returned from an internal call to `ciphers.apply(true)` and finally save the result to the file `hello-world.gz.cipher`.

Again, note that the `store` and `connectedCodec` objects are virtually stateless, and hence reusable.

## Modules

Fun I/O has a modular architecture, providing the following modules:

+ `fun-io-api`: The API provides interfaces for sockets, stores, transformations, codecs et al, but no implementations.
+ `fun-io-scala-api`: The Scala API wraps the Java API to enhance the syntax with a domain specific language (DSL).
+ `fun-io-bios`: The Basic Input/Output System (pun intended) provides basic implementations for encoding, transforming, 
  storing or streaming data.
  + The `BIOS` class provides the following `Codec` functions:
    + `jaxbCodec` marshals/unmarshals objects to/from XML using JAXB.
    + `serializationCodec` serializes/deserializes objects using `ObjectOutputStream`/`ObjectInputStream`.
    + `xmlCodec` encodes/decodes objects using `XMLEncoder`/`XMLDecoder`.
  + The following `Transformation` functions are provided in the same class:
    + `base64` encodes/decodes data to/from Base64.
    + `buffer` buffers I/O operations.
    + `cipher` encrypts/decrypts data using a function which provides initialized `javax.security.Cipher` objects.
    + `deflate` deflates/inflates data using the ZIP compression.
    + `gzip` compresses/decompresses data using the GZIP compression format.
    + `identity` is a no-op, forming transformations into a [Monoid].
    + `inflate` inflates/deflates data using the ZIP compression.
    + `inverse` inverses a given transformation by buffering the entire data to a buffer, e.g. on the heap.
    + `rot` provides the (in)famous ROT transformation, e.g. [ROT13].
  + The following `Store` functions are provided:
    + `memoryStore` stores data on the heap. This is primarily used for cloning objects or testing. 
    + `pathStore` stores data in files or any other `java.nio.file.Path`.
    + `preferencesStore` stores data in preferences nodes using a given key.
  + For streaming `InputStream`/`OutputStream`, the `stream` functions are provided.
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
  + `jsonCodec` marshals/unmarshals objects to/from JSON using Jackson.
+ `fun-io-xz`: Depends on [XZ for Java] to provide the following compression `Transformation` functions in the `XZ` 
  class:
  + `lzma2` compresses/decompresses data using the LZMA2 compression format.
  + `xz` compresses/decompresses data using the XZ compression format.

[Apache Commons Compress]: https://commons.apache.org/proper/commons-compress/
[Jackson Databind]: http://wiki.fasterxml.com/JacksonHome
[Monoid]: https://en.wikipedia.org/wiki/Monoid
[ROT13]: https://en.wikipedia.org/wiki/ROT13
[XZ for Java]: https://tukaani.org/xz/
