package stream.experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ReportProcessor 
	extends VariableContext
{

	static Logger log = LoggerFactory.getLogger( ReportProcessor.class );
	
	public ReportProcessor( Map<String,String> vars ){
		super( vars );
	}
	
	public void process( InputStream input, File output ) throws Exception {
		
		PrintStream out = new PrintStream( new FileOutputStream( output ) );
		BufferedReader r = new BufferedReader( new InputStreamReader( input ) );
		String line = r.readLine();
		while( line != null ){
			String str = expand( line );
			out.println( str );
			line = r.readLine();
		}
		
		r.close();
		out.flush();
		out.close();
	}
	

	public static void copyResource( String uri, File outdir ) throws Exception {
		URL url = ReportProcessor.class.getResource( uri );
		if( url == null ){
			log.error( "Resource '{}' not found!", uri );
			return;
		}
		
		String path = url.getFile();
		String fileName = path;
		int idx = path.lastIndexOf( "/" );
		if( idx >= 0 )
			fileName = path.substring( idx + 1 );
		
		File out = new File( outdir + File.separator + fileName );
		FileOutputStream fos = new FileOutputStream( out );
		InputStream in = url.openStream();
		byte[] buf = new byte[1024];
		int read = 0;
		do {
			read = in.read( buf );
			if( read > 0 )
				fos.write( buf, 0, read );
		} while( read > 0 );
		fos.close();
		in.close();
	}
	
	public static void copyResource( String uri, OutputStream out ) throws Exception {
		URL url = ReportProcessor.class.getResource( uri );
		if( url == null ){
			log.error( "Resource '{}' not found!", uri );
			return;
		}
		
		InputStream in = url.openStream();
		byte[] buf = new byte[1024];
		int read = 0;
		do {
			read = in.read( buf );
			if( read > 0 )
				out.write( buf, 0, read );
		} while( read > 0 );
		out.close();
		in.close();
	}
	
	public void processXML( File input, File output ) throws Exception {
		log.debug( "Processing document {} ~> {}", input, output );
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse( input );

		process( doc.getDocumentElement() );

		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.transform( new DOMSource( doc ), new StreamResult( new FileOutputStream( output ) ) );
	}


	protected void process( Node node ){
		log.debug( "Processing node {}", node.getNodeName() );

		if( node.getNodeType() == Node.TEXT_NODE )
			node.setTextContent( expand( node.getTextContent() ) );

		if( node.getNodeType() == Node.ATTRIBUTE_NODE )
			node.setNodeValue( expand( node.getNodeValue() ) );

		NamedNodeMap attributes = node.getAttributes();
		if( attributes != null ){
			for( int a = 0; a < attributes.getLength(); a++ )
				process( attributes.item( a ) );
		}

		NodeList children = node.getChildNodes();
		if( children != null ){
			for( int i = 0; i < children.getLength(); i++ )
				process( children.item( i ) );
		}
	}


	
	public String getDataSourceInfo( URL url ){
		try {
			URL infoUrl = url;
			if( ! url.getFile().endsWith( ".info" ) ){
				infoUrl = new URL( url.toString() + ".info" );
			}
			
			log.info( "Checking for DataSource information at URL {}", infoUrl );
			StringBuffer s = new StringBuffer();
			BufferedReader r = new BufferedReader( new InputStreamReader( infoUrl.openStream() ) );
			String line = r.readLine();
			while( line != null ){
				s.append( line + "\n" );
				line = r.readLine();
			}
			r.close();
			return s.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	


	public static void main( String[] args ) throws Exception {
		File in = new File( "/Users/chris/Documents/jwall.org/AuditConsole/console-reporting/output/report.db" );
		File out = new File( "/Users/chris/Documents/jwall.org/AuditConsole/console-reporting/output/report.db.postprocessed" );

		Map<String,String> vars = new HashMap<String,String>();
		vars.put( "AUTHOR:FIRSTNAME", "Christian" );
		vars.put( "AUTHOR:SURNAME", "Bockermann" );
		vars.put( "REQUEST_HEADERS:Host", "Server" );
		vars.put( "RESPONSE_STATUS", "Server Response Code" );
		ReportProcessor p = new ReportProcessor( vars );
		p.processXML( in, out );
	}
}