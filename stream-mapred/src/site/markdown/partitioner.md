DataSet Partitioner
===================

The data set partitioner is a simple tool that allows you to split
a large data set into multiple blocks of a specified size. This very
much behaves like the Unix `split` command, but allows for additional
shuffling of the data on-the-fly.

The partitioner will simply download a file from a URL and slice it 
into batches of equal sizes. Since the SVM-light format is one-data-point 
per line, this perfectly fits the need to split data sets in that format (as
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