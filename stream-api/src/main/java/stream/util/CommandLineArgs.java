/**
 * 
 */
package stream.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author chris
 *
 */
public class CommandLineArgs {

	Map<String,String> options = new LinkedHashMap<String,String>();
	List<String> args = new ArrayList<String>();
	
	
	public CommandLineArgs( String[] args ){
		int i = 0;
		while( i < args.length ){
			
			if( args[i].startsWith( "-" ) && i+1 < args.length && ! args[i+1].startsWith( "--" ) ){
				String key = getOptionName( args[i] );
				String value = args[i+1];
				options.put( key, value );
				i++;
			} else {
				this.args.add( args[i] );
			}
			
			i++;
		}
	}
	
	public List<String> getArguments(){
		return args;
	}
	
	
	public Map<String,String> getOptions(){
		return options;
	}
	
	public String getOption( String key ){
		return getOption( key, null );
	}
	
	public String getOption( String key, String defaultValue ){
		if( options.get( key ) == null )
			return defaultValue;
		return options.get( key );
	}
	
	
	protected String getOptionName( String opt ){
		String str = opt;
		while( str.startsWith( "-" ) )
			str = str.substring(1);
		
		return str.replaceAll( "-", "\\." );
	}
	
	public void setSystemProperties( String prefix ){
		String pre = prefix;
		if( ! prefix.endsWith( "." ) )
			pre = prefix + ".";
		
		for( String opt : options.keySet() ){
			System.setProperty( pre + opt, options.get( opt ) );
		}
	}
	
	public void dumpArgs(){
		for( String opt : getOptions().keySet() ){
			System.out.println( "  option "+ opt + " = " + getOption( opt ) );
		}
		
		for( String arg : getArguments() ){
			System.out.println( "  arg: " + arg );
		}
	}
	
	public static Properties expandSystemProperties( Properties p ){
		Properties result = new Properties();
		
		for( Object key : p.keySet() ){
			String k = key.toString();
			String value = p.getProperty( k );
			if( value.indexOf( "${" ) >= 0 ){
				for( Object o : System.getProperties().keySet() ){
					String os = "${" + o.toString() + "}";
					value = value.replace( os, System.getProperty( o.toString() ) );
				}
				
				result.setProperty( k, value );
				
			} else {
				result.setProperty( k, p.getProperty(k) );
			}
		}

		return result;
	}
	
	public static void populateSystemProperties( Properties p ){
		for( Object k : p.keySet() ){
			if( System.getProperty( k.toString() ) == null ){
				System.setProperty( k.toString(), p.getProperty( k.toString() ) );
			}
		}
	}
	
	
	public static void main( String[] args ){
		String[] params = new String[]{
				"--limit", "120",
				"--block-size", "12",
				"--max-parts", "4",
				"/tmp/test.file",
				"/tmp/out-dir"
		};
		
		CommandLineArgs cla = new CommandLineArgs( params );
		cla.dumpArgs();
	}
}