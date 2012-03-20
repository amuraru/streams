/**
 * 
 */
package stream.data;

import stream.data.filter.ExpressionCompiler;
import stream.data.filter.Expression;

/**
 * @author chris
 *
 */
public abstract class ConditionedDataProcessor implements DataProcessor {

	Expression condition;
	
	/**
	 * @return the condition
	 */
	public String getCondition() {
		if( condition == null )
			return "";
		
		return condition.toString();
	}

	
	/**
	 * @param condition the condition to set
	 */
	public void setCondition(String condition) {
		try {
			this.condition = ExpressionCompiler.parse( condition );
		} catch (Exception e ){
			throw new RuntimeException( e.getMessage() );
		}
	}

	
	public boolean matches( Data item ){
		return ( condition == null || condition.matches( item ) );
	}
	

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		if( matches( data ) )
			return processMatchingData( data );

		return data;
	}
	
	
	public abstract Data processMatchingData( Data data );
}