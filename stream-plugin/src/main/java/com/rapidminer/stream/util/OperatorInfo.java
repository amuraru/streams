/**
 * 
 */
package com.rapidminer.stream.util;

import org.w3c.dom.Element;

/**
 * @author chris
 *
 */
public class OperatorInfo implements Comparable<OperatorInfo> {

	
	String group;
	String key;
	String className;
	
	
	public OperatorInfo( String grp, String key, String className ){
		this.group = grp;
		this.key = key;
		this.className = className;
	}


	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}


	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}


	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}


	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}


	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}


	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}


	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(OperatorInfo arg0) {
		
		if( !this.group.equals( arg0.group ) ){
			return group.compareTo( arg0.group );
		}
		
		return getKey().compareTo( arg0.getKey() );
	}
	
	public String toString(){
		return "Group: " + group + ", Key: " + key + ", Class: " + className;
	}
	
	public String toXML(){
		StringBuffer s = new StringBuffer();
		s.append( "\t<operator>\n" );

		s.append( "\t\t<key>" + getKey() + "</key>\n" );
		s.append( "\t\t<class>" + getClassName() + "</class>\n" );
		
		s.append( "\t</operator>\n" );
		return s.toString();
	}
	
	
	public void addDomNode( Element node ){
	}
}