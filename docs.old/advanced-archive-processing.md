---
---

# Advanced Archive Processing

## Diffing Two JAR Files

The following code diffs a base JAR file to an update JAR file and generates a delta JAR file.
In addition to the `CommonsCompress` facade for accessing the archive files, it uses the `Delta` facade for diffing 
them:

```java
import java.io.File;

import static global.namespace.fun.io.commons.compress.CommonsCompress.jar; // from `fun-io-commons-compress`
import static global.namespace.fun.io.delta.Delta.diff;                     // from `fun-io-delta`

File base = ...;
File update = ...;
File delta = ...;
diff().base(jar(base)).update(jar(update)).to(jar(delta));
```

If you wanted to use the module `fun-io-bios` instead of `fun-io-commons-compress`, then, apart from configuring the 
class path, you would only have to edit the `import` statement as shown in the next example.

## Patching The Base JAR File

The following code patches the base JAR file with the delta JAR file to an(other) update JAR file.
For the purpose of illustration, it uses the `BIOS` facade from the module `fun-io-bios` instead of the 
`CommonsCompress` facade from the module `fun-io-commons-compress` for accessing the JAR file format using the JRE.
For production, using the `CommonsCompress` facade is recommend for better accuracy and performance. 
It also uses the `Delta` facade again for patching the base archive file with the delta archive file:

```java
import java.io.File;

import static global.namespace.fun.io.bios.BIOS.jar;     // from `fun-io-bios`
import static global.namespace.fun.io.delta.Delta.patch; // from `fun-io-delta`

File base = ...;
File delta = ...;
File update = ...;
patch().base(jar(base)).delta(jar(delta)).to(jar(update));
```

## Diffing Two Directories

The following code diffs a base directory to an update directory and generates a delta ZIP file.

```java
import java.io.File;

import static global.namespace.fun.io.bios.BIOS.directory;                  // from `fun-io-bios`
import static global.namespace.fun.io.commons.compress.CommonsCompress.zip; // from `fun-io-commons-compress`
import static global.namespace.fun.io.delta.Delta.diff;                     // from `fun-io-delta`

File base = ...;
File update = ...;
File delta = ...;
diff().base(directory(base)).update(directory(update)).to(zip(delta));
```

## Patching The Base Directory

The following code patches the base directory with the delta ZIP file to an(other) update directory.

```java
import java.io.File;

import static global.namespace.fun.io.bios.BIOS.directory;                  // from `fun-io-bios`
import static global.namespace.fun.io.commons.compress.CommonsCompress.zip; // from `fun-io-commons-compress`
import static global.namespace.fun.io.delta.Delta.patch;                    // from `fun-io-delta`

File base = ...;
File delta = ...;
File update = ...;
patch().base(directory(base)).delta(zip(delta)).to(directory(update));
```

## Computing A Delta Model

Maybe you just want to examine the delta of two archive files or directories, but not generate a delta archive file or directory from that?
The following code diffs a base directory to an update directory and computes a delta model.

```java
import java.io.File;

import global.namespace.fun.io.delta.model.DeltaModel;     // from `fun-io-delta`

import static global.namespace.fun.io.bios.BIOS.directory; // from `fun-io-bios`
import static global.namespace.fun.io.delta.Delta.diff;    // from `fun-io-delta`

File base = ...;
File update = ...;
DeltaModel model = diff().base(directory(base)).update(directory(update)).toModel();
model.changedEntries().forEach(entry -> { /* do something with it */ });
```

The delta model has properties describing the changed, unchanged, added and removed entries.
