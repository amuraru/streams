RandomBinaryLabel
=================

This operator simply adds a random label (either -1.0 or +1.0) to
processed data items. The labels are distributed normally.

The `key` parameter allows for specifying the label attribute name,
which by default is considered to be `@label`:

     <RandomBinaryLabel key="@label" />
