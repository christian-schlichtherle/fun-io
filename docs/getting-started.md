# Getting Started

## Design Concept

Fun I/O employs a few simple design principles:

+ The API is defined by abstract classes and interfaces in the module `fun-io-api`.
+ The module `fun-io-scala-api` adds operators and implicit conversions for an enhanced development experience in Scala.
+ Each implementation module provides a single facade class which consists of one or more static factory methods.
+ Each static factory method returns an instance of a class or interface defined by the API without revealing the actual 
  implementation class.
+ Except for their expected side effect (e.g. reading or writing data), implementations are virtually stateless, and 
  hence reusable and trivially thread-safe.

With this design, the canonical way of using Fun I/O is to import some static factory methods from one or more facade 
classes.
It's perfectly fine to import all static factory methods using a wildcard like `*`.
However, for the purpose of showing the originating facade class, the examples on this page do not use wildcard imports 
for static factory methods.

## Configuring The Classpath

Once you've decided on the set of features required by your application you need to add the respective modules to the 
class path - see [Module Structure And Features].
A Java application typically has a dependency on `fun-io-bios`.
A Scala application typically has the same dependencies as a Java application plus an additional dependency on
`fun-io-scala-api` to improve the development experience in Scala. 

The examples on this page depend on `fun-io-bios`, `fun-io-jackson`, `fun-io-scala-api` and, transitively, `fun-io-api`.
If your application is a Java project build with Maven or a Scala project build with SBT, then you need to add the 
following to its project configuration:

::: code

```pom
<!-- pom.xml -->
<project [...]>
    [...]
    <dependencies>
        [...]
        <dependency>
            <groupId>global.namespace.fun-io</groupId>
            <artifactId>fun-io-bios</artifactId>
            <version>2.2.0</version>
        </dependency>
        <dependency>
            <groupId>global.namespace.fun-io</groupId>
            <artifactId>fun-io-jackson</artifactId>
            <version>2.2.0</version>
        </dependency>
    </dependencies>
</project>
```

```sbt
// build.sbt
...
libraryDependencies ++= Seq(
  "global.namespace.fun-io" % "fun-io-bios" % "2.0.0",
  "global.namespace.fun-io" % "fun-io-jackson" % "2.0.0",
  "global.namespace.fun-io" %% "fun-io-scala-api" % "2.0.0"
)
```

:::

## Encoding Objects

The following code encodes the string `"Hello world!"` to JSON and writes it to standard output - including the quotes:

:::code

```java
import global.namespace.fun.io.api.Codec;       // from module `fun-io-api`
import global.namespace.fun.io.api.Encoder;
import global.namespace.fun.io.api.Sink;
import global.namespace.fun.io.bios.BIOS;       // from module `fun-io-bios`
import global.namespace.fun.io.jackson.Jackson; // from module `fun-io-jackson`

class Scratch {
    public static void main(String[] args) throws Exception {
        Codec codec = Jackson.json();
        Sink sink = BIOS.stdout();
        Encoder encoder = codec.encoder(sink);
        encoder.encode("Hello world!");
    }
}
```

```scala
import global.namespace.fun.io.api.{Codec, Encoder, Sink} // from module `fun-io-api`
import global.namespace.fun.io.bios.BIOS                  // from module `fun-io-bios`
import global.namespace.fun.io.jackson.Jackson            // from module `fun-io-jackson`

val codec: Codec = Jackson.json
val sink: Sink = BIOS.stdout
val encoder: Encoder = codec.encoder(sink)
encoder.encode("Hello world!")
```

:::

In the preceding code, first an instance of the `Codec` interface is obtained from the `Jackson` facade class for 
encoding/decoding objects to/from JSON.
Next, an instance of the interface `Sink` is obtained from the `BIOS` facade class for writing to standard output.
The codec and the sink are then combined into an instance of the `Encoder` interface.
Finally, the encoder is used to encode the string `"Hello world!"` and write it to standard output.

The preceding code can be simplified to the following one-liner:

:::code

```java
import static global.namespace.fun.io.bios.BIOS.stdout;
import static global.namespace.fun.io.jackson.Jackson.json;

class Scratch {
    public static void main(String[] args) throws Exception {
        json().encoder(stdout()).encode("Hello world!");
    }
}
```

```scala
import global.namespace.fun.io.bios.BIOS.stdout     // from module `fun-io-bios`
import global.namespace.fun.io.jackson.Jackson.json // from module `fun-io-jackson`

json encoder stdout encode "Hello world!"
```

:::

Note that importing static members of facade classes like `BIOS` and `Jackson` is the canonical way to use their API.
As you can see, this leads to concise, easily comprehensible code. 

