package stream.eval.size;

import java.io.Serializable;

import org.apache.wicket.util.lang.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Utility class for the determination of object sizes.</p>
 *
 * <p>The method {@link #sizeOf(java.lang.Object[])} gets a number of objects as input
 * and returns the sum of the object sizes in bytes as double. If an error occurs Double.NaN will
 * be returned.<br />For the size measurement there will be tested whether an object implements
 * the interface {@link Measurable} (which provides an own "getBytes" method so that the size of
 * an object can be set manually or calculated in a more sensible way). If this is the case,
 * {@link Measurable#getBytes()} will be used as size estimation. Otherwise
 * {@link Objects#sizeof(java.lang.Object)} will be used (basically serialization and then reading the
 * number of bytes serialized).</p>
 *
 * @author Benedikt Kulmann, Lukas Kalabis
 * @see Measurable
 */
public class SizeMeasurement {

    private static final Logger logger = LoggerFactory.getLogger(SizeMeasurement.class);

    /**
     * private constructor as it makes no sense to be able to create an instance of
     * a utility class / a class with static methods only
     */
    private SizeMeasurement() {
    }

    /**
     * Note: all passed objects have to be {@link Serializable} or implement {@link Measurable}!
     * 
     * @param objects The objects which sizes have to be determined. Standard size measurement
     * works with {@link Objects#sizeof(java.lang.Object)}. In case you want to calculate the object size
     * on your own (or even want to provide a fixed size), you have to let the object in question implement
     * the {@link Measurable} interface.
     * @return The sum of the sizes of the passed objects (in bytes).
     * If a provided object is not Serializable or another error occurs,
     * Double.NaN will be returned.
     */
    public static double sizeOf(Object... objects) {
        try {
            double size = 0.0;
            for(Object o : objects) {
                if (o instanceof Measurable) {
                    size += ((Measurable) o).getBytes();
                } else {
                    size += Objects.sizeof(o);
                }
            }
            return size;
        } catch(Exception e) {// e.g. in case of a NotSerializableException
            logger.warn(e.getMessage(), e);
            return Double.NaN;
        }
    }
}