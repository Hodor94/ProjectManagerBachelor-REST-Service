package dao;

import entity.ChatEntity;
import entity.TeamEntity;
import entity.UserEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import util.HibernateUtil;

public class ChatDAO extends GenericDAO<ChatEntity> {

	public ChatEntity getChatByName(TeamEntity team, String chatName) {
		ChatEntity result;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(ChatEntity.class)
				.add(Expression.eq("name", chatName))
				.add(Expression.eq("team", team));
		result = (ChatEntity) criteria.uniqueResult();
		session.getTransaction().commit();
		session.close();
		return result;
	}

}
