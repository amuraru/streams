#!/bin/sh
#
# This is a sample script to run the SgdExperiment on a split
# if training and test data.
#

# This is the URL of the training dataset in svm-light format
#
TRAIN=http://kirmes.cs.uni-dortmund.de/data/mnist-800k.tr 

# This is the URL of the test dataset in svm-light format
#
TEST=http://kirmes.cs.uni-dortmund.de/data/mnist-200k.tt

# This is the output directory where all data is stored, the logs
# are written, etc.
#
OUT=/tmp/

# This is the location of the completely assembled jar file
#
LIBJAR="target/stream-mapred-1.0.2.jar"


# a list of java options for setting the memory limit, the maximum
# number of threads, etc.
# The options below specify 32GB of memory and a maximum of 4 parallel threads
#
JAVA_OPTS=" -Xmx32768M  -DmaxThreads=4 "

RUN="java ${JAVA_OPTS} -cp ${LIBJAR} stream.SgdExperiment"

# test if the library is in place
#
if [ ! -f $LIBJAR ]; then
   echo "File ${LIBJAR} does not exist!"
   echo "Did you forget to run 'mvn assembly:assembly' ?"
   exit -1;
fi



#
# Experiments:
#
# Run SGD with several configurations:
#     T=25k, M=32
#     T=50k, M=16
#     T=100k, M=8
#     T=200k, M=4
#     T=400k, M=2  
#
$RUN 25000 32 $TRAIN $TEST $OUT >> $OUT/log
$RUN 50000 16 $TRAIN $TEST $OUT >> $OUT/log
$RUN 100000 8 $TRAIN $TEST $OUT >> $OUT/log
$RUN 200000 4 $TRAIN $TEST $OUT >> $OUT/log
$RUN 400000 2 $TRAIN $TEST $OUT >> $OUT/log