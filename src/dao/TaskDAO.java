package dao;

import entity.TaskEntity;
import entity.TeamEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import util.HibernateUtil;


@SuppressWarnings("deprecation")
public class TaskDAO extends GenericDAO<TaskEntity> {

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
