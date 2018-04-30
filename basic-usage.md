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

Then, the following code encodes the string `"Hello world!"` to JSON and writes it to standard output - including the 
quotes:

<nav>
  <div class="nav nav-tabs" role="tablist">
    <a class="nav-item nav-link active" id="java1-tab" data-toggle="tab" href="#java1" role="tab" aria-controls="java1" aria-selected="true">Java</a>
    <a class="nav-item nav-link" id="scala1-tab" data-toggle="tab" href="#scala1" role="tab" aria-controls="scala1" aria-selected="false">Scala</a>
  </div>
</nav>
<div class="tab-content">
  <div class="tab-pane active" id="java1" role="tabpanel" aria-labelledby="java1-tab">

{% highlight java %}
import global.namespace.fun.io.api.Encoder;                 // from `fun-io-api`

import static global.namespace.fun.io.bios.BIOS.stdout;     // from `fun-io-bios`
import static global.namespace.fun.io.jackson.Jackson.json; // from `fun-io-jackson`

class Scratch {
    public static void main(String[] args) throws Exception {
        Encoder encoder = json().encoder(stdout());
        encoder.encode("Hello world!");
    }
}
{% endhighlight %}

  </div>
  <div class="tab-pane" id="scala1" role="tabpanel" aria-labelledby="scala1-tab">

{% highlight scala %}
import global.namespace.fun.io.api.Encoder          // from `fun-io-api`
import global.namespace.fun.io.bios.BIOS.stdout     // from `fun-io-bios`
import global.namespace.fun.io.jackson.Jackson.json // from `fun-io-jackson`

val encoder: Encoder = json encoder stdout
encoder encode "Hello world!"
{% endhighlight %}

  </div>
</div>

Fun I/O employs a few simple design principles:

+ The API is defined by abstract classes and interfaces in the module `fun-io-api`.
+ The module `fun-io-scala-api` adds operators and implicit conversions for an enhanced development experience in Scala.
+ Each implementation module provides a single facade class which consists of one or more static factory methods.
+ Each static factory method returns an instance of a class or interface defined by the API without revealing the actual 
  implementation class.
+ Each implementation is virtually stateless, and hence reusable and trivially thread-safe.

With this design, the canonical way of using Fun I/O is to import all static factory methods from one or more facade 
classes.
In the previous example, the `BIOS` facade class provides the `stdout()` factory method which returns an instance of the 
`Sink` interface.
The `Jackson` facade class provides the `json()` factory method which returns an instance of the `Codec` interface.
The codec and the sink are then combined into an instance of the `Encoder` interface, which is subsequently used to 
encode the string `"Hello world!"`.

Here is a slightly more complex example:

<nav>
  <div class="nav nav-tabs" role="tablist">
    <a class="nav-item nav-link active" id="java2-tab" data-toggle="tab" href="#java2" role="tab" aria-controls="java2" aria-selected="true">Java</a>
    <a class="nav-item nav-link" id="scala2-tab" data-toggle="tab" href="#scala2" role="tab" aria-controls="scala2" aria-selected="false">Scala</a>
  </div>
</nav>
<div class="tab-content">
  <div class="tab-pane active" id="java2" role="tabpanel" aria-labelledby="java2-tab">

{% highlight java %}
import global.namespace.fun.io.api.Encoder;
import global.namespace.fun.io.api.Store;

import java.nio.file.Paths;

import static global.namespace.fun.io.bios.BIOS.file;
import static global.namespace.fun.io.bios.BIOS.gzip;
import static global.namespace.fun.io.jackson.Jackson.json;

class Scratch {
    public static void main(String[] args) throws Exception {
        Store store = file(Paths.get("hello-world.gz"));
        Encoder encode = json().map(gzip()).encoder(store);
        encode.encode("Hello world!");
    }
}
{% endhighlight %}

  </div>
  <div class="tab-pane" id="scala2" role="tabpanel" aria-labelledby="scala2-tab">

{% highlight scala %}
import java.nio.file.Paths

import global.namespace.fun.io.api.{Encoder, Store}
import global.namespace.fun.io.bios.BIOS.{file, gzip}
import global.namespace.fun.io.jackson.Jackson.json
import global.namespace.fun.io.scala.api._

