---
---

# Basic Archive Processing

## Packing An Archive File

The following code packs a TAR.GZ file from a directory.
It uses the `CommonsCompress` facade for accessing the TAR file format and transforming it to TAR.GZ using Apache 
Commons Compress.
It also uses the `BIOS` facade for accessing directories like archive files and copying them.   

```java
import java.io.File;

import static global.namespace.fun.io.commons.compress.CommonsCompress.gzip; // from `fun-io-commons-compress`
import static global.namespace.fun.io.commons.compress.CommonsCompress.tar;
import static global.namespace.fun.io.bios.BIOS.*;                           // from `fun-io-bios`

File dir = ...;
File tgz = ...;
copy(directory(dir), tar(file(tgz).map(gzip())));
``` 

## Unpacking An Archive File

The following code is the inverse of the previous operation and unpacks a TAR.GZ file to a directory.

```java
import java.io.File;

import static global.namespace.fun.io.commons.compress.CommonsCompress.gzip; // from `fun-io-commons-compress`
import static global.namespace.fun.io.commons.compress.CommonsCompress.tar;
import static global.namespace.fun.io.bios.BIOS.*;                           // from `fun-io-bios`

File tgz = ...;
File dir = ...;
copy(tar(file(tgz).map(gzip())), directory(dir));
``` 

## Transforming An Archive File

The following code transforms a TAR.GZ file to a ZIP file.

```java
import java.io.File;

import static global.namespace.fun.io.commons.compress.CommonsCompress.gzip; // from `fun-io-commons-compress`
import static global.namespace.fun.io.commons.compress.CommonsCompress.tar;
import static global.namespace.fun.io.commons.compress.CommonsCompress.zip;
import static global.namespace.fun.io.bios.BIOS.*;                           // from `fun-io-bios`

File tgz = ...;
File zip = ...;
copy(tar(file(tgz).map(gzip())), zip(zip));
``` 