There are many other `Codecs` and `Sinks` available - see [Module Structure And Features].
Also, the `Sink` interface is extended by the `Store` interface, of which you will find plenty implementations provided
by facade classes like `BIOS` and others, as you will see in the next example.

## Applying Filters

### Encoding Objects To Files

The following example is only slightly more complex than the previous one.
Again, the string `"Hello world!"` is encoded to JSON, but this time it also gets compressed using GZIP and the result 
saved to the file `hello-world.gz`:

::: code

```java
import global.namespace.fun.io.api.Codec;
import global.namespace.fun.io.api.Filter;
import global.namespace.fun.io.api.Store;
import global.namespace.fun.io.bios.BIOS;
import global.namespace.fun.io.jackson.Jackson;

class Scratch {
    public static void main(String[] args) throws Exception {
        Filter gzip = BIOS.gzip();
        Filter buffer = BIOS.buffer();
        Codec codec = Jackson.json().map(gzip).map(buffer);
        Store store = BIOS.file("hello-world.gz");
        codec.encoder(store).encode("Hello world!");
    }
}
```

```scala
import global.namespace.fun.io.api.{Codec, Filter, Store}
import global.namespace.fun.io.bios.BIOS
import global.namespace.fun.io.jackson.Jackson
import global.namespace.fun.io.scala.api._                // from module `fun-io-scala-api`

val gzip: Filter = BIOS.gzip
val buffer: Filter = BIOS.buffer
val codec: Codec = Jackson.json << gzip << buffer
val store: Store = BIOS.file("hello-world.gz")
codec.encoder(store).encode("Hello world!")
```

:::

In the preceding code, first two instances of the `Filter` interface are obtained from the `BIOS` facade class which
represent the GZIP compression and an heap buffer algorithm, respectively.
Next, an instance of the `Codec` interface is obtained from the `Jackson` facade class for encoding/decoding objects 
to/from JSON, just like in the previous example.
This time however, the `map` method is called to transform the codec into a new codec which applies the two filters.
In Scala, this expression can be more concisely written as `json << gzip << buffer`, where the `<<` operator is 
associative. 
Also, note that the buffer filter is applied last in order to minimize the number of subsequent write operations to the
file.
Next, an instance of the `Store` interface is obtained from the `BIOS` facade class which represents the file
`hello-world.gz`.
Finally, the codec is connected to the store and used to encode, compress and write the string `"Hello world!"` to the
file `hello-world.gz`.  

Again, using static imports from the facade classes, the preceding code can be simplified to the following:

::: code

```java
import static global.namespace.fun.io.bios.BIOS.*;
import static global.namespace.fun.io.jackson.Jackson.json;

class Scratch {
    public static void main(String[] args) throws Exception {
        json()
                .map(gzip())
                .map(buffer())
                .encoder(file("hello-world.gz"))
                .encode("Hello world!");
    }
}
```

```scala
import global.namespace.fun.io.bios.BIOS._
import global.namespace.fun.io.jackson.Jackson.json
import global.namespace.fun.io.scala.api._

json << gzip << buffer encoder file("hello-world.gz") encode "Hello world!"
```

:::

### Decoding Objects From Files

To read the object back from the file, you can use the following code:

::: code

```java
import static global.namespace.fun.io.bios.BIOS.*;
import static global.namespace.fun.io.jackson.Jackson.json;

class Scratch {
    public static void main(String[] args) throws Exception {
        String clone = json()
                .map(gzip())
                .map(buffer())
                .decoder(file("hello-world.gz"))
                .decode(String.class);
        assert clone.equals("Hello world!");
    }
}
```

```scala
import global.namespace.fun.io.bios.BIOS._
import global.namespace.fun.io.jackson.Jackson.json
import global.namespace.fun.io.scala.api._

val clone = json << gzip << buffer decoder file("hello-world.gz") decode classOf[String]
assert(clone == "Hello world!")
```

:::

As you can see, it's analogous to the writing algorithm:
You just need to replace the calls to the methods `encoder` and `encode` with `decoder` and `decode`, respectively.
The composition of the JSON codec with the GZIP and buffer filters remains the same. 

### Using Connected Codecs

Sometimes, an application needs to read and write some structured data to the same store again and again.  
In this case, rather than repeatedly creating an instance of the `Encoder` interface for writing and its `Decoder`
counterpart for reading, it's simpler to make a permanent connection of a `Codec` to a `Store` by using a
`ConnectedCodec`:

::: code

```java
import global.namespace.fun.io.api.ConnectedCodec;

import static global.namespace.fun.io.bios.BIOS.*;
import static global.namespace.fun.io.jackson.Jackson.json;

class Scratch {
    public static void main(String[] args) throws Exception {
        ConnectedCodec codec = json()
                .map(gzip())
                .map(buffer())
                .connect(file("hello-world.gz"));
        codec.encode("Hello world!");
        String clone = codec.decode(String.class);
        assert clone.equals("Hello world!");
    }
}
```

