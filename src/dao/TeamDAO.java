package dao;

import entity.TeamEntity;
import entity.UserEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import util.HibernateUtil;

import java.util.Collection;

@SuppressWarnings("deprecation")
public class TeamDAO extends GenericDAO<TeamEntity> {

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

    public void removeByName(String teamName) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(TeamEntity.class)
				.add(Expression.eq("name", teamName));
        session.delete(criteria.uniqueResult());
        session.getTransaction().commit();
        session.close();
    }

	public void removeUser(UserEntity user) {

	}

}
