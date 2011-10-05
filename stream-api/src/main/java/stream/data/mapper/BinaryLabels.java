package stream.data.mapper;

import java.io.Serializable;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.data.DataUtils;


/**
 * This class implements a simple strategy to map labels to { -1, +1 }
 * 
 * @author chris@jwall.org
 *
 */
public class BinaryLabels
implements DataProcessor
{

	String labelAttribute;
	String positive = null;
	Double threshold = null;

	public BinaryLabels(){
		this( null, null );
	}

	public BinaryLabels( String label ){
		this( label, null );
	}

	public BinaryLabels( String label, String positive ){
		this.labelAttribute = label;
		this.positive = positive;
	}



	/**
	 * @return the threshold
	 */
	 public Double getThreshold() {
		 return threshold;
	 }

	 /**
	  * @param threshold the threshold to set
	  */
	 public void setThreshold(Double threshold) {
		 this.threshold = threshold;
	 }

	 /**
	  * @return the key
	  */
	 public String getLabelAttribute() {
		 return labelAttribute;
	 }


	 /**
	  * @param key the key to set
	  */
	 public void setLabelAttribute(String key) {
		 this.labelAttribute = key;
	 }


	 /**
	  * @return the positive
	  */
	 public String getPositive() {
		 return positive;
	 }


	 /**
	  * @param positive the positive to set
	  */
	 public void setPositive(String positive) {
		 this.positive = positive;
	 }




	 @Override
	 public Data process(Data data)
	 {
		 if( labelAttribute == null ){
			 for( String k : data.keySet() ){
				 if( DataUtils.isAnnotation( k ) && k.startsWith( "@label" ) ){
					 labelAttribute = k;
					 break;
				 }
			 }
		 }

		 if( labelAttribute == null )
			 return data;

		 Serializable val = data.get( labelAttribute );
		 if( val == null )
			 return data;

		 
		 if( val instanceof Double ){
			 //
			 // handle numerical values by threshold
			 //
			 if( threshold == null )
				 threshold = 0.0d;

			 Double d =(Double)val;
			 if( d < threshold ){
				 data.put( labelAttribute, -1.0d );
			 } else 
				 data.put( labelAttribute, 1.0d );
			 return data;

		 } else {
			 //
			 // handle nominal values by checking against the 
			 // defined positive value.
			 //
			 if( positive == null )
				 positive = val.toString();

			 if( positive.equals( val ) )
				 data.put( labelAttribute, 1.0d );
			 else
				 data.put( labelAttribute, -1.0d );

			 return data;
		 }
	 }
}