package dao;

import java.util.ArrayList;
import java.util.Collection;

import entity.StatisticEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import util.HibernateUtil;

/**
 * This class is a data access object for the StatisticEntity entries in the
 * database.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since version 1.0
 */
@SuppressWarnings("deprecation")
public class StatisticDAO extends GenericDAO<StatisticEntity> {

	/**
	 * Gets the StatisticEntity object belonging to a specific user and
	 * project out of the database.
	 *
	 * @param username The username of the user the participation statistic
	 *                    belongs to.
	 * @param projectname The name of the project the statistic belongs to.
	 *
	 * @return Returns the StatisticEntity object if it exist in the database
	 * and null if it does not.
	 */
    public StatisticEntity getStatisticOfUser(String username, String projectname) {
        StatisticEntity result;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(StatisticEntity.class).add(Expression.eq("project", projectname))
                .add(Expression.eq("user", username));
        result = (StatisticEntity) criteria.uniqueResult();
        session.getTransaction().commit();
        session.close();
        return result;
    }

	/**
	 * Gets all StatisticEntity entries of a specific project as a {@see
	 * Collection} out of the database.
	 *
	 * @param projectname The name of the project the statistics belong to.
	 *
	 * @return All statistics of a project or a empty Collection if there are
	 * no statistics saved in the database.
	 */
	public Collection<StatisticEntity> getStatisticsOfProject(String projectname) {
        Collection<StatisticEntity> result;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(StatisticEntity.class)
                            .add(Expression.eqOrIsNull("project", projectname));
        result = (ArrayList<StatisticEntity>) criteria.list();
        session.getTransaction().commit();
        session.close();
        return result;
    }
    
}
