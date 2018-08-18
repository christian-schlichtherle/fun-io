# Basic Archive Processing

## Packing An Archive File

The following code packs the TAR.GZ file `archive.tar.gz` from the directory `directory`.
It uses the `CommonsCompress` facade for accessing the TAR file format and transforming it to TAR.GZ using Apache 
Commons Compress.
It also uses the `BIOS` facade for accessing directories like archive files and copying them:

```java
import static global.namespace.fun.io.commons.compress.CommonsCompress.gzip; // from `fun-io-commons-compress`
import static global.namespace.fun.io.commons.compress.CommonsCompress.tar;
import static global.namespace.fun.io.bios.BIOS.copy;                        // from `fun-io-bios`
import static global.namespace.fun.io.bios.BIOS.directory;
import static global.namespace.fun.io.bios.BIOS.file;

class Scratch {
    public static void main(String[] args) throws Exception {
        copy(directory("directory"), tar(file("archive.tar.gz").map(gzip())));
    }
}
```

## Unpacking An Archive File

The following code is the inverse of the previous operation:

```java
import static global.namespace.fun.io.commons.compress.CommonsCompress.gzip;
import static global.namespace.fun.io.commons.compress.CommonsCompress.tar;
import static global.namespace.fun.io.bios.BIOS.copy;
import static global.namespace.fun.io.bios.BIOS.directory;
import static global.namespace.fun.io.bios.BIOS.file;

class Scratch {
    public static void main(String[] args) throws Exception {
        copy(tar(file("archive.tar.gz").map(gzip())), directory("directory"));
    }
}
```

## Transforming An Archive File

The following code transforms the TAR.GZ file `archive.tar.gz` to the ZIP file `archive.zip`:

```java
import static global.namespace.fun.io.commons.compress.CommonsCompress.gzip;
import static global.namespace.fun.io.commons.compress.CommonsCompress.tar;
import static global.namespace.fun.io.commons.compress.CommonsCompress.zip;
import static global.namespace.fun.io.bios.BIOS.copy;
import static global.namespace.fun.io.bios.BIOS.file;

class Scratch {
    public static void main(String[] args) throws Exception {
        copy(tar(file("archive.tar.gz").map(gzip())), zip("archive.zip"));
    }
}
```
