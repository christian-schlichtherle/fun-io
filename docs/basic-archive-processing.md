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

## Working with S3 Buckets

Fun I/O treats S3 buckets like archive files.
This feature enables you to copy and transform data between an S3 bucket (with an optional key prefix) and any other 
supported archive file, directory or S3 bucket (with an optional key prefix).

Fun I/O provides two modules for accessing S3:

+ Fun I/O AWS SDK1 (alias `fun-io-aws-sdk1`) depends on the Amazon SDK for Java and provides the package 
  `global.namespace.fun.io.aws.sdk1`. 
+ Fun I/O AWS SDK2 (alias `fun-io-aws-sdk2`) depends on the Amazon SDK for Java 2 and provides the package 
  `global.namespace.fun.io.aws.sdk2`.  

Both packages provide a facade class named `AWS` with identical behavior.

The following code uses Fun I/O AWS SDK1 to "unpack" the TAR.GZ file `archive.tgz` to the S3 bucket `bucket` with the 
key prefix `archive/`:

```java
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import static global.namespace.fun.io.aws.sdk1.AWS.s3; // from `fun-io-aws-sdk1`
import static global.namespace.fun.io.bios.BIOS.copy;
import static global.namespace.fun.io.bios.BIOS.file;
import static global.namespace.fun.io.commons.compress.CommonsCompress.gzip;
import static global.namespace.fun.io.commons.compress.CommonsCompress.tar;

class Scratch {
    public static void main(String[] args) throws Exception {
        AmazonS3 client = AmazonS3ClientBuilder.defaultClient();
        copy(tar(file("archive.tgz").map(gzip())), s3(client, "bucket", "archive/"));
    }
}
```

:::tip
If provided, the key prefix must be either empty or a normalized path which ends with a `/`. 
Absolute paths or referencing parent directories is not allowed.
For example, the following key prefixes are invalid:

+ `path` doesn't end with a `/`.
+ `/path/` is an absolute path.
+ `./path/` is not in normalized form.
+ `../path/` references the parent directory.
:::