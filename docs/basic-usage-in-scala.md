---
---

# Basic Usage in Scala

The following Scala code prints `"Hello world!"` - including the quotes:

```scala
import global.namespace.fun.io.api._             // from `fun-io-api`
import global.namespace.fun.io.bios.BIOS._       // from `fun-io-bios`
import global.namespace.fun.io.jackson.Jackson._ // from `fun-io-jackson`

val encoder: Encoder = json encoder stdout
encoder encode "Hello world!"
```

The preceding code encodes the string `"Hello world!` to JSON and writes it to `System.out`.
The call to `stdout()` wraps `System.out` in a socket which ignores any call to the `OutputStream.close()` method as it 
would be inappropriate to do that on `System.out`.
The `stream` function allows to do the same for any given `InputStream` or `OutputStream`.
Note that the `encoder` object is virtually stateless, and hence reusable.

Here is a more realistic, yet incomplete example:

```scala
import global.namespace.fun.io.api._             // from `fun-io-api`
import global.namespace.fun.io.bios.BIOS._       // from `fun-io-bios`
import global.namespace.fun.io.jackson.Jackson._ // from `fun-io-jackson`
import global.namespace.fun.io.scala.api._       // from `fun-io-scala-api`
import java.nio.file.Paths

def ciphers(outputMode: Boolean): javax.crypto.Cipher = ??? // needs to return an initialized cipher
val store: Store = path(Paths get "hello-world.gz.cipher")
val connectedCodec: ConnectedCodec = json << gzip << cipher(ciphers _) << store
connectedCodec encode "Hello world!"
```

Note that the `<<` operator is associative and an `XFunction` is like a `java.util.function.Function`, except that it 
may throw an `Exception`. 

Assuming a complete implementation of the `ciphers` function, the preceding code would first encode the string 
`"Hello world!"` to JSON, then compress the result using the GZIP format, then encrypt the result using a cipher 
returned from an internal call to `ciphers.apply(true)` and finally save the result to the file `hello-world.gz.cipher`.
Again, note that the `store` and `connectedCodec` objects are virtually stateless, and hence reusable.
