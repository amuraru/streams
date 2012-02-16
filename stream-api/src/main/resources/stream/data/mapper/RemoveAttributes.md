RemoveAttributes
================

This processors provides the possibility to remove a set of features
from each processed data item. The list can be specified by setting
the `keys` parameter to the list of features to be removed:

      <RemoveAttributes keys="attr1,attr2,attr3" />

The key strings will be splitted at each comma and will be trimmed,
i.e. the list above will have the same effect as the following:

      <RemoveAttributes keys="attr1, attr2 , attr3" />