val store: Store = file(Paths get "hello-world.gz")
val encoder: Encoder = json << gzip encoder store
encoder encode "Hello world!"
{% endhighlight %}

  </div>
</div>

The preceding code encodes the string `"Hello world!"` to JSON, compresses it using GZIP and writes the result to the 
file `hello-world.gz`.
Note that the expression `json().map(gzip())` actually _transforms_ the JSON codec with the GZIP filter into a new 
codec.
In Scala, this expression can be more concisely written as `json << gzip`. 
As you can see, the design concept still holds and the resulting code is only slightly more complex.

Creating an encoder from a transformed codec and a store is nice, but what if you want to read back something from the
store?
In this case, it may be more appropriate to create a `ConnectedCodec` instead of an `Encoder` like so:

<nav>
  <div class="nav nav-tabs" role="tablist">
    <a class="nav-item nav-link active" id="java3-tab" data-toggle="tab" href="#java3" role="tab" aria-controls="java3" aria-selected="true">Java</a>
    <a class="nav-item nav-link" id="scala3-tab" data-toggle="tab" href="#scala3" role="tab" aria-controls="scala3" aria-selected="false">Scala</a>
  </div>
</nav>
<div class="tab-content">
  <div class="tab-pane active" id="java3" role="tabpanel" aria-labelledby="java3-tab">
 
{% highlight java %}
import global.namespace.fun.io.api.ConnectedCodec;
import global.namespace.fun.io.api.Store;

import java.nio.file.Paths;

import static global.namespace.fun.io.bios.BIOS.file;
import static global.namespace.fun.io.bios.BIOS.gzip;
import static global.namespace.fun.io.jackson.Jackson.json;

class Scratch {
    public static void main(String[] args) throws Exception {
        Store store = file(Paths.get("hello-world.gz"));
        ConnectedCodec codec = json().map(gzip()).connect(store);
        codec.encode("Hello world!");
        String clone = codec.decode(String.class);
        assert clone.equals("Hello world!");
    }
}
{% endhighlight %}

  </div>
  <div class="tab-pane" id="scala3" role="tabpanel" aria-labelledby="scala3-tab">

{% highlight scala %}
import java.nio.file.Paths

import global.namespace.fun.io.api.{ConnectedCodec, Store}
import global.namespace.fun.io.bios.BIOS.{file, gzip}
import global.namespace.fun.io.jackson.Jackson.json
import global.namespace.fun.io.scala.api._

val store: Store = file(Paths get "hello-world.gz")
val codec: ConnectedCodec = json << gzip << store
codec encode "Hello world!"
val clone: String = codec decode classOf[String]
assert(clone == "Hello world!")
{% endhighlight %}

  </div>
</div>

A `ConnectedCodec` is an `Encoder` and a `Decoder` in one, so it can be used to create a deep clone of the original 
object.
To make this more useful, the `gzip()` filter and the `file(...)` store can be removed and the encoded data get buffered 
on the heap instead:

<nav>
  <div class="nav nav-tabs" role="tablist">
    <a class="nav-item nav-link active" id="java4-tab" data-toggle="tab" href="#java4" role="tab" aria-controls="java4" aria-selected="true">Java</a>
    <a class="nav-item nav-link" id="scala4-tab" data-toggle="tab" href="#scala4" role="tab" aria-controls="scala4" aria-selected="false">Scala</a>
  </div>
</nav>
<div class="tab-content">
  <div class="tab-pane active" id="java4" role="tabpanel" aria-labelledby="java4-tab">
 
{% highlight java %}
import global.namespace.fun.io.api.ConnectedCodec;
import global.namespace.fun.io.api.Store;

import java.nio.file.Paths;

import static global.namespace.fun.io.bios.BIOS.file;
import static global.namespace.fun.io.bios.BIOS.gzip;
import static global.namespace.fun.io.jackson.Jackson.json;

class Scratch {
    public static void main(String[] args) throws Exception {
        ConnectedCodec codec = json().connect(memory());
        codec.encode("Hello world!");
        String clone = codec.decode(String.class);
        assert clone.equals("Hello world!");
    }
}
{% endhighlight %}

  </div>
  <div class="tab-pane" id="scala4" role="tabpanel" aria-labelledby="scala4-tab">

