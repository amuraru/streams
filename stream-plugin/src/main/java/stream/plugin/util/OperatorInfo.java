/**
 * 
 */
package stream.plugin.util;

import org.w3c.dom.Element;

/**
 * @author chris
 *
 */
public class OperatorInfo implements Comparable<OperatorInfo> {

	
	String group;
	String key;
	String className;
	String icon = null;
	String docText = null;
	
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
	
	
	

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}


	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		if( icon == null || "".equals( icon.trim() ) )
			return;
		this.icon = icon;
	}


	/**
	 * @return the docText
	 */
	public String getDocText() {
		return docText;
	}


	/**
	 * @param docText the docText to set
	 */
	public void setDocText(String docText) {
		this.docText = docText;
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
	
	
	public boolean equals( Object o ){
		if( o instanceof OperatorInfo ){
			return this.toString().equals( o.toString() );
		}
		return false;
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