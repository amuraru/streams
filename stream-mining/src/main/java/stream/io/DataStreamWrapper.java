/**
 * 
 */
package stream.io;

import java.util.ArrayList;

import moa.AbstractMOAObject;
import moa.core.InstancesHeader;
import moa.streams.InstanceStream;
import stream.data.Data;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * <p>
 * This class implements a wrapper for the DataStream interface. It provides instances
 * read from a data stream.
 * </p>
 * 
 * @author Christian Bockeramnn (christian.bockermann@udo.edu)
 *
 */
public class DataStreamWrapper extends AbstractMOAObject implements InstanceStream {

	/** The unique class ID */
	private static final long serialVersionUID = 152647770521783777L;

	DataStream source;
	Data last = null;
	ArrayList<Attribute> attributes = new ArrayList<Attribute>();
	ArrayList<String> attributeNames = new ArrayList<String>();
	
	public DataStreamWrapper( DataStream source ) throws Exception {
		this.source = source;
		last = this.source.readNext();
	}
	
	
	/**
	 * @see moa.streams.InstanceStream#estimatedRemainingInstances()
	 */
	@Override
	public long estimatedRemainingInstances() {
		return 1;
	}

	
	/**
	 * @see moa.streams.InstanceStream#getHeader()
	 */
	@Override
	public InstancesHeader getHeader() {
		for( String attribute : source.getAttributes().keySet() ){
			Attribute a = new Attribute( attribute );
			if( ! attributes.contains( a ) )
				attributes.add( a );
		}
		
		Instances instances = new Instances( "GeneratedStream", attributes, 10 );
		return new InstancesHeader( instances );
	}

	
	public Instance wrap( Data item ){
		
		DenseInstance inst = new DenseInstance( attributes.size() );
		int i = 0;
		for( Attribute a : attributes ){
			String name = attributeNames.get( i );
			if( isNumerical( name, item ) )
				inst.setValue( i, (Double) item.get( name ) );
			else {
				String val = item.get( name ).toString();
				int idx = a.indexOfValue( val );
				if( idx < 0 )
					idx = a.addStringValue( val );
				
				if( idx < 0 )
					inst.setMissing( i );
				else
					inst.setValue( i, idx );
			}
			i++;
		}
		
		return inst;
	}
	

	public static boolean isNumerical( String key, Data item ){
		return item.containsKey( key ) && item.get( key ).getClass() == Double.class;
	}

	public static boolean isNominal( String key, Data item ){
		return !isNumerical( key, item );
	}
	
	/**
	 * @see moa.streams.InstanceStream#hasMoreInstances()
	 */
	@Override
	public boolean hasMoreInstances() {
		return false;
	}

	
	/**
	 * @see moa.streams.InstanceStream#isRestartable()
	 */
	@Override
	public boolean isRestartable() {
		return false;
	}

	/**
	 * @see moa.streams.InstanceStream#nextInstance()
	 */
	@Override
	public Instance nextInstance() {
		Data cur = last;
		try {
			last = source.readNext();
		} catch (Exception e) {
			last = null;
		}
		return wrap(cur);
	}

	
	/**
	 * @see moa.streams.InstanceStream#restart()
	 */
	@Override
	public void restart() {
	}

	/**
	 * @see moa.MOAObject#getDescription(java.lang.StringBuilder, int)
	 */
	@Override
	public void getDescription(StringBuilder sb, int indent) {
	}
}