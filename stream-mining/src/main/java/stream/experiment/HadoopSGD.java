package stream.experiment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.model.NominalDistributionModel;
import stream.optimization.StochasticGradientDescent;

public class HadoopSGD 
	extends HadoopMapper
	implements DataProcessor
{
	StochasticGradientDescent learner;
	NominalDistributionModel<String> targets = new NominalDistributionModel<String>();
	
	public HadoopSGD( StochasticGradientDescent learner ){
		this.learner = learner;
	}

	@Override
	public Data process(Data data) {
		//learner.learn( new DataVector( data ) );
		Serializable val = data.get( "@label" );
		if( val != null ){
			targets.update( val.toString() );
		}
		return data;
	}
	
	public void writeResults( OutputStream out ){
		PrintStream p = new PrintStream( out );
		for( String target : targets.getElements() ){
			p.print( target + ":" + targets.getCount( target ) );
			p.print( " ");
		}
		p.println();
		p.close();
	}
	
	
	public static void main( String[] args ) throws Exception {
		InputStream in = System.in;
		OutputStream out = System.out;
		
		if( args.length > 0 )
			in = new FileInputStream( new File( args[0] ) );
		
		if( args.length > 1 )
			out = new FileOutputStream( new File( args[1] ) );
		
		HadoopSGD sgd = new HadoopSGD( null );
		sgd.processInput( in );
		sgd.writeResults( out );
	}
}
