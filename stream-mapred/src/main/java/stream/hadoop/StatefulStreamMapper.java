package stream.hadoop;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.GetFuture;
import net.spy.memcached.internal.OperationFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>
 * This class implements a simple mapper that will process a block
 * of data items. In addition to that is provides methods for storing
 * temporal state data in a local memcached instance.
 * </p>
 * 
 * @author Christian Bockermann
 *
 */
public abstract class StatefulStreamMapper
extends AbstractStreamMapper
{
	static Logger log = LoggerFactory.getLogger( StatefulStreamMapper.class );
	Integer expireTime = 3600;
	MemcachedClient memcache;

	public StatefulStreamMapper(){
		if( System.getProperty( "memcache.address" ) != null ){
			try {
				memcache = new MemcachedClient( AddrUtil.getAddresses( System.getProperty( "memcache.address" ) ) );
			} catch (Exception e) {
				e.printStackTrace();
				if( memcache != null ){
					log.info( "Shutting down memcache connection..." );
					memcache.shutdown();
					memcache = null;
				}
			}
		} else
			memcache = null;
	}

	public boolean remember( String key, Serializable object ){

		if( memcache != null ){

			log.debug( "Storing object as {}", key );
			OperationFuture<Boolean> future = null;

			if( memcache.get( key ) == null ){
				log.debug( "using memcache.add(..)" );
				future = memcache.add( key, expireTime, object );
			} else {
				log.debug( "Using memcache.set(..)" );
				future = memcache.set( key, expireTime, object );
			}

			try {
				return future.get( 1, TimeUnit.SECONDS );
			} catch (Exception e) {
				log.error( "Failed to store value for key {}: {}", key, e.getMessage() );
			}
		}

		return false;
	}


	public Serializable recall( String key ){
		if( memcache != null ){
			try {
				GetFuture<Object> future = memcache.asyncGet( key );
				return (Serializable) future.get( 1, TimeUnit.SECONDS );
			} catch (Exception e) {
				log.error( "Cache access timed out: {}", e.getMessage() );
				return null;
			}
		}
		return null;
	}



	public Integer getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Integer expireTime) {
		if( expireTime < 0 ){
			log.warn( "Negative expire-time is not supported! Sticking to {}", this.expireTime );
			return;
		}
		this.expireTime = expireTime;
	}


	public void finish(){
		try {
			if( memcache != null ){
				log.info( "Shutting down memcache {}", memcache );
				memcache.shutdown( 1, TimeUnit.SECONDS );
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}