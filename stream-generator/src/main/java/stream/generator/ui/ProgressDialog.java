/**
 * 
 */
package stream.generator.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JProgressBar;

/**
 * @author chris
 *
 */
public class ProgressDialog extends JDialog implements TaskListener {

	/** The unique class ID */
	private static final long serialVersionUID = -6567618770119606903L;
	JProgressBar progressBar = new JProgressBar();
	
	public ProgressDialog( String title ){
		super();
		this.setTitle( title );
		this.setModal( true );
		this.setResizable( false );
		this.setSize( 600, 100 );
		
		
		progressBar.setMinimum( 0 );
		progressBar.setMaximum( 100 );
		progressBar.setValue( 0 );
		getContentPane().add( progressBar, BorderLayout.CENTER );
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation( (int) (d.getWidth() - this.getWidth()) / 2 , (int) (d.getHeight() - this.getHeight()) / 2 );
		this.pack();
	}


	/**
	 * @see stream.generator.ui.TaskListener#taskStarted()
	 */
	@Override
	public void taskStarted() {
		//setVisible( true );
	}
	
	
	/**
	 * @see stream.generator.ui.TaskListener#progress(java.lang.String, java.lang.Double)
	 */
	public void progress( String status, Double p ){
		if( p == 1.0d )
			this.setVisible( false );
		
		this.progressBar.setString( status );
		Double d = p * 100;
		progressBar.setValue( d.intValue() );
	}


	/**
	 * @see stream.generator.ui.TaskListener#taskCompleted()
	 */
	@Override
	public void taskCompleted() {
		setVisible( false );
	}
}