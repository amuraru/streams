package stream.web;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import stream.data.storage.model.User;
import stream.web.services.UserService;
import stream.web.services.UserServiceImpl;


/**
 * <p>
 * This class provides a spring-wrapper for the old ConsoleSecurity manager.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class UserManager implements AuthenticationManager, AuthenticationProvider, UserDetailsService {

	static Logger log = LoggerFactory.getLogger( AuthenticationManager.class );
	final UserService userService;
	
	public UserManager(){
		userService = new UserServiceImpl();
	}
	
	
	/**
	 * @see org.springframework.security.authentication.AuthenticationManager#authenticate(org.springframework.security.core.Authentication)
	 */
	@Override
	public Authentication authenticate(Authentication arg0) throws AuthenticationException {
		log.debug( "Authenticating {}", arg0 );
		try {
			String login = arg0.getName();
			String pass = arg0.getCredentials().toString();
			log.debug( "Authenticating user '{}' with password '{}'", login, pass );
			User user = userService.authenticate( login, pass );
			if( user == null ){
				log.error( "authentication failed, username: {}", arg0.getName() );
				throw new BadCredentialsException( "Authentication failed!" );
			}
			
			List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
			roles.add( new GrantedAuthorityImpl( "ROLE_USER" ) );	
			for( String perm : user.getPermissions() ){
				log.debug( "  Adding role {}", perm );
				roles.add( new GrantedAuthorityImpl( perm ) );
			}
			
			log.debug( "User {} has roles: {}", user.getLogin(), roles );
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken( arg0.getName(), arg0.getCredentials(), roles );
			token.setDetails( user );
			SecurityContextHolder.getContext().setAuthentication( token );
			return token;
			
		} catch (Exception e) {
			log.error( "Failed to authenticate: {}", e.getMessage() );
			if( log.isDebugEnabled() )
				e.printStackTrace();
			throw new AuthenticationServiceException( e.getMessage() );
		}
	}


	/**
	 * @see org.springframework.security.authentication.AuthenticationProvider#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<? extends Object> authentication) {
		
		if( authentication == UsernamePasswordAuthenticationToken.class )
			return true;
		
		log.info( "This implementation does not support authentication class {}", authentication );
		return false;
	}


	/**
	 * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		// TODO Auto-generated method stub
		
		log.info( "loadUserByUsername( {} )", username );
		throw new DataAccessResourceFailureException( "Not yet implemented!" );
	}
}