
The <code>streams</code> Framework
=======================

The `streams` framework is a Java implementation of a variety of online machine
learning algorithms. It provides several classifier implementations as well as
algorithms for online counting or book-keeping statistics (e.g. *top-k* statistics,
and *quantiles*).

In addition to the learning algorithms, it provides a simple plugin structure to
data preprocessing and evaluation of the classifiers using a *test-then-train*
strategy.


Overview
--------

The `streams` framework comprises several modules, of which the central one is the
*stream-api* module. The *stream-api* defines the basic interfaces and data structures
used in all other modules.

The other modules are

  * [stream-mining](stream-mining/index.html) -- a library providing various onling 
    learning algorithms

  * [stream-mapred](stream-mapred/index.html) -- a module providing a simple Map&Reduce 
    environment that can be used with the online learning algorithms of `stream-mining`

  * [stream-log](stream-log/index.html) -- a library for reading and processing log-data
    (web server logs, database query logs, etc.)

The modules are again maven projects, each having a separate project page and
documentation.


Source Code & Usage
-------------------

The source code of the framework is available at [github](http://github.com/cbockermann/streams).

Each of the modules can easily be integrated into your own code and used as library by
listing it as maven dependency. The libraries are currently available via the following
maven repository:

      <repository>
         <id>jwall</id>
         <name>jwall.org Maven Repository</name>
         <url>http://secure.jwall.org/maven/repository/all</url>
      </repository>


