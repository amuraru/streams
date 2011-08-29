package stream.quantiles;

import stream.learner.Learner;
import stream.model.SelectiveDescriptionModel;



public interface QuantileLearner extends Learner<Double, SelectiveDescriptionModel<Double, Double>> {

}
