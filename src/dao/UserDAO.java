package dao;

import java.util.ArrayList;
import java.util.Collection;

import entity.UserEntity;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import util.HibernateUtil;

@SuppressWarnings("deprecation")
public class UserDAO extends GenericDAO<UserEntity> {

    public UserEntity getUserByUsername(String username) {
        UserEntity result;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
        session.beginTransaction();
        Criteria criteria = session.createCriteria(UserEntity.class)
                .add(Expression.eq("username", username));
        result = (UserEntity) criteria.uniqueResult();
        session.getTransaction().commit();
        } catch (HibernateException exc) {
            result = null;
        } finally {
            session.close();
        }
        return result;
    }

    public void removeUserFromTeam(UserEntity user) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.getTransaction().commit();
        session.close();
    }

    public boolean checkIfUserExists(String username) {
        boolean result = false;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(UserEntity.class).add
				(Expression.eq("username", username));
        if (criteria.uniqueResult() != null) {
        	result = true;
		}
		session.getTransaction().commit();
        session.close();
		return result;
    }

}
