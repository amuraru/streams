/**
 * 
 */
package stream.util;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 *
 */
public class TrustAllManager 
	implements X509TrustManager, TrustManager 
{
	private static Logger log = LoggerFactory.getLogger( TrustAllManager.class );
	
	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {

		log.debug( "checkClientTrusted: \n");
		
		for( X509Certificate cert : chain ){
			log.info("-------------------------------------------------------");
			log.debug( " SubjectDN = "+cert.getSubjectDN() );
			log.debug( " Issuer = " + cert.getIssuerDN() );
		}		
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		
		log.debug( "checkServerTrusted: \n");
		
		for( X509Certificate cert : chain ){
			log.debug("-------------------------------------------------------");
			log.debug( " SubjectDN = "+cert.getSubjectDN() );
			log.debug( " Issuer = " + cert.getIssuerDN() );
		}		
	}

	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}
}