package stream.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to annotate class fields and define them as
 * parameter.
 * 
 * @author chris
 *
 */
@Target( ElementType.FIELD )
@Retention( RetentionPolicy.RUNTIME )
public @interface Description {

	/**
	 * A descriptive text.
	 * 
	 * @return
	 */
	String value();
	
	String tooltip();
}