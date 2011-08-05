/**
 * 
 */
package stream.generator.ui;

/**
 * @author chris
 *
 */
public interface TaskListener {
	
	public void taskStarted();

	public void progress( String status, Double completed );
	
	public void taskCompleted();
}