package entity;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a chat in  the system.
 *
 * @author Raphael Grum
 * @version 1.0
 */
@Entity
@Table(name = "chat")
public class ChatEntity extends GenericEntity {

	// Attributes related to other entities
	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(cascade = CascadeType.ALL)
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
	 * Creates a new instance of ChatEntity with no information in it.
	 */
	public ChatEntity() {
		super();
		users = new ArrayList<UserEntity>();
		messages = new ArrayList<MessageEntity>();
	}

	/**
	 * Creates a new instace of ChatEntity with all information given.
	 *
	 * @param name - The new name of the chat.
	 * @param users - The new list of users chatting.
	 * @param messages - The messages sent in this chat.
	 * @param team - The team in which the chat exists.
	 * @param creator - The user who created the chat.
	 */
	public ChatEntity(String name, ArrayList<UserEntity> users,
					  ArrayList<MessageEntity> messages, TeamEntity team,
					  UserEntity creator) {
		super();
		this.users = users;
		this.messages = messages;
		this.team = team;
	}

	public List<UserEntity> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<UserEntity> users) {
		this.users = users;
	}

	public List<MessageEntity> getMessages() {
		return messages;
	}

	public void setMessages(ArrayList<MessageEntity> messages) {
		this.messages = messages;
	}

	public void setUsers(List<UserEntity> users) {
		this.users = users;
	}

	public void setMessages(List<MessageEntity> messages) {
		this.messages = messages;
	}

	public TeamEntity getTeam() {
		return team;
	}

	public void setTeam(TeamEntity team) {
		this.team = team;
	}

	public void addUser(UserEntity user) {
		this.users.add(user);
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		String result;
		stringBuilder.append("{");
		stringBuilder.append("\"id\": " + "\"" + this.getId() + "\",");
		stringBuilder.append(appendJSONTeamName(team));
		stringBuilder.append("}");
		result = stringBuilder.toString();
		return result;
	}

	private String appendJSONTeamName(TeamEntity team) {
		if (team != null && team.getName() != null
				&& !(team.getName().equals(""))) {
			return "\"team\": " + "\"" + this.getTeam().getName() + "\", ";
		} else {
			return "\"team\": " + null + ", ";
		}
	}

}

