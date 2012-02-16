CreateID
========

This processor simply adds an incremental identifier to each processed
data item. By default, this identifier is stored as feature `@id`, but
can be used with any other name as well.

The following example creates a processor adding IDs with name `@uid`:

     <CreateID key="@uid" />
     
IDs are numbered starting from 0, but can also start at arbitrary
integer values:

     <CreateID key="@uid" start="10" />