/**
 * 
 */
package stream.web.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.storage.DataStorage;
import stream.data.storage.model.NoteItem;
import stream.web.NoteRef;

/**
 * @author chris
 *
 */
public class NoteServiceImpl implements NoteService {

	static Logger log = LoggerFactory.getLogger( NoteServiceImpl.class );
	final DataStorage storage;
	
	public NoteServiceImpl(){
		storage = DataStorage.getDataStorage();
	}
	
	
	/**
	 * @see stream.web.services.NoteService#getNote(java.lang.String)
	 */
	@Override
	public String getNote(String key) {
		log.info( "Retrieving note {}", key );
		return getNote( key, new Date() );
	}

	
	/**
	 * @see stream.web.services.NoteService#getNote(java.lang.String, java.util.Date)
	 */
	@Override
	public String getNote(String key, Date date) {
		String txt = null;
		Session s = storage.openSession();
		
		try {
			
			List<?> list = s.createQuery( "from NoteItem n where n.name = ? and n.created <= ? order by n.created desc" )
					.setParameter( 0, key )
					.setParameter( 1, date )
					.setMaxResults( 1 )
					.list();
			
			if( list != null && ! list.isEmpty() ){
				NoteItem note = (NoteItem) list.get( 0 );
				txt = note.getText();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			s.close();
		}
		
		return txt;
	}

	
	/**
	 * @see stream.web.services.NoteService#getChanges(java.lang.String)
	 */
	@Override
	public List<Date> getChanges(String key) {
		List<Date> results = new ArrayList<Date>();
		
		Session s = storage.openSession();
		
		try {
			List<?> list = s.createQuery( "select n.created from NoteItem n where n.name = ? order by n.created desc" )
					.setParameter( 0, key )
					.list();
			
			for( Object o : list ){
				try {
					Date date = (Date) o;
					results.add( date );
				} catch (Exception e){
					e.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			s.close();
		}

		return results;
	}


	/**
	 * @see stream.web.services.NoteService#saveNote(java.lang.String, java.lang.String)
	 */
	@Override
	public void saveNote(String key, String txt) {
		
		Transaction tx = null;
		Session s = storage.openSession();
		try {
			
			NoteItem item = new NoteItem();
			item.setName( key );
			item.setText( txt );
			item.setCreated( new Date() );
			tx = s.beginTransaction();
			s.save( item );
			tx.commit();
			
		} catch (Exception e) {
			if( tx != null )
				tx.rollback();
			
			e.printStackTrace();
		} finally {
			s.close();
		}
	}


	/* (non-Javadoc)
	 * @see stream.web.services.NoteService#search(java.lang.String)
	 */
	@Override
	public Set<NoteRef> search(String query) {
		Set<NoteRef> results = new LinkedHashSet<NoteRef>();
		
		Session s = storage.openSession();
		String search = "%" + query + "%";
		
		try {
			List<?> list = s.createQuery( "from NoteItem n where n.text LIKE ? group by n.name order by n.created desc" )
					.setParameter( 0, search )
					.list();
			
			for( Object o : list ){
				try {
					NoteItem note = (NoteItem) o;
					results.add( new NoteRef( note.getId(), note.getName(), note.getCreated() ) );
				} catch (Exception e){
					e.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			s.close();
		}

		return results;
	}
}
