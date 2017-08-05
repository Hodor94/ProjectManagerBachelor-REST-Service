package dao;

import entity.GenericEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import util.HibernateUtil;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Diese Klasse stellt eine generische Oberklasse für den Zugriff und die Methoden zur Bearbeitung
 * von DatenbankeintrÃ¤gen.
 *
 * @param <T> Der Typ der Entität, welche in der spezifischen Tabelle in der Datenbank
 *            bearbeitet werden soll.
 * @author Raphael Grum
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
	 * Diese Methode holt eine bestimmte Instanz aus der Datenbank.
	 * Der Typ der Instanz ist generisch und wird vom Platzhalter 'T' bestimmt.
	 *
	 * @return T
	 */
	public T get(long id) {
		// Die offene Datenbankverbindung.
		Session session = HibernateUtil.getSessionFactory()
				.getCurrentSession();
		session.beginTransaction();
		T result = session.get(persistentClass, id);
		session.getTransaction().commit();
		return result;
	}

	/**
	 * Diese Methode holt sich alle Instanzen vom generischen Typ 'T' aus der spezifischen
	 * Datenbank und speichert diese in einer Liste.
	 *
	 * @return List<T>
	 */
	public List<T> getAll() {
		Session session = HibernateUtil.getSessionFactory()
				.getCurrentSession();
		session.beginTransaction();
		// Entspricht einer komplexen Anfrage - hier: 'select *'.
		Criteria crit = session.createCriteria(persistentClass);
		List<T> result = crit.list();
		session.getTransaction().commit();
		return result;
	}

	/**
	 * Diese Methode speichert eine Instanz vom Typ 'T' in der spezifischen Tabelle in der Datenbank,
	 * falls fÃ¼r diese noch kein Eintrag exisitert. Falls dieser schon existiert, wird er aktualisiert.
	 *
	 * @param entity
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
	 * Diese Methode soll einen Eintrag aus der spezifischen Tabelle in der Datenbank
	 * fÃ¼r eine existierende Instanz aus der Datenbank lÃ¶schen.
	 *
	 * @param entity Die zu lÃ¶schende Instanz.
	 */
	public void remove(T entity) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		session.remove(entity);
		session.getTransaction().commit();
	}

	/**
	 * Diese Methode soll einen Eintrag aus der spezifischen Tabelle in der Datenbank
	 * fÃ¼r eine existierende Instanz aus der Datenbank lÃ¶schen.
	 *
	 * @param primaryKey Der PrimÃ¤rschlÃ¼ssel der Instanz, fÃ¼r die der Eintrag aus der Datenbank
	 *                   gelÃ¶scht werden soll.
	 */
	public void remove(long primaryKey) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.remove(get(primaryKey));
		session.getTransaction().commit();
	}

}