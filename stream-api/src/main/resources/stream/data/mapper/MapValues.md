MapValue
========

This processor provides a way to map values of a specific feature
onto other values. For example, mapping all labels `0.0` to the
value `-1.0` can be done using

      <MapValues key="@label" from="0.0" to="-1.0" />
      
This is useful, e.g. when required to map labels or other categorical
data to different values.

A more complex setting can be used by specifying the map in a `key=value`
file, e.g. stored as `my-map.txt`. With such a file, the processor can
be specified as:

      <MapValues key="@label" map="my-map.txt" />
      
where the `my-map.txt` file may look like

      false=-1.0
      true=1.0
      
which will map the string values `true` and `false` according to the map. 