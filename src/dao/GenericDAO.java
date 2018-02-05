package dao;

import entity.GenericEntity;
import entity.UserEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import util.HibernateUtil;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * This class enables the generic interaction of the system with the database.
 *
 * @param <T> The type of the entity with which should be worked with.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings({"deprecation"})
public class GenericDAO<T extends GenericEntity> {

	private Class<T> persistentClass;

	public GenericDAO() {
		// Specifies the class with which the DAO works.
		this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	/**
	 * Reads the instance with the type T and the identifier id out of the
	 * database and returns it.
	 *
	 * @param id The identifier of the instance.
	 * @return If there is a entry with the right identifier it will be
	 * returned. If not, null will be returned.
	 */
	public T get(long id) {
		Session session = HibernateUtil.getSessionFactory()
				.getCurrentSession();
		session.beginTransaction();
		T result = session.get(persistentClass, id);
		session.getTransaction().commit();
		session.close();
		return result;
	}

	/**
	 * Gets all instances of the type T out of the database and saves it
	 * within a List and returns it.
	 *
	 * @return A List full of all in the database saved instances of the type T.
	 */
	public List<T> getAll() {
		Session session = HibernateUtil.getSessionFactory()
				.getCurrentSession();
		session.beginTransaction();
		Criteria crit = session.createCriteria(persistentClass);
		List<T> result = crit.list();
		session.getTransaction().commit();
		session.close();
		return result;
	}

	/**
	 * Searches for a given entity. If it is already existing in the
	 * database, it will be updated, else there will be saved a new entry.
	 *
	 * @param entity The entity which should be updated or saved.
	 */
	public void saveOrUpdate(T entity) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		if (entity.getId() != 0) {
		session.merge(entity);
		} else {
			session.saveOrUpdate(entity);
		}
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * Searches for a specific entry and deletes it if possible.
	 *
	 * @param entity The entity which will be deleted.
	 */
	public void remove(T entity) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		session.remove(entity);
		session.getTransaction().commit();
		session.close();
	}

}