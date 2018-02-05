package entity;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used by the framework Hibernate to work with the database and
 * represents a chat in  the system.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "chat")
public class ChatEntity extends GenericEntity {

	@Column(name = "type")
	public boolean isSoloChat;

	@Column(name = "name")
	public String name;

	// Attributes related to other entities
	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(mappedBy = "chats", targetEntity = UserEntity.class)
	private List<UserEntity> users;

	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "chat", targetEntity = MessageEntity.class,
			cascade = CascadeType.REMOVE)
	private List<MessageEntity> messages;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team")
	private TeamEntity team;

	/**
	 * Creates a new instance of ChatEntity with all attributes set with
	 * default values.
	 */
	public ChatEntity() {
		super();
		users = new ArrayList<UserEntity>();
		messages = new ArrayList<MessageEntity>();
	}

	/**
	 * Creates a new instace of ChatEntity with all attributes set with
	 * parameter values.
	 *
	 * @param users The new list of users chatting.
	 * @param messages The messages sent in this chat.
	 * @param team The team in which the chat exists.
	 */
	public ChatEntity(ArrayList<UserEntity> users,
					  ArrayList<MessageEntity> messages, TeamEntity team) {
		super();
		this.users = users;
		this.messages = messages;
		this.team = team;
	}

	/**
	 * Returns a {@see List} of all members of the chat.
	 *
	 * @return The members of the chat.
	 */
	public List<UserEntity> getUsers() {
		return users;
	}

	/**
	 * Sets the users of the chat.
	 *
	 * @param users The members of the chat.
	 */
	public void setUsers(ArrayList<UserEntity> users) {
		this.users = users;
	}

	/**
	 * Returns a {@see List} of all messages of this chat.
	 *
	 * @return All the chats messages.
	 */
	public List<MessageEntity> getMessages() {
		return messages;
	}

	/**
	 * Sets the chats messages.
	 *
	 * @param messages The messages of this chat.
	 */
	public void setMessages(ArrayList<MessageEntity> messages) {
		this.messages = messages;
	}

	/**
	 * Sets the members of this chat.
	 *
	 * @param users The users messaging in this chat.
	 */
	public void setUsers(List<UserEntity> users) {
		this.users = users;
	}

	/**
	 * Sets the messages written in this chat.
	 *
	 * @param messages The messages of this chat,
	 */
	public void setMessages(List<MessageEntity> messages) {
		this.messages = messages;
	}

	/**
	 * Returns the team this chat belongs to.
	 *
	 * @return The chats team.
	 */
	public TeamEntity getTeam() {
		return team;
	}

	/**
	 * Sets the team of the chat.
	 *
	 * @param team The team the chat belongs to.
	 */
	public void setTeam(TeamEntity team) {
		this.team = team;
	}

	/**
	 * Adds a user to the chat.
	 *
	 * @param user A new user in the chat.
	 */
	public void addUser(UserEntity user) {
		this.users.add(user);
	}

	/**
	 * Transforms this ChatEntity into a JSON format String.
	 *
	 * @return This object as a JSON String.
	 */
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		String result;
		stringBuilder.append("{");
		stringBuilder.append("\"id\": " + "\"" + this.getId() + "\",");
		stringBuilder.append(appendJSONChatName());
		stringBuilder.append(appendJSONIsSoloChat());
		stringBuilder.append(appendJSONTeamName(team));
		stringBuilder.append("}");
		result = stringBuilder.toString();
		return result;
	}

	/*
	Returns the name of the chat as a JSON attribute.
	 */
	private String appendJSONChatName() {
		if (name != null) {
			return "\"name\": \"" + name + "\", ";
		} else {
			return "\"name\": " + null + ", ";
		}
	}

	/*
	Returns if the chat is a 1 to 1 chat or not as a JSON attribute.
	 */
	private String appendJSONIsSoloChat() {
		return "\"type\": \"" + isSoloChat + "\", ";
	}

	/*
	Returns the name of the team this chat belongs to as a JSON attribute.
	 */
	private String appendJSONTeamName(TeamEntity team) {
		if (team != null && team.getName() != null
				&& !(team.getName().equals(""))) {
			return "\"team\": " + "\"" + this.getTeam().getName() + "\"";
		} else {
			return "\"team\": " + null;
		}
	}

	/**
	 * Returns the name of this chat.
	 *
	 * @return The name of this chat.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this chat.
	 *
	 * @param name The name of this chat.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets if this chat is a 1 to 1 chat (true) or a group chat (false).
	 *
	 * @param isSoloChat true for single chat or flase for group chat.
	 */
	public void setIsSoloChat(boolean isSoloChat) {
		this.isSoloChat = isSoloChat;
	}

	/**
	 * Returns if this chat is a 1 to 1 chat or a group chat.
	 *
	 * @return true if this chat is a 1 to 1 chat or flase for a group chat.
	 */
	public boolean getIsSoloChat() {
		return isSoloChat;
	}

}

