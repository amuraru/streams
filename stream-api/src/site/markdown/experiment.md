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
         <Stream id="test-stream" class="stream.io.CsvStream"
                 url="http://kirmes.cs.uni-dortmund.de/data/test.csv.gz" />

         <Process input="test-stream">

             <SelectAttributes keys="name,role" />

             <AddId key="@id" />


             <DataStreamWriter file="output.csv" separator=";" />

         </Process>
     </experiment>

The `<Process>` element represents a single thread that continuously reads
items from the specified input stream and executes all nested/inner elements.
The elements within the `<Process>` tag all directly refer to Java classes
provided within the *Stream-API*. All of these classes can be found in the
package `stream.data`, or sub packages, e.g. `stream.data.mapper.AddId`.

Data items within a stream are processed one-by-one, limitting the overall
memory consumption of the process, allowing for (pre-) processing large
data files.


A more complex example
----------------------

The first example shown above is rather simple and straight-forward to show
the basic concept of a process within the *StreamAPI*.

The *stream-logs* module provides several data-stream implementations and
parsers for reading all kinds of different log files. In addition to that
it provides processors for SQL query parsing of SQL logs and the like.

The following example process reads a plain MySQL query log, extracts the
queries and parses these into ASTs (parse trees) and writes the trees into
a CSV based file format:

     <experiment>
         <Stream id="sql-log" class="stream.io.SyslogDataStream"
                 url="file:///var/log/mysql.log" />

         <Process input="sql-log">

           <org.jwall.sql.audit.SQLStreamParser key="sql" />

           <DataStreamWriter file="sql-trees.csv" keys="sql,@tree:sql" />
           
         </Process>
     </experiment>

In this experiment, a MySQL log-file `mysql.log` is read. The SQL query of
each line of that file is stored in the `sql` attribute of the data items.

The `SQLStreamParser` requires an attribute from which it shall extract
SQL queries and parses them into a syntax tree. This tree is added to the
item as attribute `@tree:sql`, i.e. the `@tree:` prefix is added to the
attribute from which the query has been parsed. 
The objects in `@tree:sql` are trees, implementing the `stream.data.tree.TreeNode`
interface.

The datastream writer in the end writes out the `sql` and `@tree:sql`
attributes from the data items into a CSV based format. The value of the
`@tree:sql` attributes in the CSV file are by default the representation
obtained by the `toString()` method of the tree objects.
