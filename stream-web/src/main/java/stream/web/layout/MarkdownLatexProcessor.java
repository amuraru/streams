/**
 * 
 */
package stream.web.layout;

/**
 * @author chris
 *
 */
public abstract class MarkdownLatexProcessor extends MarkdownPreprocessor {

	/**
	 * @param startTag
	 * @param endTag
	 */
	public MarkdownLatexProcessor(String startTag, String endTag) {
		super(startTag, endTag);
	}
	
}
