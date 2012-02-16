package stream.data.mapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import stream.data.Data;
import stream.data.DataProcessor;

public class MapValues
    implements DataProcessor
{

    String key = "@label";
    String map;
    
    Serializable defaultValue = new Double( -1.0d );
    Map<Serializable,Serializable> mapping = new HashMap<Serializable,Serializable>();
    
    String from = null;
    String to = null;

    public void addMapping( Serializable from, Serializable to ){
        mapping.put( from, to );
    }

    
    public void setDefault( Serializable defaultValue ){
        this.defaultValue = defaultValue;
    }

    public Serializable getDefault(){
        return this.defaultValue;
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
	 * @return the map
	 */
	public String getMap() {
		return map;
	}


	/**
	 * @param map the map to set
	 */
	public void setMap(String map) {
		try {
			File file = new File( map );
			Properties p = new Properties();
			p.load( new FileInputStream( file ) );

			for( Object key : p.keySet() ){
				mapping.put( key.toString(), p.getProperty( key.toString() ) );
			}
			
			this.map = map;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}


	/**
	 * @param from the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}


	/**
	 * @return the to
	 */
	public String getTo() {
		return to;
	}


	/**
	 * @param to the to to set
	 */
	public void setTo(String to) {
		this.to = to;
	}


	@Override
    public Data process(Data data)
    {
        Serializable val = data.get( key );
        if( val == null )
            return data;
        
        
        if( from != null && to != null ){
        	if( from.equals( val.toString() ) ){
        		data.put( key, to );
        	}
        }

        Serializable to = mapping.get( val );
        if( to == null ){
            data.put( key, to );
        } else {
            data.put( key, defaultValue );
        }
        
        return data;
    }
}