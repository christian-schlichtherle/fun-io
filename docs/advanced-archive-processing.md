---
---

# Advanced Archive Processing

## Diffing Two JAR Files And Generating A Delta JAR File

The following code diffs two JAR files and generates a delta JAR file.
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

## Patching A JAR File With A Delta JAR File To Another JAR File

The following code patches a JAR file with a delta JAR file to another JAR file.
For the purpose of illustration, it uses the `BIOS` facade from the module `fun-io-bios` instead of the 
`CommonsCompress` facade from the module `fun-io-commons-compress` for accessing the JAR file format using the JRE.
For production, using the `CommonsCompress` facade is recommend for better accuracy and performance. 
It also uses the `Delta` facade again for patching the base archive file with the delta archive file:

```java
import java.io.File;

import static global.namespace.fun.io.bios.BIOS.jar;     // from `fun-io-bios`
import static global.namespace.fun.io.delta.Delta.patch; // from `fun-io-delta`

File base = ...;
File update = ...;
File delta = ...;
patch().base(jar(base)).delta(jar(delta)).to(jar(update));
```

## Diffing Two Directories And Generating A Delta ZIP File

The following code diffs two directories and generates a delta ZIP file.

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

## Patching A Directory With A Delta ZIP File To Another Directory

The following code patches a directory with a delta ZIP file to another directory.

```java
import java.io.File;

import static global.namespace.fun.io.bios.BIOS.directory;                  // from `fun-io-bios`
import static global.namespace.fun.io.commons.compress.CommonsCompress.zip; // from `fun-io-commons-compress`
import static global.namespace.fun.io.delta.Delta.patch;                    // from `fun-io-delta`

File base = ...;
File update = ...;
File delta = ...;
patch().base(directory(base)).delta(zip(delta)).to(directory(update));
```

## Diffing Two Directories And Computing A Delta Model

Maybe you just want to examine the delta of two directories, but not generate a delta archive file or directory from 
that?
The following code diffs two directories and computes a delta model.
Again, the `BIOS` and the `Delta` facades are used to do that:

```java
import java.io.File;

import global.namespace.fun.io.delta.model.DeltaModel;     // from `fun-io-delta`

import static global.namespace.fun.io.bios.BIOS.directory; // from `fun-io-bios`
import static global.namespace.fun.io.delta.Delta.diff;    // from `fun-io-delta`

File base = ...;
File update = ...;
DeltaModel model = diff().base(directory(base)).update(directory(update)).toModel();
```

The delta model has properties describing the changed, unchanged, added and removed entries.
