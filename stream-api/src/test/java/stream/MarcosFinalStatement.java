/**
 * 
 */
package stream;

/**
 * @author chris
 *
 */
public class MarcosFinalStatement {

	
	public static String finalCountdown(){
		
		try {
			System.out.println( "Trying to do something... " );
			System.out.flush();
			return "something done.";
			
		} catch (Exception e) {
		 	System.out.println( "ieee - Exception!" );
		 	System.out.flush();
		} finally {
			System.out.println( "Doing final stuff..." );
			System.out.flush();
		}
		return "last line";
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String ret = finalCountdown();
		System.out.println( "result: " + ret );
	}
}
