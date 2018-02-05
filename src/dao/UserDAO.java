package dao;

import entity.UserEntity;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import util.HibernateUtil;

/**
 * This class is a data access object for a UserEntity entry in the database.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since version 1.0
 */
@SuppressWarnings("deprecation")
public class UserDAO extends GenericDAO<UserEntity> {

	/**
	 * Gets a UserEntity object with a specific username out of the database.
	 *
	 * @param username The username of the user to fetch.
	 *
	 * @return A UserEntity object if it exists in the database and null if
	 * it does not.
	 */
    public UserEntity getUserByUsername(String username) {
        UserEntity result;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
        session.beginTransaction();
        Criteria criteria = session.createCriteria(UserEntity.class)
                .add(Expression.eq("username", username));
        result = (UserEntity) criteria.uniqueResult();
        session.getTransaction().commit();
        } catch (HibernateException exc) {
            result = null;
        } finally {
            session.close();
        }
        return result;
    }

	/**
	 * Checks if a UserEntity exists in the database.
	 *
	 * @param username The username of the user.
	 *
	 * @return A UserEntity object if it exists in the database and null if
	 * it does not.
	 */
	public boolean checkIfUserExists(String username) {
        boolean result = false;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(UserEntity.class).add
				(Expression.eq("username", username));
        if (criteria.uniqueResult() != null) {
        	result = true;
		}
		session.getTransaction().commit();
        session.close();
		return result;
    }

	/**
	 * Removes a UserEntity from the database.
	 *
	 * @param id The identifier of the UserEntity entry.
	 */
	public void removeUser(long id) {
    	Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    	session.beginTransaction();
    	UserEntity toDelete = session.load(UserEntity.class, id);
    	session.remove(toDelete);
    	session.getTransaction().commit();
    	session.close();
	}

}
