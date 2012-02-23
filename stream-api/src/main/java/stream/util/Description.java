package stream.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * This annotation can be used to specify documentation settings for the 
 * annotated data processor implementation.
 * </p>
 * 
 * @author Christan Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
@Target( ElementType.TYPE )
@Retention( RetentionPolicy.RUNTIME )
public @interface Description {

	/**
	 * A name for the DataProcessor implementation.
	 * 
	 * @return
	 */
	String name() default "";
	
	/**
	 * A descriptive text.
	 * 
	 * @return
	 */
	String text() default "";
	
	
	/**
	 * A URL reference for further documentation or description.
	 * 
	 * @return
	 */
	String url() default "";
	
	
	/**
	 * A group name to which the annotated class belongs.
	 * @return
	 */
	String group() default "";
}