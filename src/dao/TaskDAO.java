package dao;

import entity.TaskEntity;
import entity.TeamEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import util.HibernateUtil;

/**
 * This class is a data access object for TasEntity entries in the database.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since version 1.0
 */
@SuppressWarnings("deprecation")
public class TaskDAO extends GenericDAO<TaskEntity> {

	/**
	 * Gets a TaskEntity object with a specific name and belonging to a
	 * specific team out of the database.
	 *
	 * @param taskName The name of the task.
	 * @param team The team the task belongs to.
	 *
	 * @return The TaskEntity object of the team if it exists in the database
	 * and null if it does not.
	 */
    public TaskEntity getTaskByTaskName(String taskName, TeamEntity team) {
        TaskEntity result;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(TaskEntity.class)
                .add(Expression.eq("name", taskName)).add(Expression.eq
                        ("team", team));
        result = (TaskEntity) criteria.uniqueResult();
        session.getTransaction().commit();
        session.close();
        return result;
    }
    
}
