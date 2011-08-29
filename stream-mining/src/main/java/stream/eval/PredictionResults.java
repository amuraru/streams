/**
 * 
 */
package stream.eval;

import java.util.Map;

import stream.learner.Learner;

/**
 * @author chris
 *
 */
public class PredictionResults<D> implements HtmlResult {

	TestAndTrain<D,Learner<D,?>> evaluation;
	
	public PredictionResults( TestAndTrain<D,Learner<D,?>> eval ){
		evaluation = eval;
	}
	
	
	/**
	 * @see stream.eval.HtmlResult#toHtml()
	 */
	@Override
	public String toHtml() {
		StringBuffer s = new StringBuffer();
		s.append( "<div class=\"section\">" );
		s.append( "<div align=\"center\" class=\"confusionMatrix\">" );
		Map<String,ConfusionMatrix<String>> m = evaluation.getConfusionMatrices();
		s.append( "<table><tr>" );
		for( String learner : m.keySet() ){
			s.append( "<td>" );
			s.append( "<div class=\"confusionMatrix\">" );
			s.append( "<div class=\"title\">" + learner + "</div>" );
			s.append( m.get( learner ).toHtml() );
			s.append( "</div>" );
			s.append( "</td>" );
		}
		s.append( "</tr></table>" );
		s.append( "</div>" );
		s.append( "</div>" );
		return s.toString();
	}
}