package org.jwall.sql.audit;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;


/**
 * This class implements some preprocessing of SQL query string to allow for
 * the JSQL-based stream-parser to properly parse more kinky SQL expressions...
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class SQLFixes
    implements DataProcessor
{
    static Logger log = LoggerFactory.getLogger( SQLFixes.class );
    
    String key = null;
    Map<String,String> fixes = new LinkedHashMap<String,String>();

    
    public SQLFixes(){
        fixes.put( "SQL_CALC_FOUND_ROWS", "" );
        fixes.put( " AS CHAR)", ")");
        fixes.put( "NULL#", "NULL" );
        fixes.put( ")#", ")" );
        fixes.put( " left(", " substring(" );
        fixes.put( "information_schema.SCHEMATA#", "information_schema.SCHEMATA" );
        fixes.put( "AND SLEEP(5)", "AND myDummySlep > SLEEP(5)" );
        fixes.put( "can\\'t get no sleep", "cant get not sleep" );
    }
    
    
    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }


    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }


    /**
     * @see stream.data.DataProcessor#process(stream.data.Data)
     */
    @Override
    public Data process(Data data) {
        
        if( key != null && data.get( key ) != null ){
            String query = data.get( key ).toString();
            
            int fixesApplied = 0;
            for( String fix : fixes.keySet() ){
                if( query.indexOf( fix ) > 0 ){
                    query = query.replace( fix, fixes.get( fix ) );
                    fixesApplied++;
                }
            }
            log.debug( "Applied {} SQL query fixes", fixesApplied );
            data.put( key, query );
        }
        
        return data;
    }
}