package stream.io;

import java.io.InputStream;
import java.net.URL;

import stream.data.Data;

public class LogFileDataStream
    extends AbstractDataStream
{
    public final static String MESSAGE = "MESSAGE";
    

    public LogFileDataStream( URL url ) throws Exception {
        this( url.openStream() );
    }
    
    public LogFileDataStream(InputStream in) throws Exception {
        super(in);
    }

    @Override
    public void readHeader() throws Exception {
    }

    @Override
    public Data readItem(Data item) throws Exception
    {
        String line = reader.readLine();
        if( line == null )
            return null;
        
        item.put( "MESSAGE", line );
        return item;
    }
}