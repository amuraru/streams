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

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * 
 * This is a small helper-class that wraps around the java md5 message-digest
 * and computes the md5 hex-string of a given input string. It is basically
 * just a wrapper and uses the MessageDigest class of the Java runtime library. 
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class MD5 {
    
    /**
     * Create a hex string of the MD5 sum of the given data (byte[] array).
     * 
     * @param array The data to compute the hash from.
     * @return The hex string of the MD5 hash.
     */
    private static String hex(byte[] array) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).toLowerCase().substring(1,3));
        }
        return sb.toString();
    }
    
    
    /**
     * This method will simply return the computed MD5 sum of the given input
     * string. The sum is returned in hex notation.
     * <p>
     * This is just a convenience method. Calls this method are delegated to
     * <code>md5( byte[] data)</code>.
     * </p>
     * 
     * @param message The message to create the digest from.
     * @return The hex string of the message digest.
     */
    public static String md5 (String message) { 
        return md5( message.getBytes() );
    }
    
    
    /**
     * This method will simply return the computed MD5 sum of the given input
     * data. The sum is returned in hex notation.
     * 
     * @param message The message to create the digest from.
     * @return The hex string of the message digest.
     */
    public static String md5 ( byte[] data ){
    	try {
    		MessageDigest md = MessageDigest.getInstance("MD5");
    		return hex ( md.digest(data) );
    	} catch (NoSuchAlgorithmException e){
    		e.printStackTrace();
    		return null;
    	}
    }
    
    
    public static String md5( Serializable object ){
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream( out );
            oos.writeObject( object );
            oos.flush();
            oos.close();
            return md5(out.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5( object.toString().getBytes() );
    }
}