{% highlight scala %}
import java.nio.file.Paths

import global.namespace.fun.io.api.{ConnectedCodec, Store}
import global.namespace.fun.io.bios.BIOS.{file, gzip}
import global.namespace.fun.io.jackson.Jackson.json
import global.namespace.fun.io.scala.api._

val codec: ConnectedCodec = json << memory
codec encode "Hello world!"
val clone: String = codec decode classOf[String]
assert(clone == "Hello world!")
{% endhighlight %}

  </div>
</div>

This can be simplified as follows:

<nav>
  <div class="nav nav-tabs" role="tablist">
    <a class="nav-item nav-link active" id="java5-tab" data-toggle="tab" href="#java5" role="tab" aria-controls="java5" aria-selected="true">Java</a>
    <a class="nav-item nav-link" id="scala5-tab" data-toggle="tab" href="#scala5" role="tab" aria-controls="scala5" aria-selected="false">Scala</a>
  </div>
</nav>
<div class="tab-content">
  <div class="tab-pane active" id="java5" role="tabpanel" aria-labelledby="java5-tab">
 
{% highlight java %}
import global.namespace.fun.io.api.ConnectedCodec;
import global.namespace.fun.io.api.Store;

import java.nio.file.Paths;

import static global.namespace.fun.io.bios.BIOS.file;
import static global.namespace.fun.io.bios.BIOS.gzip;
import static global.namespace.fun.io.jackson.Jackson.json;

class Scratch {
    public static void main(String[] args) throws Exception {
        ConnectedCodec codec = json().connect(memory());
        String clone = codec.clone("Hello world!");
        assert clone.equals("Hello world!");
    }
}
{% endhighlight %}

  </div>
  <div class="tab-pane" id="scala5" role="tabpanel" aria-labelledby="scala5-tab">

{% highlight scala %}
import java.nio.file.Paths

import global.namespace.fun.io.api.{ConnectedCodec, Store}
import global.namespace.fun.io.bios.BIOS.{file, gzip}
import global.namespace.fun.io.jackson.Jackson.json
import global.namespace.fun.io.scala.api._

val codec: ConnectedCodec = json << memory
val clone: String = codec clone "Hello world!"
assert(clone == "Hello world!")
{% endhighlight %}

  </div>
</div>

Because deep-cloning is a standard use case, there is a ready-made method in the BIOS facade for it:

<nav>
  <div class="nav nav-tabs" role="tablist">
    <a class="nav-item nav-link active" id="java6-tab" data-toggle="tab" href="#java6" role="tab" aria-controls="java6" aria-selected="true">Java</a>
    <a class="nav-item nav-link" id="scala6-tab" data-toggle="tab" href="#scala6" role="tab" aria-controls="scala6" aria-selected="false">Scala</a>
  </div>
</nav>
<div class="tab-content">
  <div class="tab-pane active" id="java6" role="tabpanel" aria-labelledby="java6-tab">
 
{% highlight java %}
import global.namespace.fun.io.bios.BIOS;

class Scratch {
    public static void main(String[] args) throws Exception {
        String c = BIOS.clone("Hello world!");
        assert c.equals("Hello world!");
    }
}
{% endhighlight %}

  </div>
  <div class="tab-pane" id="scala6" role="tabpanel" aria-labelledby="scala6-tab">

{% highlight scala %}
import global.namespace.fun.io.bios.BIOS.clone

val c: String = clone("Hello world!")
assert(c == "Hello world!")
{% endhighlight %}

  </div>
</div>

In contrast to the previous examples, this method uses `BIOS.serialization()` instead of `Jackson.json()` as the 
`Codec`, so the object to clone must implement `java.io.Serializable`, which `java.lang.String` does.

<div class="btn-group d-flex justify-content-center" role="group" aria-label="Pagination">
  <button type="button" class="btn btn-light"><a href="{{ site.baseurl }}{% link module-structure-and-features.md %}">&laquo; Module Structure And Features</a></button>
  <button type="button" class="btn btn-light"><a href="{{ site.baseurl }}{% link basic-archive-processing.md %}">Basic Archive Processing &raquo;</a></button>
</div>
