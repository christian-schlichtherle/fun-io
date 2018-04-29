---
title: Basic Usage
---

First of all you need to decide on the set of features required by your application and add their respective modules to 
its class path - see [Module Structure And Features]({{ site.baseurl }}{% link module-structure-and-features.md %}).
A Java application typically has a single dependency on `fun-io-bios`.
A Scala application typically has the same dependencies as a Java application plus an additional dependency on
`fun-io-scala-api` to improve the development experience in Scala. 

The examples on this page depend on `fun-io-bios`, `fun-io-jackson`, `fun-io-scala-api` and, transitively, `fun-io-api`.
Assuming Maven is used for Java and SBT for Scala, you need to add the following to your project configuration:

<nav>
  <div class="nav nav-tabs" role="tablist">
    <a class="nav-item nav-link active" id="maven-tab" data-toggle="tab" href="#maven" role="tab" aria-controls="maven" aria-selected="true">Maven</a>
    <a class="nav-item nav-link" id="sbt-tab" data-toggle="tab" href="#sbt" role="tab" aria-controls="sbt" aria-selected="false">SBT</a>
  </div>
</nav>
<div class="tab-content">
  <div class="tab-pane active" id="maven" role="tabpanel" aria-labelledby="maven-tab">

{% highlight xml %}
<dependencies>
    <dependency>
        <groupId>global.namespace.fun-io</groupId>
        <artifactId>fun-io-bios</artifactId>
        <version>1.1.0</version>
    </dependency>
    <dependency>
        <groupId>global.namespace.fun-io</groupId>
        <artifactId>fun-io-jackson</artifactId>
        <version>1.1.0</version>
    </dependency>
</dependencies>
{% endhighlight %}

  </div>
  <div class="tab-pane" id="sbt" role="tabpanel" aria-labelledby="sbt-tab">

{% highlight scala %}
libraryDependencies ++= Seq(
  "global.namespace.fun-io" % "fun-io-bios" % "1.1.0",
  "global.namespace.fun-io" % "fun-io-jackson" % "1.1.0",
  "global.namespace.fun-io" %% "fun-io-scala-api" % "1.1.0"
)
{% endhighlight %}

  </div>
</div>

Then, the following code prints `"Hello world!"` - including the quotes:

<nav>
  <div class="nav nav-tabs" role="tablist">
    <a class="nav-item nav-link active" id="java1-tab" data-toggle="tab" href="#java1" role="tab" aria-controls="java1" aria-selected="true">Java</a>
    <a class="nav-item nav-link" id="scala1-tab" data-toggle="tab" href="#scala1" role="tab" aria-controls="scala1" aria-selected="false">Scala</a>
  </div>
</nav>
<div class="tab-content">
  <div class="tab-pane active" id="java1" role="tabpanel" aria-labelledby="java1-tab">

{% highlight java %}
import global.namespace.fun.io.api.*;                    // from `fun-io-api`

import static global.namespace.fun.io.bios.BIOS.*;       // from `fun-io-bios`
import static global.namespace.fun.io.jackson.Jackson.*; // from `fun-io-jackson`

Encoder encoder = json().encoder(stdout());
encoder.encode("Hello world!");
{% endhighlight %}

  </div>
  <div class="tab-pane" id="scala1" role="tabpanel" aria-labelledby="scala1-tab">

{% highlight scala %}
import global.namespace.fun.io.api._             // from `fun-io-api`
import global.namespace.fun.io.bios.BIOS._       // from `fun-io-bios`
import global.namespace.fun.io.jackson.Jackson._ // from `fun-io-jackson`

val encoder: Encoder = json encoder stdout
encoder encode "Hello world!"
{% endhighlight %}

  </div>
</div>

The preceding code encodes the string `"Hello world!` to JSON and writes it to `System.out`.
The call to `stdout()` wraps `System.out` in a socket which ignores any call to the `OutputStream.close()` method as it 
would be inappropriate to do that on `System.out`.
The `stream` function allows to do the same for any given `InputStream` or `OutputStream`.
Note that the `encoder` object is virtually stateless, and hence reusable.

Here is a more realistic, yet incomplete example:

<nav>
  <div class="nav nav-tabs" role="tablist">
    <a class="nav-item nav-link active" id="java2-tab" data-toggle="tab" href="#java2" role="tab" aria-controls="java2" aria-selected="true">Java</a>
    <a class="nav-item nav-link" id="scala2-tab" data-toggle="tab" href="#scala2" role="tab" aria-controls="scala2" aria-selected="false">Scala</a>
  </div>
</nav>
<div class="tab-content">
  <div class="tab-pane active" id="java2" role="tabpanel" aria-labelledby="java2-tab">

{% highlight java %}
import global.namespace.fun.io.api.*;                    // from `fun-io-api`
import global.namespace.fun.io.api.function.*;
import java.nio.file.Paths;
import javax.crypto.Cipher;

import static global.namespace.fun.io.bios.BIOS.*;       // from `fun-io-bios`
import static global.namespace.fun.io.jackson.Jackson.*; // from `fun-io-jackson`

// XFunction is like a regular `Function`, except that it may throw an `Exception`:
XFunction<Boolean, Cipher> ciphers = outputMode -> { throw new UnsupportedOperationException("TODO"); };

Store store = file(Paths.get("hello-world.gz.cipher"));

ConnectedCodec connectedCodec = json().map(gzip()).map(cipher(ciphers)).connect(store);
connectedCodec.encode("Hello world!");
{% endhighlight %}

  </div>
  <div class="tab-pane" id="scala2" role="tabpanel" aria-labelledby="scala2-tab">

{% highlight scala %}
import global.namespace.fun.io.api._             // from `fun-io-api`
import global.namespace.fun.io.bios.BIOS._       // from `fun-io-bios`
import global.namespace.fun.io.jackson.Jackson._ // from `fun-io-jackson`
import global.namespace.fun.io.scala.api._       // from `fun-io-scala-api`
import java.nio.file.Paths
import javax.crypto.Cipher

def ciphers(outputMode: Boolean): Cipher = ??? // needs to return an initialized cipher

val store: Store = file(Paths get "hello-world.gz.cipher")

// The `<<` operator is associative: 
val connectedCodec: ConnectedCodec = json << gzip << cipher(ciphers _) << store
connectedCodec encode "Hello world!"
{% endhighlight %}

  </div>
</div>

Assuming a complete implementation of the `ciphers` function, the preceding code would first encode the string 
`"Hello world!"` to JSON, then compress the result using the GZIP format, then encrypt the result using a cipher 
returned from an internal call to `ciphers.apply(true)` and finally save the result to the file `hello-world.gz.cipher`.
Again, note that the `store` and `connectedCodec` objects are virtually stateless, and hence reusable.

<div class="btn-group d-flex justify-content-center" role="group" aria-label="Pagination">
  <button type="button" class="btn btn-light"><a href="{{ site.baseurl }}{% link module-structure-and-features.md %}">&laquo; Module Structure And Features</a></button>
  <button type="button" class="btn btn-light"><a href="{{ site.baseurl }}{% link basic-archive-processing.md %}">Basic Archive Processing &raquo;</a></button>
</div>
