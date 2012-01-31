Data Processor
==============

Each class implementing the `DataProcessor` interface represents a simple
function that acts upon a single data item of the stream. Data processors
can be plugged into the processing chain to perform a series of operations
on the data.

As described in the [conventions](convention.html) page, the processors can
access various parts of the data item by using the items keys.

The following code snippet implements a simple data processor that checks
for the key `x` and adds the key `y` by multiplying `x` by 2:

     package my.package;

     import stream.data.Data;
     import stream.data.DataProcessor;

     public class Multiplier
         implements DataProcessor 
     {
         /**
          * Extract the attribute x and add y as y = 2 * x
          * @param item
          */
         public Data process( Data item ){
             
             if( item.get( "x" ) != null ){
                //
                // the values in all items are "serializable" by default
                //
                Serializable value = item.get( "x" );

                //
                // parse a double-value from the string representation
                // of the serializable value:
                //
                Double x = new Double( value.toString() );
                
                
                // add the value 2*x as new attribute of the item:
                //
                data.put( "y",  new Double(  2 * x ) );

             }
             
             return item;
         }
     }

This simple multiplier relies on parsing the double value from its string
representation. If the double is available as Double object already in the
item, then we could also use a cast for this:

     // directly cast the serializable value to a Double object:
     //
     Double x = (Double) item.get( "x" );


This simple processor is already ready to be used within a simple stream
processing chain. To use it, we can directly use the XML syntax of the *streams*
framework to include it in to the process:

       <experiment>
         ...
         
         <processors>
            <my.package.Multiplier />
         </processors>
       </experiment>

The multiplier will be created at the startup of the experiment and will be
called (i.e. the `process(..)` method) for each event of the data stream.


Adding Parameters
-----------------

In most cases, we want to add a simple method for parameterizing our DataProcessor
implementation. This can easily be done by following the Convention&Configuration
paradigm:

By convention, all `setX(...)` and `getY()` methods are automatically regarded as
parameters for the data processors and directly available as XML attributes.

In the example from above, we want to add two parameters: `key` and `factor` to
our Multiplier implementation. The `key` parameter will be used to select the
attribute used instead of `x` and the `factor` will be a value used for multipying
(instead of the constant `2` as above).

To add these two parameters to our Multiplier, we only need to provide corresponding
getters and setters:

        String key = "x";    // by default we still use 'x'
        Double factor = 2;   // by default we multiply with 2

        // getter/setter for parameter "key"
        //
        public void setKey( String key ){
            this.key = key;
        }

        public String getKey()(
            return key;
        }

        // getter/setter for parameter "factor"
        // 
        public void setFactor( Double fact ){
            this.factor = fact;
        }

        public Double getFactor(){
            return factor;
        }
        
After compiling this class, we can directly use the new parameters `key` and `factor`
as XML attributes. For example, to multiply all attributes `z` by `3.1415`, we can
use the following XML setup:

       <experiment>
         ...
         <processors>
            <my.package.Multiplier key="z" factor="3.1415" />
         </processors>
       </experiment>

Upon startup, the getters and setters of the Multiplier class will be checked and
if the argument is a Double (or Integer, Float,...) it will be automatically converted
to that type.

In the example of our extended Multiplier, the `factor` parameter will be created to
a Double object of value *3.1415* and used as argument in the `setFactor(..)` method.



