DNS Lookup
==========

This processor simply checks for the hostname of a given
IP address. The IP address can either be IPv4 or IPv6.
Lookup is carried out using the local naming services. The
processor integrates a simple, limited cache to speed up
lookups.

The following example will lookup the IP address of the
hostname found in attribute `REMOTE_HOST` and store the
resolved address in attribute `REMOTE_ADDR`:

     <DNSLookup key="REMOTE_HOST" target="REMOTE_ADDR" />

The cache size is set with the `cacheSize` parameter. By
default the size of the cache is 10000 entries.
