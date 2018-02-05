package dao;

import entity.ChatEntity;
import entity.TeamEntity;
import entity.UserEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import util.HibernateUtil;

/**
 * This class is a data access object for chats in the database.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings({"deprecation"})
public class ChatDAO extends GenericDAO<ChatEntity> {

	/**
	 * Reads a chat with a given name and team out of the database.
	 *
	 * @param team The team the chat belongs to.
	 * @param chatName The name the chat has.
	 *
	 * @return Returns null, if the chat is not saved in the database and a
	 * ChatEntity object if it is saved in the database.
	 */
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
