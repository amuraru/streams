/*
 *  Copyright (C) 2007-2010 Christian Bockermann <chris@jwall.org>
 *
 *  This file is part of the  web-audit  library.
 *
 *  web-audit library is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The  web-audit  library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package stream.io;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * This class implements a split-method which takes care of quoted strings, i.e. there will be no
 * split within a char sequence that is surrounded by quotes (single or double quotes). These sequences
 * are simply skipped.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 */
public class QuotedStringTokenizer {
    
    
    public static List<String> splitRespectQuotes( String input, char sep ){

        List<String> results = new ArrayList<String>();
        int last = 0;
        int i = 0;
        
        while( i <= input.length() - 1 ){
            char c = input.charAt( i );
            
            
            // we skip quoted substrings 
            //
            if( c == '"' || c == '\'' ){
                do {
                    i++;
                    //char d = input.charAt( i );
                } while( i < input.length() && (input.charAt( i ) != c || input.charAt( i - 1 ) == '\\' ) );
            }
            
            // if we hit a separating character, we found another token
            //
            if( input.indexOf( sep, i ) == i  || i+1 == input.length() ){
                if( i + 1 == input.length() )
                    results.add( input.substring( last, i + 1 ) );
                else
                    results.add( input.substring( last, i ) );
                last = i + 1;
            }
            
            i++;
        }
        
        return results;
    }
}
