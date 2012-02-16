MapKeys
=======

This processor provides a way to map keys (feature names) to other
values. This is sometimes required to match special processors that
rely on specific feature names:

      <MapKeys from="labelAttribute" to="@label" />
      

A more complex setting can be used by specifying the map in a `key=value`
file, e.g. stored as `my-map.txt`. With such a file, the processor can
be specified as:

      <MapKeys map="my-map.txt" />
      
where the `my-map.txt` file may look like

      attr1=sepalLength
      attr2=sepalWidth
      ...
      
which will map the keys according to the map. 