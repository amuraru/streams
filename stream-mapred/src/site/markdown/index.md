Map Reduce Framework
====================

   There exists a new module called 'stream-mapred' in the streams
project. The purpose of this module is simply to provide a very thin
bed for map&reduce programs that read from standard input and write
to standard output.
   The provided classes and interfaces are all set up to read input
in SVM-light format and write out sparse vectors (map jobs) or read
in a list of sparse vectors and write out a single sparse vector.

To previously split your data into blocks, see the [partitioner](partitioner.html)
page for details.


Map&Reduce Runner
-----------------

  In order to run a map&reduce job on the partitioned data, the
jar-file provides a class called `MapReduce`. This class can be
started from the jar file and requires several arguments:

  - the name of the Map class
  - the name of the Reduce task
  - a list of input files (parts)
  - an output file.

All these settings are read from a properties file that is given
to the MapReduce class at startup. The following is an example of
such a properties file, used for running [SGD](/notes/sgd) on some
data blocks:


     # Definition of the Mapper
     #
     mapper.class=stream.optimization.SgdMapper
     mapper.args.lambda=7.8125e-7
     mapper.input=/data/input/blocks/
     mapper.threads=64
     #
     # The Reducer definition
     #
     reducer.class=stream.optimization.SgdReducer
     reducer.output=/data/results/url_model.out


To start the Map&Reduce framework with the above defined mapper and reducer,
simple run the jar file and provide the properties file:


    # java -jar stream-mapred.jar my-map-reduce.properties


### Using Standard Input

If you do not specify the `mapper.input` property, then input data is read from
standard input. This is especially handy if you want to test your mapper on a
single small example block only. If you remove the `mapper.input` line in the
properties file above, the following command then uses blocks from standard input:

    # cat /data/my.block | java -jar stream-mapred.jar my-map-reduce.properties.

