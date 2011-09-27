package stream.data.storage;

public class StorageException extends Exception {

	/** The unique class ID */
	private static final long serialVersionUID = 401023101107535947L;

	public StorageException(){
		super();
	}
	
	public StorageException( String msg ){
		super( msg );
	}
}
