DNS Reverse Lookup
==================

This processor translates IP addresses back to their host
names. The lookup is performed using the local naming service.

Two keys need to be specified: the `key` parameter specifies
the attribute that is used as address (IPv4 or IPv6) to be
mapped to a hostname. The `target` parameter specifies the
name of the attribute into which the hostname should be stored.

The following example will lookup the attribute value for
`REMOTE_ADDR` and will write the result into the attribute
`HOSTNAME`:

      <DNSReverseLookup key="REMOTE_ADDR" target="HOSTNAME" />

The processor integrates a simple, limited cache to speed up
lookups.
The cache size is set with the `cacheSize` parameter. By
default the size of the cache is 10000 entries.
