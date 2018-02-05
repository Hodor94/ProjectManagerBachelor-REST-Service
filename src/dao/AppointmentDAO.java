package dao;

import entity.AppointmentEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import util.HibernateUtil;

import java.util.Calendar;

/**
 * This class is the data access object for the appointment database entries.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings("deprecation")
public class AppointmentDAO extends GenericDAO<AppointmentEntity>{

	/**
	 * Reads the appointment out of the database with the specific given name
	 * and the specific deadline of the appointment.
	 *
	 * @param calendar The deadline of the appointment.
	 * @param appointmentName The name of the appointment.
	 *
	 * @return Returns the AppointmentEntity if it exists but null if it does
	 * not.
	 */
	public AppointmentEntity getAppointment(Calendar calendar,
											String appointmentName) {
		AppointmentEntity result;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(AppointmentEntity.class)
				.add(Expression.eq("deadline", calendar))
				.add(Expression.eq("name", appointmentName));
		result = (AppointmentEntity) criteria.uniqueResult();
		session.getTransaction().commit();
		session.close();
		return result;

	}
}
