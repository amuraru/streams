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
package stream.util;

import org.apache.commons.codec.binary.Base64;


/**
 * 
 * This class is basically just a wrapper around a <i>real</i> codec implementation.
 * The current implementation just uses apache commons codec.
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class Base64Codec {

    
    /**
     * Encode a chunk of byte using Base64 encoding.
     * 
     * @param data The data to encode.
     * @return The encoded data.
     */
	public static byte[] encode( byte[] data ){
		return Base64.encodeBase64( data );
	}
	
	
	public static String encode( String data ){
		return new String( Base64.encodeBase64( data.getBytes() ) );
	}

	
	/**
	 * Decode a chunk of bytes using Base64 decoding.
	 * 
	 * @param data The data to decode.
	 * @return The decoded data.
	 */
	public static byte[] decode( byte[] data ){
		return Base64.decodeBase64( data );
	}
}
