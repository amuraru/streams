/**
 * 
 */
package com.rapidminer.stream.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author chris
 *
 */
public class OperatorList {

	TreeSet<OperatorInfo> operators = new TreeSet<OperatorInfo>();

	String name = "DataStream";
	String version = "5.0";
	String docBundle = "com/rapidminer/resources/i18n/OperatorsDoc";


	public void add( OperatorInfo info ){
		operators.add( info );
	}
	
	public void add( Collection<OperatorInfo> infos ){
		operators.addAll( infos );
	}
	
	public void setDefaultGroup( String grp ){
		for( OperatorInfo op : operators ){
			if( op.getGroup() == null || op.getGroup().trim().isEmpty() ){
				op.setGroup( grp );
			}
		}
	}

	public String toXML(){
		StringBuffer s = new StringBuffer( "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" );
		s.append( "<operators name=\"" + name + "\" version=\"" + version + "\" docbundle=\"" + docBundle + "\">\n" );

		String group = null;

		for( OperatorInfo info : operators ){

			if( group == null ){
				group = info.getGroup();
				s.append( "<group key=\"" + group + "\">\n" );
			}

			if( !group.equals( info.getGroup() ) ){
				s.append( "</group>\n" );
				s.append( "<group key=\"" + info.getGroup() + "\">\n" );
			}

			s.append( info.toXML() );
			
			group = info.getGroup();
		}

		s.append( "</operators>" );
		return s.toString();
	}
	
	
	public void insertIntoOperatorsXml( File source, File file ) throws Exception {
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse( source );

		insertIntoDom( doc );
		
		Transformer trans = TransformerFactory.newInstance().newTransformer();
		trans.setOutputProperty( OutputKeys.STANDALONE, "no" );
		trans.setOutputProperty( OutputKeys.ENCODING, "utf-8" );
		trans.setOutputProperty( OutputKeys.VERSION, "1.0" );
		trans.setOutputProperty( OutputKeys.INDENT, "yes" );
		trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		
		OutputStream out = new FileOutputStream( file );
		trans.transform( new DOMSource( doc ), new StreamResult( out ) );
		out.close();
		//trans.transform( new DOMSource( doc ), new StreamResult( System.out ) );
	}
	
	
	private void insertIntoDom( Document doc ){
	
		Map<String,List<OperatorInfo>> groups = new LinkedHashMap<String,List<OperatorInfo>>();
		for( OperatorInfo info : operators ){
			String grp = info.getGroup();
			if( grp == null )
				grp = "";
			List<OperatorInfo> list = groups.get( grp );
			if( list == null ){
				list = new ArrayList<OperatorInfo>();
			}
			
			list.add( info );
			groups.put( grp, list );
		}
		
		
		for( String group : groups.keySet() ){
			Element grp = findOrCreateGroupElement( group, doc );
			for( OperatorInfo info : groups.get( group ) ){
				this.insertOperator( info, grp, doc );
			}
		}
	}
	
	private Element findOrCreateGroupElement( String groupKey, Document doc ){

		NodeList list = doc.getElementsByTagName( "group" );
		for( int i = 0; i < list.getLength(); i++ ){
			
			Node node = list.item( i );
			if( node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals( "group" ) ){

				Element element = (Element) node;
				if( groupKey.equals( element.getAttribute( "key" ) ) ){
					return element;
				}
			}
		}


		Element ops = doc.getDocumentElement();
		
		Element group = doc.createElement( "group" );
		group.setAttribute( "key", groupKey );
		ops.appendChild( group );

		return group;
	}
	
	
	private void insertOperator( OperatorInfo info, Element group, Document doc ){
		
		Element operator = doc.createElement( "operator" );
		
		Element key = doc.createElement( "key" );
		key.setTextContent( info.getKey() );
		operator.appendChild( key );
		
		Element clazz = doc.createElement( "class" );
		clazz.setTextContent( info.getClassName() );
		operator.appendChild( clazz );
		
		group.appendChild( operator );
	}
}