# Advanced Archive Processing

## Diffing Two JAR Files

The following code diffs the base JAR file `base.jar` to the update JAR file `update.jar` and generates the delta JAR 
file `delta.jar`.
In addition to the `CommonsCompress` facade for accessing the archive files, it uses the `Delta` facade for diffing 
them:

```java
import static global.namespace.fun.io.commons.compress.CommonsCompress.jar; // from module `fun-io-commons-compress`
import static global.namespace.fun.io.delta.Delta.diff;                     // from module `fun-io-delta`

class Scratch {
    public static void main(String[] args) throws Exception {
        diff().base(jar("base.jar")).update(jar("update.jar")).to(jar("delta.jar"));
    }
}
```

If you wanted to use the module `fun-io-bios` instead of `fun-io-commons-compress`, then, apart from configuring the 
classpath, you would only have to edit the `import` statement as shown in the next example.

## Patching The Base JAR File

The following code patches the base JAR file `base.jar` with the delta JAR file `delta.jar` to an(other) update JAR 
file `update.jar`.
For the purpose of illustration, it uses the `BIOS` facade from the module `fun-io-bios` instead of the 
`CommonsCompress` facade from the module `fun-io-commons-compress` for accessing the JAR file format using the JRE.
For production, using the `CommonsCompress` facade is recommend for better accuracy and performance. 
It also uses the `Delta` facade again for patching the base archive file with the delta archive file:

```java
import static global.namespace.fun.io.bios.BIOS.jar;     // from module `fun-io-bios`
import static global.namespace.fun.io.delta.Delta.patch;

class Scratch {
    public static void main(String[] args) throws Exception {
        patch().base(jar("base.jar")).delta(jar("delta.jar")).to(jar("update.jar"));
    }
}
```

## Diffing Two Directories

The following code diffs the base directory `base` to the update directory `update` and generates the delta ZIP file 
`delta.zip`:

```java
import static global.namespace.fun.io.bios.BIOS.directory;
import static global.namespace.fun.io.commons.compress.CommonsCompress.zip;
import static global.namespace.fun.io.delta.Delta.diff;

class Scratch {
    public static void main(String[] args) throws Exception {
        diff().base(directory("base")).update(directory("update")).to(zip("delta.zip"));
    }
}
```

## Patching The Base Directory

The following code patches the base directory `base` with the delta ZIP file `delta.zip` to the update directory
`update`:

```java
import static global.namespace.fun.io.bios.BIOS.directory;
import static global.namespace.fun.io.commons.compress.CommonsCompress.zip;
import static global.namespace.fun.io.delta.Delta.patch;

class Scratch {
    public static void main(String[] args) throws Exception {
        patch().base(directory("base")).delta(zip("delta.zip")).to(directory("update"));
    }
}
```

## Computing A Delta Model

Maybe you just want to examine the delta of two archive files or directories, but not generate a delta archive file or 
directory from that?
The following code diffs the base directory `base` to the update directory `update` and computes a delta model:

```java
import global.namespace.fun.io.delta.model.DeltaModel;

import static global.namespace.fun.io.bios.BIOS.directory;
import static global.namespace.fun.io.delta.Delta.diff;

class Scratch {
    public static void main(String[] args) throws Exception {
        DeltaModel model = diff().base(directory("base")).update(directory("update")).toModel();
        model.changedEntries().forEach(entry -> { /* do something with it */ });
    }
}
```

The delta model has properties describing the changed, unchanged, added and removed entries.
