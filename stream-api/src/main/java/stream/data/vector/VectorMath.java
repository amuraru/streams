/**
 * 
 */
package stream.data.vector;

import java.io.Serializable;

import stream.data.Data;
import stream.data.DataUtils;

/**
 * @author chris
 *
 */
public class VectorMath {


	public static void add( Data x, Data y ){
		add( x, 1.0d, y );
	}


	public static void add( Data x, double scale, Data y ){

		for( String key : y.keySet() ){
			if( ! DataUtils.isHiddenOrSpecial( key ) ){
				Serializable v = y.get( key );
				if( v instanceof Double ){

					Serializable vx = x.get( key );
					if( vx == null ){
						x.put( key, scale * (Double) v );
					} else {
						Double d = new Double( vx.toString() );
						x.put( key, d + (Double) v );
					}
				}
			}
		}
	}


	public static void scale( Data x, Double factor ){
		for( String key : x.keySet() ){
			if( ! DataUtils.isHiddenOrSpecial( key ) ){
				Serializable v = x.get( key );
				if( v instanceof Double ){
					Double scaled = factor * (Double) v;
					x.put( key, scaled );
				}
			}
		}
	}
}
