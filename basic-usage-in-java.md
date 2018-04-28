---
title: Basic Usage in Java
---

First of all, you need to add the modules providing the respective feature to your class path.
The examples on this page depend on `fun-io-bios`, `fun-io-jackson` and, transitively, `fun-io-api`.
Assuming Maven, you need to add the following to the `pom.xml`:

{% highlight xml %}
<dependencies>
    <dependency>
        <groupId>global.namespace.fun-io</groupId>
        <artifactId>fun-io-bios</artifactId>
        <version>0.13.0</version>
    </dependency>
    <dependency>
        <groupId>global.namespace.fun-io</groupId>
        <artifactId>fun-io-jackson</artifactId>
        <version>0.13.0</version>
    </dependency>
</dependencies>
{% endhighlight %}

Then, the following JShell code prints `"Hello world!"` - including the quotes:

{% highlight java %}
import global.namespace.fun.io.api.*;                    // from `fun-io-api`

import static global.namespace.fun.io.bios.BIOS.*;       // from `fun-io-bios`
import static global.namespace.fun.io.jackson.Jackson.*; // from `fun-io-jackson`

Encoder encoder = json().encoder(stdout());
encoder.encode("Hello world!");
{% endhighlight %}

The preceding code encodes the string `"Hello world!` to JSON and writes it to `System.out`.
The call to `stdout()` wraps `System.out` in a socket which ignores any call to the `OutputStream.close()` method as it 
would be inappropriate to do that on `System.out`.
The `stream` function allows to do the same for any given `InputStream` or `OutputStream`.
Note that the `encoder` object is virtually stateless, and hence reusable.

Here is a more realistic, yet incomplete example:

{% highlight java %}
import global.namespace.fun.io.api.*;                    // from `fun-io-api`
import global.namespace.fun.io.api.function.*;
import java.nio.file.Paths;

import static global.namespace.fun.io.bios.BIOS.*;       // from `fun-io-bios`
import static global.namespace.fun.io.jackson.Jackson.*; // from `fun-io-jackson`

XFunction<Boolean, Cipher> ciphers = outputMode -> { throw new UnsupportedOperationException("TODO"); };
Store store = pathStore(Paths get "hello-world.gz.cipher");
ConnectedCodec connectedCodec = json().map(gzip()).map(cipher(ciphers)).connect(store);
connectedCodec.encode("Hello world!");
{% endhighlight %}

Note that an `XFunction` is like a `java.util.function.Function`, except that it may throw an `Exception`. 

Assuming a complete implementation of the `ciphers` function, the preceding code would first encode the string 
`"Hello world!"` to JSON, then compress the result using the GZIP format, then encrypt the result using a cipher 
returned from an internal call to `ciphers.apply(true)` and finally save the result to the file `hello-world.gz.cipher`.
Again, note that the `store` and `connectedCodec` objects are virtually stateless, and hence reusable.

**&laquo;** [Module Structure And Features]({{ site.baseurl }}{% link module-structure-and-features.md %})
**&raquo;** [Basic Usage In Scala]({{ site.baseurl }}{% link basic-usage-in-scala.md %})
