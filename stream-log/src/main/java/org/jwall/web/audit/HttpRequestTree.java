package org.jwall.web.audit;

import java.io.Serializable;

import org.jwall.sql.audit.ASTNode;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.data.TreeNode;

public class HttpRequestTree
    implements DataProcessor
{

    @Override
    public Data process(Data data)
    {
        if( data.containsKey( "REQUEST_URI" ) ){
            
            TreeNode request = new ASTNode( "HttpRequest" );
            TreeNode header = new ASTNode( "Header" );
            TreeNode body = null;

            if( data.containsKey( "REQUEST_METHOD" ) ){
                TreeNode method = new ASTNode( "Method" );
                method.addChild( new ASTNode( data.get( "REQUEST_METHOD" ).toString() ) );
                header.addChild( method );
            }

            String uri = data.get( "REQUEST_URI" ).toString();
            String path = uri;

            TreeNode pathNode;
            String qs = null;
            int idx = path.indexOf( "?" );
            if( idx > 0 ){
                path = uri.substring( 0, idx );
                qs = uri.substring( idx + 1 );
                pathNode = new ASTNode( path );
            } else {
                pathNode = new ASTNode( uri );
            }
            
            TreeNode reqPath = new ASTNode( "URI" );
            reqPath.addChild( pathNode );
            header.addChild( reqPath );
            
            if( qs != null ){
                TreeNode getParams = this.parseQueryString( "Parameter", qs );
                header.addChild( getParams );
            }
            
            Serializable reqBody = data.get( "REQUEST_BODY" );
            if( reqBody != null && ! "".equals( reqBody.toString() ) ){
                body = new ASTNode( "Body" );
                TreeNode postParams = this.parseQueryString( "Parameter", reqBody.toString() );
                body.addChild( postParams );
            }
            
            request.addChild( header );
            if( body != null )
                request.addChild( body );
            
            data.put( "@tree:request", request );
        }
        
        return data;
    }
    
    protected TreeNode parseQueryString( String rootLabel, String qs ){
        TreeNode root = new ASTNode( rootLabel );
        
        String[] tok = qs.split( "&" );
        for( String pairs : tok ){
            
            String[] kv = pairs.split( "=" );
            if( kv.length == 2 ){
                TreeNode param = new ASTNode( kv[0] );
                TreeNode value = new ASTNode( kv[1] );
                param.addChild( value );
                root.addChild( param );
            } else {
                root.addChild( new ASTNode( pairs ) );
            }
        }
        return root;
    }
}