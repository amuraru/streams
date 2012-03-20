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
 * This condition checks for equality.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class ConditionEQ
	extends BinaryOperator
{
	/** The unique class ID */
	private static final long serialVersionUID = -7185932909087120854L;

	public ConditionEQ() {
		super( "@eq", "=", "==", "!=" );
	}

	public boolean eval( Serializable input, String pattern ){
		return input != null && input.toString().equals( pattern );
	}
}