package com.rapidminer.stream;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.rapidminer.operator.AbstractIOObject;
import com.rapidminer.operator.Annotations;


/**
 * 
 * This class implements a wrapper to wrap simple Data objects into
 * IOObjects to be passed along within RapidMiner.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
public class DataObject 
	extends AbstractIOObject
	implements stream.data.Data {
	
	/** The unique class ID */
	private static final long serialVersionUID = -358985628975633770L;
	final stream.data.Data data;
	
	
	public DataObject( stream.data.Data data ){
		this.data = data;
	}

	public void clear() {
		data.clear();
	}

	public boolean containsKey(Object arg0) {
		return data.containsKey(arg0);
	}

	public boolean containsValue(Object arg0) {
		return data.containsValue(arg0);
	}

	public Set<java.util.Map.Entry<String, Serializable>> entrySet() {
		return data.entrySet();
	}

	public boolean equals(Object arg0) {
		return data.equals(arg0);
	}

	public Serializable get(Object arg0) {
		return data.get(arg0);
	}

	public int hashCode() {
		return data.hashCode();
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public Set<String> keySet() {
		return data.keySet();
	}

	public Serializable put(String arg0, Serializable arg1) {
		return data.put(arg0, arg1);
	}

	public void putAll(Map<? extends String, ? extends Serializable> arg0) {
		data.putAll(arg0);
	}

	public Serializable remove(Object arg0) {
		return data.remove(arg0);
	}

	public int size() {
		return data.size();
	}

	public Collection<Serializable> values() {
		return data.values();
	}

	@Override
	public Annotations getAnnotations() {
		return new Annotations();
	}
	
	public stream.data.Data getWrappedDataItem(){
		return data;
	}
	
	public String toString(){
		return "IOObject[  " + data + "  ]";
	}
}