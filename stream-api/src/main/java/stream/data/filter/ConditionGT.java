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

public class ConditionGT
    extends BinaryOperator
{
    /** The unique class ID */
    private static final long serialVersionUID = -5807565784925165795L;

    public ConditionGT() {
        super( "@gt", ">" );
    }
    

    /**
     * @see org.jwall.web.audit.rules.Condition#matches(java.lang.String, java.lang.String)
     */
    public boolean eval( Serializable input, String pattern ){
    	if( isNumeric( pattern ) ){
    		try {
    			Double v = new Double( pattern );
    			Double w = new Double( input + "" );
    			int rc = v.compareTo( w );
    			return rc < 0;
    		} catch (Exception e) {
    		}
    	}
    	
    	return ( "" + input ).compareTo( pattern ) >= 0;
    }
}