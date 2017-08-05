package dao;

import java.util.ArrayList;
import java.util.Collection;

import entity.StatisticEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import util.HibernateUtil;


@SuppressWarnings("deprecation")
public class StatisticDAO extends GenericDAO<StatisticEntity> {

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
