package stream.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import stream.data.Data;
import stream.data.DataImpl;


/**
 * This class reads a CSV file and writes CSV file. It will pass through
 * the input file twice to determine the complete list of columns and then
 * starts to write out the fully, non-sparse items.
 * 
 * @author chris@jwall.org
 *
 */
public class CsvFinalizer
{

    /**
     * @param args
     */
    public static void main(String[] args)
        throws Exception
    {
        File file = new File( args[0] );
        String sep = ";";
        if( System.getProperty( "separator" ) != null )
            sep = System.getProperty( "separator" );
        
        CsvStream stream = new CsvStream( new FileInputStream( file ), sep );
        
        Set<String> keys = new LinkedHashSet<String>();
        
        Data item = stream.readNext();
        while( item != null ){
            
            for( String key : item.keySet() ){
                if( ! keys.contains( key ) ){
                    keys.add( key );
                }
            }
            item = stream.readNext();
        }
        
        DataStreamWriter writer = new DataStreamWriter( new FileOutputStream( new File( args[1] ) ), sep );
        
        stream = new CsvStream( new FileInputStream( file ), sep );
        item = stream.readNext();
        while( item != null ){

            Data out = new DataImpl();
            for( String key : keys ){
                Serializable val = item.get( key );
                if( val != null )
                    out.put( key, val );
                else
                    out.put( key, 0.0d );
            }
            
            writer.process( out );
            item = stream.readNext();
        }
        
        writer.close();
    }

}
