package stream.io;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jwall.log.io.ParserGenerator;

import stream.data.Data;

public class SyslogDataStream
    extends AbstractDataStream
{
    public final static String DATA = "DATA";
    public final static String MESSAGE = "MESSAGE";
    public final static String DATE_TIME = "DATE_TIME";
    public final static String TIMESTAMP = "TIMESTAMP";
    
    String format = "%{MESSAGE}";
    SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy MMM dd HH:mm:ss" );
    org.jwall.log.io.Parser<Map<String,String>> parser;
    
    public SyslogDataStream( URL url ) throws Exception {
        this( url.openStream() );
    }
    
    public SyslogDataStream(InputStream in) throws Exception
    {
        super(in);
        ParserGenerator pg = new ParserGenerator( "%{MONTH} %{DAY} %{HOUR}:%{MINUTE}:%{SECOND} %{FACILITY|\\w+} %{MESSAGE}" );
        parser = pg.newParser();
    }
    
    public void setFormat( String formatString ){
        this.format = formatString;
        ParserGenerator pg = new ParserGenerator( formatString );
        parser = pg.newParser();
    }
    
    public String getFormat(){
        return this.format;
    }

    
    /**
     * @see stream.io.AbstractDataStream#readHeader()
     */
    @Override
    public void readHeader() throws Exception {
    }
    

    /**
     * @see stream.io.AbstractDataStream#readNext(stream.data.Data)
     */
    @Override
    public Data readNext(Data item) throws Exception
    {
        String line = this.reader.readLine();
        item.put( DATA, line );
        
        Map<String,String> features = parser.parse( line );

        Map<String,String> timeMap = getCurrentTimeMap();
        
        for( String key : features.keySet() ){
            item.putAll( features );
            if( timeMap.containsKey( key ) )
                timeMap.put( key, features.get(key) );
        }
        
        Long time = getTimestamp( timeMap );
        item.put( TIMESTAMP, time + "" );
        item.put( DATE_TIME, dateFormat.format( new Date(time) ) );
        return item;
    }
    
    
    protected Long getTimestamp( Map<String,String> timeMap ){
        StringBuffer ds = new StringBuffer();
        ds.append( timeMap.get( "YEAR" ) );
        ds.append( " " );
        ds.append( timeMap.get( "MONTH" ) );
        ds.append( " " );
        ds.append( timeMap.get( "DAY" ) );
        ds.append( " " );
        ds.append( timeMap.get( "HOUR" ) );
        ds.append( ":" );
        ds.append( timeMap.get( "MINUTE" ) );
        ds.append( ":" );
        ds.append( timeMap.get( "SECOND" ) );
        
        try {
            Date date = this.dateFormat.parse( ds.toString() );
            return date.getTime();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }
    
    public Map<String,String> getCurrentTimeMap(){
        Date today = new Date();
        SimpleDateFormat month = new SimpleDateFormat( "mmm" );
        Map<String,String> now = new HashMap<String,String>();
        Calendar cal = Calendar.getInstance();
     
        now.put( "YEAR", cal.get( Calendar.YEAR ) + "" );
        now.put( "MONTH", month.format( today ) );
        now.put( "DAY", cal.get( Calendar.DAY_OF_MONTH ) + "" );
        now.put( "HOUR", cal.get( Calendar.HOUR ) + "" );
        now.put( "MINUTE", cal.get( Calendar.MINUTE ) + "" );
        now.put( "SECOND", cal.get( Calendar.SECOND ) + "" );
        
        return now;
    }
}