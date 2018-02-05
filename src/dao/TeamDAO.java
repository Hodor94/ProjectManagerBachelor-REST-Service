package dao;

import entity.TeamEntity;
import entity.UserEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import util.HibernateUtil;

import java.util.Collection;

/**
 * This class is a data access object for TeamEntity entry in the database.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since version 1.0
 */
@SuppressWarnings("deprecation")
public class TeamDAO extends GenericDAO<TeamEntity> {

	/**
	 * Gets a TeamEntity object with a specific name out of the database.
	 *
	 * @param name The name of the team to fetch.
	 *
	 * @return A TeamEntity object if it exists in the database and null if
	 * it does not.
	 */
    public TeamEntity getTeamByTeamName(String name) {
        TeamEntity result;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(TeamEntity.class)
				.add(Expression.eq("name", name));
        result = (TeamEntity) criteria.uniqueResult();
        session.getTransaction().commit();
        session.close();
        return result;
    }
}
