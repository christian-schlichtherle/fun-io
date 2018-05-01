---
title: Advanced Archive Processing
---

## Diffing Two JAR Files

The following code diffs the base JAR file `base.jar` to the update JAR file `update.jar` and generates the delta JAR 
file `delta.jar`.
In addition to the `CommonsCompress` facade for accessing the archive files, it uses the `Delta` facade for diffing 
them:

{% highlight java %}
import static global.namespace.fun.io.commons.compress.CommonsCompress.jar; // from `fun-io-commons-compress`
import static global.namespace.fun.io.delta.Delta.diff;                     // from `fun-io-delta`

diff().base(jar("base.jar")).update(jar("update.jar")).to(jar("delta.jar"));
{% endhighlight %}

If you wanted to use the module `fun-io-bios` instead of `fun-io-commons-compress`, then, apart from configuring the 
classpath, you would only have to edit the `import` statement as shown in the next example.

## Patching The Base JAR File

The following code patches the base JAR file `base.jar` with the delta JAR file `delta.jar` to an(other) update JAR 
file `update.jar`.
For the purpose of illustration, it uses the `BIOS` facade from the module `fun-io-bios` instead of the 
`CommonsCompress` facade from the module `fun-io-commons-compress` for accessing the JAR file format using the JRE.
For production, using the `CommonsCompress` facade is recommend for better accuracy and performance. 
It also uses the `Delta` facade again for patching the base archive file with the delta archive file:

{% highlight java %}
import static global.namespace.fun.io.bios.BIOS.jar;     // from `fun-io-bios`
import static global.namespace.fun.io.delta.Delta.patch; // from `fun-io-delta`

patch().base(jar("base.jar")).delta(jar("delta.jar")).to(jar("update.jar"));
{% endhighlight %}

## Diffing Two Directories

The following code diffs the base directory `base` to the update directory `update` and generates the delta ZIP file 
`delta.zip`:

{% highlight java %}
import static global.namespace.fun.io.bios.BIOS.directory;                  // from `fun-io-bios`
import static global.namespace.fun.io.commons.compress.CommonsCompress.zip; // from `fun-io-commons-compress`
import static global.namespace.fun.io.delta.Delta.diff;                     // from `fun-io-delta`

diff().base(directory("base")).update(directory("update")).to(zip("delta.zip"));
{% endhighlight %}

## Patching The Base Directory

The following code patches the base directory `base` with the delta ZIP file `delta.zip` to the update directory
`update`:

{% highlight java %}
import static global.namespace.fun.io.bios.BIOS.directory;                  // from `fun-io-bios`
import static global.namespace.fun.io.commons.compress.CommonsCompress.zip; // from `fun-io-commons-compress`
import static global.namespace.fun.io.delta.Delta.patch;                    // from `fun-io-delta`

patch().base(directory("base")).delta(zip("delta.zip")).to(directory("update"));
{% endhighlight %}

## Computing A Delta Model

Maybe you just want to examine the delta of two archive files or directories, but not generate a delta archive file or 
directory from that?
The following code diffs the base directory `base` to the update directory `update` and computes a delta model:

{% highlight java %}
import global.namespace.fun.io.delta.model.DeltaModel;     // from `fun-io-delta`

import static global.namespace.fun.io.bios.BIOS.directory; // from `fun-io-bios`
import static global.namespace.fun.io.delta.Delta.diff;    // from `fun-io-delta`

DeltaModel model = diff().base(directory("base")).update(directory("update")).toModel();
model.changedEntries().forEach(entry -> { /* do something with it */ });
{% endhighlight %}

The delta model has properties describing the changed, unchanged, added and removed entries.

<div class="btn-group d-flex justify-content-center" role="group" aria-label="Pagination">
  <button type="button" class="btn btn-light"><a href="{{ site.baseurl }}{% link basic-archive-processing.md %}">&laquo; Basic Archive Processing</a></button>
</div>
