/**
 * 
 */
package stream.data.plotter;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import stream.io.DataStreamListener;

/**
 * @author chris
 *
 */
public abstract class AbstractPlotter implements Plotter, DataStreamListener {

	String name = "";
	String title = "";
	String domainTitle = "";
	String rangeTitle = "";
	String description = "";
	Integer updateInterval = 1000;
	Integer history = Integer.MAX_VALUE;
	Dimension size = new Dimension( 1000, 400 );
	Map<String,Object> ctx = new HashMap<String,Object>();
	
	public AbstractPlotter( String name ){
		this.name = name;
	}

	public String getName(){
		return name;
	}
	
	public void setName( String name ){
		this.name = name;
	}
	
	
	/**
	 * @see experiments.report.Plotter#createHtmlEmbedding()
	 */
	@Override
	public abstract String createHtmlEmbedding();
	

	/**
	 * @see experiments.report.Plotter#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	
	/**
	 * @see experiments.report.Plotter#getTitle()
	 */
	@Override
	public String getTitle() {
		return title;
	}

	
	/**
	 * @see experiments.report.Plotter#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(String description) {
		this.description = description;
	}


	/**
	 * @see experiments.report.Plotter#setSize(java.awt.Dimension)
	 */
	@Override
	public void setSize(Dimension d) {
		this.size = d;
	}

	
	public Dimension getSize(){
		return size;
	}
	
	
	/**
	 * @see experiments.report.Plotter#setTitle(java.lang.String)
	 */
	@Override
	public void setTitle(String title) {
		this.title = title;
	}


	/**
	 * @return the updateInterval
	 */
	public Integer getUpdateInterval() {
		return updateInterval;
	}


	/**
	 * @param updateInterval the updateInterval to set
	 */
	public void setUpdateInterval(Integer updateInterval) {
		this.updateInterval = updateInterval;
	}


	/**
	 * @return the history
	 */
	public Integer getHistory() {
		return history;
	}


	/**
	 * @param history the history to set
	 */
	public void setHistory(Integer history) {
		this.history = history;
	}
	
	
	/**
	 * @return the domainTitle
	 */
	public String getDomainTitle() {
		return domainTitle;
	}


	/**
	 * @param domainTitle the domainTitle to set
	 */
	public void setDomainTitle(String domainTitle) {
		this.domainTitle = domainTitle;
	}


	/**
	 * @return the rangeTitle
	 */
	public String getRangeTitle() {
		return rangeTitle;
	}


	/**
	 * @param rangeTitle the rangeTitle to set
	 */
	public void setRangeTitle(String rangeTitle) {
		this.rangeTitle = rangeTitle;
	}


	public Object get( String key, Object init ){
		if( ctx.containsKey( key ) ){
			return ctx.get( key );
		} else {
			ctx.put( key, init );
			return init;
		}
	}
}