package stream.learner.tree;

import java.util.Map;

import stream.learner.Regressor;

public interface LinearRegression<D> 
	extends Regressor<D>
{
	public void setParameters(Map<String, Object> parameters);

}
