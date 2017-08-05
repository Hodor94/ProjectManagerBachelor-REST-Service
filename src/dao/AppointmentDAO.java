package dao;

import entity.AppointmentEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import util.HibernateUtil;

import java.util.Calendar;

@SuppressWarnings("deprecation")
public class AppointmentDAO extends GenericDAO<AppointmentEntity>{

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
