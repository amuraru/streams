package stream.tools;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.data.Data;
import stream.io.DataStream;
import stream.util.ObjectFactory;

public class StreamRunner
{
    static Logger log = LoggerFactory.getLogger( StreamRunner.class );
    ObjectFactory objectFactory = ObjectFactory.newInstance();
    
    String source = null;
    Map<String,DataStream> streams = new LinkedHashMap<String,DataStream>();

    public StreamRunner( URL url ) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse( url.openStream() );
        
        if( !doc.getDocumentElement().getNodeName().equals( "experiment" ) ){
            throw new Exception( "Expecting root element to be 'experiment'!" );
        }
        
        this.init( doc );
    }
    
    
    public void init( Document doc ) throws Exception {
        Element root = doc.getDocumentElement();
        source = root.getAttribute( "source" );
        if( source ==  null )
            throw new Exception( "No source-key has been defined! Expecting 'source' element!" );
        
        NodeList children = root.getChildNodes();
        
        for( int i = 0; i < children.getLength(); i++ ){
            Node node = children.item( i );
            if( node instanceof Element && node.getNodeName().equals( "Stream" ) ){
                Element child = (Element) node;
                try {
                    Map<String,String> attr = objectFactory.getAttributes( child );
                    String id = attr.get( "id" );
                    
                    DataStream stream = createStream( attr );
                    if( stream != null ){
                        if( id == null )
                            id = "" + stream;
                        streams.put( id, stream );
                    }
                    
                } catch (Exception e) {
                    log.error( "Failed to create object: {}", e.getMessage() );
                    e.printStackTrace();
                }
            }
        }
    }
    
    
    public static DataStream createStream( Map<String,String> params ) throws Exception {
        Class<?> clazz = Class.forName( params.get( "class" ) );
        Constructor<?> constr = clazz.getConstructor( URL.class );
        URL url = new URL(params.get("url") );
        DataStream stream = (DataStream) constr.newInstance( url );
        return stream;
    }
    
    
    public void run() throws Exception {
        
        if( streams.isEmpty() )
            throw new Exception( "No data-stream defined!" );
        
        DataStream stream = streams.get( source );
        if( stream == null )
            throw new Exception( "No stream found for source key '" + source + "'!" );
        
        
        
        Data item = stream.readNext();
        while( item != null ){
            log.info( "processing item: {}", item );
            item = stream.readNext();
        }
    }
    
    
    
}