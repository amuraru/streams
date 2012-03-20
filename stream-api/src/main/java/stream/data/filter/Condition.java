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


/**
 * <p>
 * This is the basic interface which needs to be implemented by all
 * rule conditions.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 */
public interface Condition
    extends Serializable
{
    public final static String EQ = Operator.EQ.toString();
    /*
    public final static String LT = Operator.LT.toString();
    public final static String LE = Operator.LE.toString();
    public final static String GT = Operator.GT.toString();
    public final static String GE = Operator.GE.toString();
    public final static String PM = Operator.PM.toString();
    public final static String RX = Operator.RX.toString();
    public final static String SX = Operator.SX.toString();
    public final static String IN = Operator.IN.toString();
     */

    /**
     * Returns the variable on which this condition acts.
     * 
     * @return
     */
    public String getVariable();
    
    
    /**
     * Sets the variable on which this condition needs to match.
     * 
     * @param variable
     */
    public void setVariable( String variable );
    
    
    /**
     * This method returns the value used by this condition to match
     * the value of the variable against.
     * 
     * @return
     */
    public String getValue();
    
    
    /**
     * This returns a textual representation of the operator of this condition. 
     * 
     * @return
     */
    public String getOperator();
    
    /**
     * 
     * 
     * @param pattern
     * @param input
     * @return
     */
    public boolean matches( String pattern, String input );
}