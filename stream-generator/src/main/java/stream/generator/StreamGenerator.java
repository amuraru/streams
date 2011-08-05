/**
 * 
 */
package stream.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import stream.data.Data;
import stream.generator.ui.TaskListener;
import stream.io.DataStream;
import stream.io.DataStreamListener;
import stream.io.DataStreamWriter;

/**
 * <p>
 * 
 * </p>
 * 
 * @author chris
 */
public class StreamGenerator extends Thread {

	DataStream source;
	DataStreamListener destination;
	Long limit = 0L;
	Long completed = 0L;
	boolean finished = false;
	List<TaskListener> listener = new ArrayList<TaskListener>();
	
	public StreamGenerator(){
	}


	public void setSource( DataStream stream ){
		this.source = stream;
	}


	public void setDestination( DataStreamListener l ){
		this.destination = l;
	}

	
	public void setLimit( Long limit ){
		this.limit = limit;
	}
	
	public Long getLimit(){
		return limit;
	}


	public Double getCompleted(){
		if( limit == 0L )
			return 1.0;
		return completed.doubleValue() / limit.doubleValue();
	}


	public void addTaskListener( TaskListener l ){
		this.listener.add(l);
	}
	

	public void run(){
		completed = 0L;
		
		for( TaskListener l : listener )
			l.taskStarted();
			
		while( completed < limit ){
			try {
				Data item = source.readNext();
				destination.dataArrived( item );
				completed++;
				
				if( completed % 10 == 0 ){
					for( TaskListener l : listener )
						l.progress( "", getCompleted() );
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		
		for( TaskListener l : listener )
			l.taskCompleted();
		
		finished = true;
	}

	
	public boolean isFinished(){
		return finished;
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		if( args.length < 2 ){
			System.err.println( "Stream generator requires 2 arguments:" );
			System.out.println( "    stream.generator.Generator  stream-config.xml output-file.csv\n" );
			return;
		}

		File file = new File( args[0] );
		File out = new File( args[1] );
		LabeledGaussianStream stream = LabeledGaussianStream.createFromFile( file );

		DataStreamWriter w = new DataStreamWriter( out, ";" );
		Long count = 0L;
		Long limit = 1000L;

		try {
			limit = new Long( System.getProperty( "limit" ) );
		} catch (Exception e) {
			limit = 1000L;
		}

		//StreamSummarizer sum = new StreamSummarizer();
		while( count < limit ){
			Data item = stream.readNext();
			w.dataArrived( item );
			//sum.dataArrived( item );
			count++;
		}
		//PrintStream desc = new PrintStream( new FileOutputStream( new File( out.getAbsolutePath().replaceAll( "csv$", "html" ) ) ) );
		//desc.println( stream.getDescription() );
		//desc.close();

		System.out.println( count + " items written to " + out );
		w.close();
	}
}