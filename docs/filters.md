# Filters

The `Filter` interface transforms the data as it is written to an `OutputStream` or read from an `InputStream`.
Their implementation is often based on the `FilterOutputStream` and `FilterInputStream` classes, hence the interface 
name.

## Some Algebra

Filters are associative by nature, which means that `a.andThen(b).andThen(c) = a.andThen(b.andThen(c))`, where `a`, `b` 
and `c` are `Filter` instances and `andThen` is an operation to apply two filter instances in order.
The same axiom holds for the `compose` operation, which applies two filter instances in reverse order. 
There is also a trivial identity element, which simply does nothing.
Hence, the `Filter` interface forms a [monoid] under the operations `andThen` and `compose`. 
 
## How To Use A Filter

**TODO**

## How To Write A Filter

**TODO**

[Monoid]: https://en.wikipedia.org/wiki/Monoid
