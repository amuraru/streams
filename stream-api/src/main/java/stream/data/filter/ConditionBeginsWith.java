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
package stream.data.filter;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>
 * Instances of this class check all values for the given variable if they
 * start with a pre-defined substring.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class ConditionBeginsWith
	extends BinaryOperator
{
    /** The unique class ID */
    private static final long serialVersionUID = 6247657062073419253L;
    static Logger log = LoggerFactory.getLogger( ConditionBeginsWith.class );
    
    public ConditionBeginsWith() {
        super( "@beginsWith" );
    }

    
    public boolean eval( Serializable input, String value ){
    	return value != null && input != null && input.toString().startsWith( value );
    }
}