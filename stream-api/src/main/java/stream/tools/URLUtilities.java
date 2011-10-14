package stream.tools;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class URLUtilities
{

    
    public static String readResponse( InputStream in ) throws Exception {
        StringBuffer s = new StringBuffer();
        BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
        String line = reader.readLine();
        while( line != null ){
            s.append( line + "\n" );
            line = reader.readLine();
        }
        reader.close();
        return s.toString();
    }
}
