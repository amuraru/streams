package stream.io;

import java.io.InputStream;
import java.net.URL;

import stream.data.Data;
import stream.util.Parameter;


/**
 * <p>
 * This class implements a stream of log messages, read from some
 * file or URL. Reading from these sources is done line-by-line and
 * each line is returned as a Data item.
 * </p>
 * <p>
 * The lines are stored as key <code>MESSAGE</code> in the Data items.
 * </p>
 * <p>
 * The LogDataStream also provides generic parsing capabilities by
 * specifying a simple format string consisting of a sequence of
 * <code>%{KEY}</code> specifications. These will be used to store
 * the appropriately parsed items as the specified keys. 
 * </p>
 * <p>
 * The following example shows a LogDataStream that produces Data
 * items with a <code>LOG_MESSAGE</code>, <code>IP_ADDRESS</code> and
 * <code>SERVICE</code>:
 * </p>
 * <pre>
 *     &lt;Stream class="stream.io.LogDataStream"
 *                url="file:///...."
 *                format="%{IP_ADDRESS} %{SERVICE}: %{LOG_MESSAGE}"&gt;
 * </pre>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
public class LogDataStream
    extends AbstractDataStream
{
    public final static String MESSAGE = "MESSAGE";

    LogStreamParser parser = null;

    public LogDataStream( URL url ) throws Exception {
        this( url.openStream() );
    }
    
    public LogDataStream(InputStream in) throws Exception {
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
        
        item.put( MESSAGE, line );
        
        if( parser != null ){
        	parser.process( item );
        }
        
        return item;
    }
    
    
    public String getFormat(){
    	
    	if( parser != null ){
    		return parser.getFormat();
    	}
    	
    	return null;
    }
    
    
    @Parameter( name="format", required=false )
    public void setFormat( String fmt ){
    	this.parser = new LogStreamParser( fmt );
    }
    
    
	/**
	 * @see stream.io.DataStream#close()
	 */
	@Override
	public void close() {
		try {
			reader.close();
		} catch (Exception e) {
			log.error( "Failed to properly close reader: {}", e.getMessage() );
			if( log.isDebugEnabled() )
				e.printStackTrace();
		}
	}
}