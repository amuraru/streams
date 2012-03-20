/**
 * 
 */
package fact.plugin.operators;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import stream.data.Data;

import com.rapidminer.BreakpointListener;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.parameter.ParameterTypeBoolean;

import fact.FactViewerPanel;
import fact.plugin.FactEventObject;

/**
 * @author chris
 *
 */
public class FactViewerOperator extends AbstractFactEventOperator {

	public final static String PAUSE_AFTER_SHOW = "Pause for each event";
	
	boolean pause = true;
	JFrame frame;
	FactViewerPanel eventPanel;
	
	/**
	 * @param description
	 */
	public FactViewerOperator(OperatorDescription description) {
		super(description);
		this.addParameterType( new ParameterTypeBoolean( PAUSE_AFTER_SHOW, "Add a breakpoint for each event.", true ) );
	}

	
	public void doWork() throws OperatorException {
		super.doWork();

		pause = getParameterAsBoolean( PAUSE_AFTER_SHOW );
		this.setBreakpoint( BreakpointListener.BREAKPOINT_AFTER, pause );
	}
	
	
	/**
	 * @see fact.plugin.operators.AbstractFactEventOperator#process(fact.plugin.FactEventObject)
	 */
	@Override
	public FactEventObject process(FactEventObject event) throws Exception {
		
		if( frame == null || eventPanel == null ){
			frame = new JFrame( "View Event" );
			eventPanel = new FactViewerPanel();
			frame.getContentPane().setLayout( new BorderLayout() );
			frame.getContentPane().add( eventPanel, BorderLayout.CENTER );
		}
		
		Data data = event.getWrappedDataItem();
		eventPanel.setEvent( data );
		
		frame.setAlwaysOnTop( true );
		frame.setVisible( true );
		return event;
	}
}