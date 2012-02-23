JavaScript
==========

This processor can be used to execute simple JavaScript snippets
using the Java-6 ECMA scripting engine.

The processor binds the data item as `data` object to the script
context to allow for accessing the item. The following snippet
prints out the message "Test" and stores the string `test` with
key `@tag` in the data object:

      println( "Test" );
      data.put( "@tag", "Test" );


External Scripts
----------------

The processor can also be used to run JavaScript snippets from
external files, by simply specifying the `file` attribute:

    <JavaScript file="/path/to/script.js" />
