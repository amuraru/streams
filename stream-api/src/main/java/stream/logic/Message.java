/**
 * 
 */
package stream.logic;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.data.filter.ExpressionCompiler;
import stream.data.filter.Expression;
import stream.util.MacroExpander;

/**
 * @author chris
 *
 */
public class Message implements DataProcessor {

	Expression filter;
	String txt;
	String condition;
	
	
	
	/**
	 * @return the txt
	 */
	public String getTxt() {
		if( txt == null )
			return "";
		
		return txt;
	}



	/**
	 * @param txt the txt to set
	 */
	public void setTxt(String txt) {
		this.txt = txt;
	}



	/**
	 * @return the condition
	 */
	public String getCondition() {
		return condition;
	}



	/**
	 * @param condition the condition to set
	 */
	public void setCondition(String condition) {
		this.condition = condition;
		try {
			this.filter = ExpressionCompiler.parse( condition );
		} catch (Exception e) {
			throw new RuntimeException( e.getMessage() );
		}
	}



	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		
		if( filter == null || filter.matches( data ) ){
			String msg = MacroExpander.expand( getTxt(), data );
			System.out.println( msg );
		}
		
		return data;
	}
}