package stream.eval.size;

import java.io.Serializable;

import stream.data.Measurable;
import stream.learner.Learner;

/**
 * <p>By passing an identifier and a learner instance into an instance of this class
 * the sizes of the learner and its model will be determined.</p>
 *
 * @author Benedikt Kulmann, Lukas Kalabis
 * @see SizeMeasurement
 * @see Measurable
 */
public class SizeEvaluationResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String identifier;
    private final double learnerSize;
    private final double modelSize;

    /**
     * <p>Creates a new instance of this result class.</p>
     *
     * @param identifier The string which identifies the field of the node where the provided learner came from
     * @param learner The learner instance to get model and learner size from
     */
    public SizeEvaluationResult(String identifier, Learner<?,?> learner) {
        this.identifier = identifier;
        this.modelSize = SizeMeasurement.sizeOf(learner.getModel());
        this.learnerSize = SizeMeasurement.sizeOf(learner) - modelSize;
    }

    /**
     * Returns a string which should identify the field of the node where the provided learner came from
     *
     * @return a string which should identify the field of the node where the provided learner came from
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Returns the size of the provided learner in bytes.
     *
     * @return the size of the provided learner in bytes.
     */
    public double getLearnerSize() {
        return learnerSize;
    }

    /**
     * Returns the size of the model of the provided learner in bytes.
     *
     * @return the size of the model of the provided learner in bytes.
     */
    public double getModelSize() {
        return modelSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final String lineSeparator = System.getProperty("line.separator");
        return new StringBuilder(lineSeparator)
		.append("SizeEvaluation for ")
                .append(identifier)
                .append(":")
                .append(lineSeparator)
                .append("Learner: ")
                .append(learnerSize)
                .append(" bytes")
                .append(lineSeparator)
                .append("Model:   ")
                .append(modelSize)
                .append(" bytes")
                .toString();
    }
}
