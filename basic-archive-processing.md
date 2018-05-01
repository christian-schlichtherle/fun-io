---
title: Basic Archive Processing
---

## Packing An Archive File

The following code packs the TAR.GZ file `archive.tar.gz` from the directory `directory`.
It uses the `CommonsCompress` facade for accessing the TAR file format and transforming it to TAR.GZ using Apache 
Commons Compress.
It also uses the `BIOS` facade for accessing directories like archive files and copying them:

{% highlight java %}
import static global.namespace.fun.io.commons.compress.CommonsCompress.gzip; // from `fun-io-commons-compress`
import static global.namespace.fun.io.commons.compress.CommonsCompress.tar;
import static global.namespace.fun.io.bios.BIOS.*;                           // from `fun-io-bios`

class Scratch {
    public static void main(String[] args) throws Exception {
        copy(directory("directory"), tar(file("archive.tar.gz").map(gzip())));
    }
}
{% endhighlight %}

## Unpacking An Archive File

The following code is the inverse of the previous operation:

{% highlight java %}
import static global.namespace.fun.io.commons.compress.CommonsCompress.gzip; // from `fun-io-commons-compress`
import static global.namespace.fun.io.commons.compress.CommonsCompress.tar;
import static global.namespace.fun.io.bios.BIOS.*;                           // from `fun-io-bios`

class Scratch {
    public static void main(String[] args) throws Exception {
        copy(tar(file("archive.tar.gz").map(gzip())), directory("directory"));
    }
}
{% endhighlight %}

## Transforming An Archive File

The following code transforms the TAR.GZ file `archive.tar.gz` to the ZIP file `archive.zip`:

{% highlight java %}
import static global.namespace.fun.io.commons.compress.CommonsCompress.gzip; // from `fun-io-commons-compress`
import static global.namespace.fun.io.commons.compress.CommonsCompress.tar;
import static global.namespace.fun.io.commons.compress.CommonsCompress.zip;
import static global.namespace.fun.io.bios.BIOS.*;                           // from `fun-io-bios`

class Scratch {
    public static void main(String[] args) throws Exception {
        copy(tar(file("archive.tar.gz").map(gzip())), zip("archive.zip"));
    }
}
{% endhighlight %}

<div class="btn-group d-flex justify-content-center" role="group" aria-label="Pagination">
  <button type="button" class="btn btn-light"><a href="{{ site.baseurl }}{% link basic-usage.md %}">&laquo; Basic Usage</a></button>
  <button type="button" class="btn btn-light"><a href="{{ site.baseurl }}{% link advanced-archive-processing.md %}">Advanced Archive Processing &raquo;</a></button>
</div>
