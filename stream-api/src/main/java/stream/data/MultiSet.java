/*******************************************************************************
 * Copyright (C) 2010 Christian Bockermann <chris@jwall.org>
 *    
 *   This file is part of the jwall-tools. The jwall-tools is a set of Java
 *   based commands for managing ModSecurity related task such as counting
 *   events in audit-log files, generating HTML file from Apache configurations
 *   and other.
 *   More information and documentation for the jwall-tools can be found at
 *   
 *                      http://www.jwall.org/jwall-tools
 *   
 *   This program is free software; you can redistribute it and/or modify it under
 *   the terms of the GNU General Public License as published by the Free Software
 *   Foundation; either version 3 of the License, or (at your option) any later version.
 *   
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY
 *   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *   FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 *   You should have received a copy of the GNU General Public License along with this 
 *   program; if not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 *  Copyright (C) 2007 Christian Bockermann <chris@jwall.org>
 *
 *  This file is part of the TestClient application.
 *
 *  TestClient is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  TestClient is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package stream.data;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class MultiSet<E extends Serializable>
    implements Serializable, Iterable<E>
{
    /** The unique class ID */
    private static final long serialVersionUID = 8605288889624476113L;
    private Hashtable<E, Long> table;
    private int total;
    
    public MultiSet(){
        total = 0;
        table = new Hashtable<E, Long>();
    }
    
    
    /**
     * Erh&ouml;ht den Count f&uuml;r <code>key</code> im 1.
     * @param key
     */
    public void add(E key){
        if(table.get(key) == null){
//            System.out.println("Adding "+key+" for the first time!");
            table.put(key, new Long(1));
        } else {
//            System.out.println("Adding "+key+" for another time!");
            Long l = (Long) table.get(key);
            Long l2 = new Long(l.longValue() + 1);
            
            table.remove(key);
            table.put(key, l2);
        }        
        
        total++;
    }
    
    /**
     * Liefert den Count f&uuml;r <code>key</code> zur&uuml;ck.
     * 
     * @param key
     * @return
     */
    public Long getCount(E key){
        if(table.get(key) == null)
            return new Long(0);
        
        return (Long) table.get(key);
    }
    
    
    public E remove(E o){
        if(table.get(o) == null)
            return null;
        
        Long c = (Long) table.get(o);
        Long l = new Long(c.longValue() - 1);
        if(l.longValue() == 0)
            table.remove(o);
        else {
            table.remove(o);
            table.put(o, l);
        }
        return o;
    }
    
    public E removeAll(E o){
        if(table.get(o) == null)
            return null;
        
        table.remove(o);
        return o;
    }
    
    
    /**
     * 
     * @deprecated
     * @param key
     * @return
     */
    public double getPercentage(E key){
        // TODO: total wird unter umstaenden mehrfach gezaehlt
        Long l = getCount(key);
        
        if(l.longValue() > 0)
            return l.doubleValue() / (double) total;
        
        return 0.0d;
    }
    
    
    public Set<E> getValues(){
        
        Set<E> s = new TreeSet<E>();
        
        for(E el : table.keySet())
            if(table.get(el) != null && table.get(el) > 0)
                s.add(el);
        
        return s;
    }
    
    public Long getTotal(){
        return new Long(total);
    }
    
    public String toString(){
        StringBuffer sb = new StringBuffer("{ ");
        
        Iterator<E> it = getValues().iterator();
        while(it.hasNext()){
            E key = it.next();
            sb.append("\""+key+"\" ("+getCount( key )+")");
            if(it.hasNext())
                sb.append(", ");
        }
        sb.append(" }");
        
        return sb.toString();
    }
    
    public boolean hasValue(E v){
        return table.get(v) != null && table.get(v).longValue() > 0;
    }
    
    
    /**
     * This returns an iterator that can be used to iterate over the
     * set of distinct values contained in this multi-set. Thus, the
     * iteration does not contain two values which are equal.
     * 
     * @return An iterator of all distinct values.
     */
    public Iterator<E> iterator(){
        return getValues().iterator();
    }
    
    
    /**
     * Returns the number of values contained in this set. This is the number
     * of distinct values times the count of each distinct value.
     * 
     * @return The total number of values.
     */
    public int size(){
        return total;
    }
    
    
    /**
     * Returns the number of distinct values contained in this multiset.
     * 
     * @return Number of distinct values.
     */
    public int distinctSize(){
        return table.values().size();
    }
    
    
    public void clear(){
        table.clear();
        total = 0;
    }
    
    public void addCount(E key, Long c){
        Long count = 0L;
        if(table.get(key) != null)
            count = table.remove(key);
        
        table.put(key, count + c);
    }
}