```scala
import global.namespace.fun.io.api.ConnectedCodec
import global.namespace.fun.io.bios.BIOS._
import global.namespace.fun.io.jackson.Jackson.json
import global.namespace.fun.io.scala.api._

val codec = json << gzip << buffer connect file("hello-world.gz")
codec encode "Hello world!"
val clone = codec decode classOf[String]
assert(clone == "Hello world!")
```

:::

In the preceding code, the `connect` method connects the (transformed) `Codec` to a (file) `Store` into a
`ConnectedCodec`.
Subsequently, the connected codec is used to write and read back the string `"Hello world!"`.

## Cloning Objects

A `ConnectedCodec` is an `Encoder` and a `Decoder` in one, so it can be used to create a deep clone of the original 
object, as seen in the previous example.
To make this more useful, the `gzip()` and `buffer()` filters and the `file(...)` store can be removed and the encoded 
data get buffered on the heap instead:

::: code

```java
import global.namespace.fun.io.api.ConnectedCodec;

import static global.namespace.fun.io.bios.BIOS.memory;
import static global.namespace.fun.io.jackson.Jackson.json;

class Scratch {
    public static void main(String[] args) throws Exception {
        ConnectedCodec codec = json().connect(memory());
        codec.encode("Hello world!");
        String clone = codec.decode(String.class);
        assert clone.equals("Hello world!");
    }
}
```

```scala
import global.namespace.fun.io.api.ConnectedCodec
import global.namespace.fun.io.bios.BIOS.memory
import global.namespace.fun.io.jackson.Jackson.json
import global.namespace.fun.io.scala.api._

val codec: ConnectedCodec = json << memory
codec encode "Hello world!"
val clone: String = codec decode classOf[String]
assert(clone == "Hello world!")
```

:::

Note that the `memory` method returns just another instance of the `Store` interface which is backed by an array of
bytes.
Encoding and decoding can be done in a single step:

::: code

```java
import global.namespace.fun.io.api.ConnectedCodec;

import static global.namespace.fun.io.bios.BIOS.memory;
import static global.namespace.fun.io.jackson.Jackson.json;

class Scratch {
    public static void main(String[] args) throws Exception {
        String clone = json().connect(memory()).clone("Hello world!");
        assert clone.equals("Hello world!");
    }
}
```

```scala
import global.namespace.fun.io.api.ConnectedCodec
import global.namespace.fun.io.bios.BIOS.memory
import global.namespace.fun.io.jackson.Jackson.json
import global.namespace.fun.io.scala.api._

val clone: String = json << memory clone "Hello world!"
assert(clone == "Hello world!")
```

:::

Because deep-cloning is a standard use case, there is a ready-made method in the BIOS facade for it:

::: code

```java
import static global.namespace.fun.io.bios.BIOS.clone;

class Scratch {
    public static void main(String[] args) throws Exception {
        String c = clone("Hello world!");
        assert c.equals("Hello world!");
    }
}
```

```scala
import global.namespace.fun.io.bios.BIOS.clone

val c: String = clone("Hello world!")
assert(c == "Hello world!")
```

:::

In contrast to the previous examples, this method uses `BIOS.serialization()` instead of `Jackson.json()` as the 
`Codec`, so the object to clone must implement `java.io.Serializable`.

## Copying Data

The BIOS facade class provides some utility methods for standard use cases based on the abstractions provided by the 
API.
One of these standard use cases is implemented by `BIOS.copy` in all its overloaded variants: 
Copying all data from a given source of some form to a given sink of some form.
Other than the naive _while-read-do-write_ loop, these copy methods employ a background thread and a ring buffer for
reading the data and piping it to the current thread for writing the data.
The result is a significant performance boost due to much better utilization of I/O channels:

::: code

```java
import static global.namespace.fun.io.bios.BIOS.buffer;
import static global.namespace.fun.io.bios.BIOS.copy;
import static global.namespace.fun.io.bios.BIOS.file;
import static global.namespace.fun.io.bios.BIOS.gzip;

class Scratch {
    public static void main(String[] args) throws Exception {
        copy(file("file.gz").map(buffer()).map(gzip()), file("file"));
    }
}
```

```scala
import global.namespace.fun.io.bios.BIOS.{buffer, copy, file, gzip}
import global.namespace.fun.io.scala.api._

copy(file("file.gz") >> buffer >> gzip, file("file"))
```

:::

The preceding code decompresses the data from the file `file.gz` and writes the decompressed data to the file `file`.

[Module Structure And Features]: ./module-structure-and-features.md
