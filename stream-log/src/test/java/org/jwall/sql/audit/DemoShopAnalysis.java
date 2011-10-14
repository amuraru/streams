package org.jwall.sql.audit;

import java.io.FileOutputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataImpl;
import stream.data.MultiSet;
import stream.data.mapper.Mapper;
import stream.data.tree.CountLeaves;
import stream.data.tree.CountNodes;
import stream.data.tree.TreeFeatures;
import stream.data.tree.TreeHeight;
import stream.io.CsvStream;
import stream.io.DataStream;
import stream.io.DataStreamWriter;

public class DemoShopAnalysis
{
    static Logger log = LoggerFactory.getLogger( DemoShopAnalysis.class );

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        URL url = DemoShopAnalysis.class.getResource( "/demo-shop.log" );
        log.info( "Reading stream from {}", url );
        DataStream stream = new CsvStream( url, "\\|" );

        SQLStreamParser sqlParser =  new SQLStreamParser( "sql:query" );
        sqlParser.addMapper( new Mapper<String,String>(){
            @Override
            public String map(String input) throws Exception {
                while( input.endsWith( "#" ) ){
                    input = input.substring( 0, input.length() - 1 );
                }

                if( input.indexOf( "AND SLEEP(5)" ) >= 0 ){
                    return input.replace( "AND SLEEP(5)", "AND myDummySleep > SLEEP(5)" );
                }
                
                /*
                if( input.matches( "SLEEP\\(\\d+\\)" ) ){
                    input = input.replaceAll( "SLEEP\\(\\d+\\)", "myDummySleep > SLEEP(5)" );
                }
                 */



                return input;
            }
        });
        stream.addPreprocessor( sqlParser );

        int count = 0;
        int syntaxOk = 0;
        int parseOk = 0;

        DataStreamWriter writer = new DataStreamWriter( new FileOutputStream( "/Users/chris/sql-trees.csv" ), ";" );

        TreeFeatures tf = new TreeFeatures();
        tf.add( new CountLeaves() );
        tf.add( new CountNodes() );
        tf.add( new TreeHeight() );

        MultiSet<String> stmts = new MultiSet<String>();
        MultiSet<String> counts = new MultiSet<String>();
        //PrintStream out = new PrintStream( new FileOutputStream( "/Users/chris/sql-trees.csv" ) );

        Data item = stream.readNext();
        while( item != null ){
            if(("" + item.get("sql:query")).indexOf( "SLEEP" ) >0 ){
                log.info( "Item has valid SQL: {} => {}", item.get( "sql:ok" ), item.get( "sql:query" ) );
                log.info( "Item has query-tree: {}", item.get( "@tree:sql:query" ) );
            }
            boolean validMySQLSyntax = "true".equals( item.get( "sql:ok" ) );
            boolean sqlMapAttack = ( "" + item.get( "REQUEST_HEADERS:User-Agent") ).indexOf( "sqlmap" ) >= 0;

            Data example = new DataImpl();
            if( sqlMapAttack )
                example.put( "@label", "attack" );
            else
                example.put( "@label", "normal" );

            example.put( "query", item.get( "sql:query" ) );


            if( item.get( "@tree:sql:query" ) == null ){

                ASTNode attack = new ASTNode( "Statement" );
                attack.addChild( new ASTNode( "attack" ) );
                attack.addChild( new ASTNode( "attack" ) );
                attack.addChild( new ASTNode( "attack" ) );
                attack.addChild( new ASTNode( "attack" ) );
                item.put( "@tree:sql:query", attack );
                //example.put( "@tree:sql:query", attack );
            } else {
                example.put( "@tree:sql:query", item.get( "@tree:sql:query" ) );
            }
            
            if( example.get( "@tree:sql:query" ) != null )
                writer.process( example );

            boolean jsqlParse = item.get( "@tree:sql:query" ) != null;
            if( jsqlParse ){

                Data tree = new DataImpl();
                tree.put( "@label", 1.0 );
                if( sqlMapAttack ){
                    tree.put( "@label", -1.0 );
                    //out.print( "attack" );
                } else {
                    //out.print( "normal" );
                }

                /*
                out.print( ";" );
                out.println( item.get( "sql:query").toString().replaceAll( ";", "" ) );
                out.println( item.get( "@tree:sql:query" ) );
                 */
            }



            stmts.add( item.get( "sql:query" ) + "" );
            if( validMySQLSyntax ){
                syntaxOk++;
            }

            if( sqlMapAttack ){

            }
            if( item.get( "@tree:sql:query" ) != null ){
                parseOk++;
            }

            counts.add( "validMysql: " + validMySQLSyntax + "  sqlmapAttack: " + sqlMapAttack + "  jsqlParse: " + jsqlParse );


            count++;
            item = stream.readNext();
        }
        log.info( "" );
        log.info( "Need to write {} columns", writer.getHeaderNames().size() );
        //out.close();
        writer.close();
        log.info( "{} statements have been read, {} unique queries.", count, stmts.size() );
        log.info( "{} did contain valid MySQL syntax (no MySQL error)", syntaxOk );
        log.info( "{} could not be parsed by the 'jsqlparser'", count - parseOk );
        log.info( "" );
        for( String key : counts ){
            log.info( "  {} = {}", key, counts.getCount( key ) );
        }
    }
}