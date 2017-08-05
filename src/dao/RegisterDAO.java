package dao;

import entity.GenericEntity;
import entity.RegisterEntity;
import entity.TeamEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import util.HibernateUtil;

import java.util.ArrayList;
import java.util.Collection;


@SuppressWarnings("deprecation")
public class RegisterDAO extends GenericDAO<RegisterEntity> {

	public RegisterEntity getRegisterByName(String registerName, String
			teamName) {
		RegisterEntity result = null;
		TeamDAO teamDAO = new TeamDAO();
		TeamEntity team = teamDAO.getTeamByTeamName(teamName);
		if (team != null) {
			Session session
					= HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			Criteria criteria = session.createCriteria(RegisterEntity.class).add
					(Expression.eq("name", registerName))
					.add(Expression.eq("team", team));
			result = (RegisterEntity) criteria.uniqueResult();
			session.getTransaction().commit();
			session.close();
		}
		return result;
	}

	public void removeByName(String registerName) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(RegisterEntity.class).add(Expression.eq("name", registerName));
		session.delete(criteria.uniqueResult());
		session.getTransaction().commit();
		session.close();
	}

	public boolean checkIfRegisterExists(String registerName) {
		boolean result = false;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(RegisterEntity.class).add
				(Expression.eq("name", registerName));
		if (criteria.uniqueResult() != null) {
			result = true;
		}
		session.getTransaction().commit();
		session.close();
		return true;
	}

	public Collection<RegisterEntity> getRegisters(TeamEntity team) {
		Collection<RegisterEntity> result = new ArrayList<RegisterEntity>();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		result = session.createCriteria(RegisterEntity.class).add(Expression
				.eq("team", team)).list();
		session.getTransaction().commit();
		session.close();
		return result;
	}
}
