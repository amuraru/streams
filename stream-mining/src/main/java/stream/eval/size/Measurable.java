package stream.eval.size;

import java.io.NotSerializableException;
import java.io.Serializable;

import stream.learner.Learner;
import stream.model.Model;

/**
 * <p>
 * If a class implements this interface, {@link #getBytes()} (instead of a standard size measurement
 * routine) will be used if an object of that class is provided to
 * {@link SizeMeasurement#sizeOf(java.lang.Object[])}.
 * </p>
 *
 * <p>
 * Context:
 * For evaluation there exists the option to evaluate memory usage of both {@link Learner} and {@link Model}.<br />
 * This is done by calling {@link SizeMeasurement#sizeOf(java.lang.Object[])} for that {@link Learner} or {@link Model}.<br />
 * However, if an object contained by a {@link Learner} or {@link Model} is not {@link Serializable}
 * {@link SizeMeasurement#sizeOf(java.lang.Object[])} would fail by throwing a {@link NotSerializableException}.<br />
 * In this case you can let your {@link Learner} or {@link Model} implement this {@link Measurable} interface
 * which has the effect that {@link SizeMeasurement#sizeOf(java.lang.Object[])} will use {@link Measurable#getBytes()}
 * as object size measurement instead of using {@link Objects#sizeof(java.lang.Object)}.
 * </p>
 *
 * @author Benedikt Kulmann, Lukas Kalabis
 * @see SizeMeasurement
 */
public interface Measurable {

    /**
     * Returns the size of this object in bytes as double.<br />
     * In case of an error Double.NaN will be returned.
     *
     * @return Number of bytes if successful, Double.NaN if an error occurs
     */
    double getBytes();

}
