

stream-mapred Module
====================

   There exists a new module called 'stream-mapred' in the streams
project. The purpose of this module is simply to provide a very thin
bed for map&reduce programs that read from standard input and write
to standard output.
   The provided classes and interfaces are all set up to read input
in SVM-light format and write out sparse vectors (map jobs) or read
in a list of sparse vectors and write out a single sparse vector.

   This will be enhanced later on.

   There are also some basic tools available, which are explained in
detail below. The module can be build by installing the complete
streams project and then building the standalone jar for the stream-mapred
module:

    # cd streams
    # mvn -DskipTests=true install
    # cd stream-mapred
    # mvn assembly:assembly
    # cp target/stream-mapred/stream-mapred-jar-with-dependencies.jar stream-mapred.jar

   These steps will leave you with a file 'stream-mapred.jar' which
with is self-contained and includes all required classes to run.


DataSet Partitioner
===================

  The simplest tool in the set of classes is the Partitioner. It
will simply download a file from a URL and slice it into batches of
equal sizes. Since the SVM-light format is one-data-point per line,
this perfectly fits the need to split data sets in that format (as
is suitable for CSV,..)

  To create a partitioned dataset from a URL, simply use the 
stream-mapred.jar created above and run:

  # java -cp stream-mapred.jar stream.Partitioner \
      --max-parts 4 --block-size 1000 --limit 10000 \
      --input-url http://kirmes.cs.uni-dortmund.de/data/mnist-100k.tr

  This will download up to 10000 examples ( '-l' is for 'limit' ) and
put it into files of 1000 examples each (the '-b' is for `block-size`).
The outcome will be files named as

     mnist-100k.tr.part0000
     mnist-100k.tr.part0001
     mnist-100k.tr.part0002
     ...

  The data files are written to the current working directory.

  To create shuffled partitions, you can specify the `-s SEED` option,
which will randomly distribute the data items over the resulting parts.
The argument to `-s` is a seed value (Long) or `-1` if a random seed
value should be used. The intention of specifying a seed value is to be
able to recreate the same partitioning of the data later on.

  The seed value can also be specified by using the system property
`global.random.seed`.


Map&Reduce Runner
=================

  In order to run a map&reduce job on the partitioned data, the
jar-file provides a class called ‘MapReduce'. This class can be
started from the jar file and requires several arguments:

  - the name of the Map class
  - the name of the Reduce task
  - a list of input files (parts)
  - an output file.

To start the provided "SgdMapper" and "SgdReducer" on the list of
mnist data parts run:

    # java -cp stream-mapred.jar MapReduce stream.hadoop.SgdMapper stream.hadoop.SgdReducer mnist-100k* OUTPUT

As the mappers will start loading the data in parallel, it makes
sense to increase the Java heap-space for that:

    # java -Xmx2048M -cp stream-mapred.jar MapReduce ...

In addition to that, the MapReduce by default uses 4 parallel
mapper (threads). This might be too much on some CPUs and effectively
slow down the process.
The number of mappers can be limitted with the "max.mappers" variable:

    # java -Dmax.mappers=2 -Xmx2048M -cp stream-mapred.jar MapReduce ...
