The Stream-API
==============

The *stream-api* is meant to be a simple set of plain Java classes and
interfaces that provide a generic, abstract way of reading data from streams.
Several I/O classes for CSV, SVMlight, log-files or binary files are already
provided.

When reading from a stream item-by-item, each item is represented as a *Data*
object, which basically is just a Java Map with *String* keys and *Serializable* 
values. Following the *convention-over-configuration* paradigm, we define some
simple rules on how to treat the keys of a data item.

The next step then is to provide implementation of the *DataProcessor* interface
that can deal with one item at a time.


Stream-API Conventions
----------------------

The *convention-over-configuration* approach reveals some powerful mechanisms, that can
help building clean and flexible APIs. This was one of the driving forces of the `streams`
framework.

At the basis of the `streams` framework is the `stream.data.Data` interface, which simply
derives from a plain Java Map. The keys are `String` objects, the values may be any 
`Serializable`s. Per se, none of the keys does have a special meaning. It is by *convention*
that the pairs of a `Data` item become *alive* `:-)`


Features, Annotations and Labels
--------------------------------

A single `Data` instance is just a mapping of keys to values. Some keys may have a special
meaning, and it is by convention that we define how this works. A few simple conventions:

  1. Keys starting with "`@`" are considered *annotations*
  2. Keys starting with "`.`" are considered *hidden*
  3. Annotations and hidden keys are considered *special*
  4. All other keys are considered *normal variables*


As one area of application of `streams` is the online learning and classification tasks, these
tasks rely on a strict model of features *x_1,...,x_p* and labels *y*. Generally, the
features are *variables* and the labels are *target variables* in the statistical sense.

For the `Data` items, we generally assume a label to be an annotation. The simplest case might
just be to have a key `@label` contained in a data item. In the case of multiple labels, we
might have additional variables, such as in the following example table:

<table>
 <tr><th>f1</th><th>f2</th><th>f3</th><th>@label</th><th>@label:L1</th><th>@label:L2</th><tr>
 <tr><td>2 </td><td>true<td></td> 3</td><td> -1.0 </td><td> green </td><td>  red </td></tr>
 <tr><td>4 </td><td>false </td><td> 3 </td><td> 1.0 </td><td> red </td><td> blue </td></tr>
 <tr><td>...</td><td> </td><td> </td><td> </td><td> </td><td> </td></tr>
</table>

That being said, a feature can easily be turned into a label by prepending the `@label:` prefix.