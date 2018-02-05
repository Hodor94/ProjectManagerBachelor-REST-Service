package dao;

import entity.RegisterEntity;
import entity.TeamEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import util.HibernateUtil;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class is a data access object for a RegisterEntity entry in the
 * database.
 *
 *@author Raphael Grum
 *@version 1.0
 *@since 1.0
 */
@SuppressWarnings("deprecation")
public class RegisterDAO extends GenericDAO<RegisterEntity> {

	/**
	 * Gets a register of a team with a given name out of the database.
	 *
	 * @param registerName The name of the register.
	 * @param teamName The team the register belongs to.
	 *
	 * @return A RegisterEntity object if it is saved in the database and
	 * null if it is not saved in the database.
	 */
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

	/**
	 * Removes a RegsiterEntity entry of the database.
	 *
	 * @param registerName The name of the register which has to be removed.
	 */
	public void removeByName(String registerName) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(RegisterEntity.class)
				.add(Expression.eq("name", registerName));
		session.delete(criteria.uniqueResult());
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * Checks if there is a registry with a given name saved in the database.
	 *
	 * @param registerName The name of the register to be deleted.
	 *
	 * @return Returns true if there is a register with the given name in the
	 * database and false if it is not.
	 */
	public boolean checkIfRegisterExists(String registerName) {
		boolean result = false;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(RegisterEntity.class)
				.add(Expression.eq("name", registerName));
		if (criteria.uniqueResult() != null) {
			result = true;
		}
		session.getTransaction().commit();
		session.close();
		return result;
	}

	/**
	 * Gets all RegisterEntity objects of a team out of the database and
	 * returns them as a {@see Collection}.
	 *
	 * @param team The team the registers have to belong to.
	 *
	 * @return A Collection of all registers of the team.
	 */
	public Collection<RegisterEntity> getRegisters(TeamEntity team) {
		Collection<RegisterEntity> result = new ArrayList<RegisterEntity>();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		result = session.createCriteria(RegisterEntity.class)
				.add(Expression.eq("team", team)).list();
		session.getTransaction().commit();
		session.close();
		return result;
	}
}
