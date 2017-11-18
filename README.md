# Fun I/O [![Maven Central](https://img.shields.io/maven-central/v/global.namespace.fun-io/fun-io-api.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22global.namespace.fun-io%22) [![Build Status](https://api.travis-ci.org/christian-schlichtherle/fun-io.svg)](https://travis-ci.org/christian-schlichtherle/fun-io)

Fun I/O provides functional, high level abstractions for composing ordinary input and output streams into loans, stores, 
transformations, codecs et al.
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

Note that the `encoder` object is virtually stateless, and hence reusable.

Here is a more realistic, yet incomplete example in Scala:

```scala
def ciphers(forOutput: java.lang.Boolean): javax.crypto.Cipher = ??? // needs to return an initialized cipher
val store: Store = pathStore(java.nio.file.Paths get "hello-world.gz.cipher")
val connectedCodec: ConnectedCodec = jsonCodec << gzip << cipher(ciphers _) << store
connectedCodec encode "Hello world!"
```

... and it's equivalent in Java: 

```java
import global.namespace.fun.io.api.function.*;

...

XFunction<Boolean, Cipher> ciphers = forOutput -> { throw new IOException("not implemented"); };
Store store = pathStore(java.nio.file.Paths.get("hello-world.gz.cipher"));
ConnectedCodec connectedCodec = jsonCodec().map(gzip()).map(cipher(ciphers)).connect(store);
connectedCodec.encode("Hello world!");

```

Note that an `XFunction` is like a `java.util.function.Function`, except it may throw an `Exception`. 

Assuming a complete implementation of the `ciphers` function, the preceding code would first encode the string 
`"Hello world!"` to JSON, then compress the result using the GZIP format, then encrypt the result using the cipher 
returned from a call to `ciphers.apply(true)` and finally save the result to the file `hello-world.gz.cipher`.

Again, note that the `store` and `connectedCodec` objects are virtually stateless, and hence reusable.

## Modules

Fun I/O has a modular architecture, providing the following modules:

+ `fun-io-api`: The API provides interfaces for loans, stores, transformations, codecs et al, but no implementations.
+ `fun-io-scala-api`: The Scala API wraps the Java API to enhance the syntax with a domain specific language (DSL).
+ `fun-io-bios`: The Basic I/O System (pun intended) provides essential implementations for encoding, transforming, 
  storing or streaming content.
+ `fun-io-jackson`: Depends on [Jackson Databind] to provide a JSON codec.
+ `fun-io-commons-compress`: Depends on [Apache Commons Compress] to provide many compression transformations. 
+ `fun-io-xz`: Depends on [XZ for Java] to provide the LZMA2 compression transformation.     

[Jackson Databind]: http://wiki.fasterxml.com/JacksonHome
[Apache Commons Compress]: https://commons.apache.org/proper/commons-compress/
[XZ for Java]: https://tukaani.org/xz/
