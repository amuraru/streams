Running Stream Experiments
==========================

The *Stream-API* provides an easy to use environment for defining processes
for continuous data-stream processing. This can be evaluation or training of
online learning algorithms, data preprocessing or simply data conversion into
different formats.

The following snippet shows an experiment that reads data from a *gzip*ped
CSV file, adds an ID column and removes all but two named columns before
writing the result into another CSV file:

     <experiment>
         <stream id="test-stream"
                 url="http://kirmes.cs.uni-dortmund.de/data/test.csv.gz" />

         <processors input="test-stream">

             <SelectAttributes keys="name,role" />

             <AddId key="@id" />


             <DataStreamWriter file="output.csv" separator=";" />

         </processors>
     </experiment>

The elements within the `<processors>` tag all directly refer to Java classes
provided within the *Stream-API*. All of these classes can be found in the
package `stream.data`, or sub packages, e.g. `stream.data.mapper.AddId`.


