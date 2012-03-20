/**
 * 
 */
package fact.plugin;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import stream.data.Data;
import stream.plugin.DataObject;

import com.rapidminer.operator.ResultObject;

/**
 * @author chris
 *
 */
public class FactEventObject extends DataObject implements ResultObject {
	
	/** The unique class ID */
	private static final long serialVersionUID = 2749158655439871907L;
	

	/**
	 * @param data
	 */
	public FactEventObject(Data data) {
		super(data);
	}


	/**
	 * @see com.rapidminer.operator.ResultObject#getName()
	 */
	@Override
	public String getName() {
		return "FactEvent";
	}


	/**
	 * @see com.rapidminer.operator.ResultObject#toResultString()
	 */
	@Override
	public String toResultString() {
		return "FactEventObject";
	}


	/**
	 * @see com.rapidminer.operator.ResultObject#getResultIcon()
	 */
	@Override
	public Icon getResultIcon() {
		return null;
	}


	/**
	 * @see com.rapidminer.operator.ResultObject#getActions()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List getActions() {
		return new ArrayList();
	}
}
