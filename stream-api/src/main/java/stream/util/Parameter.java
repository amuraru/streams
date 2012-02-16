package stream.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to annotate class methods and define them as
 * parameter.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
@Target( { ElementType.FIELD, ElementType.METHOD } )
@Retention( RetentionPolicy.RUNTIME )
public @interface Parameter {

	/**
	 * An optional name for the parameter, which determines the <b>external</b> name of that
	 * parameter, i.e. within a configuration file.
	 * 
	 * @return
	 */
	String name() default "";
	
	/**
	 * An optional parameter for the parameter which states whether this parameter is optional
	 * or required.
	 * 
	 * @return
	 */
	boolean required() default true;
	

	/**
	 * The default minimum for an numerical parameters
	 * 
	 * @return
	 */
	double min() default 0.0d;
	
	
	/**
	 * The default maximum for any numerical parameters
	 * 
	 * @return
	 */
	double max() default Double.MAX_VALUE;
	
	
	/**
	 * A default list of possible values for string parameters
	 * 
	 * @return
	 */
	String[] values() default {};
	
	
	/**
	 * The default value as string for this parameter
	 * @return
	 */
	String defaultValue() default "";
	
	
	String description() default "";
	
	
	Class<?> type() default Object.class;
}