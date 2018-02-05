package entity;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class is used by the framework Hibernate to work with the database
 * and represents a team in the system.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "team")
public class TeamEntity extends GenericEntity {

	// Normal attributes
	@ElementCollection
	@Column(name = "requests")
	private List<String> requestsOfUsers;

	@ElementCollection
	@Column(name = "news")
	private List<String> news;

	@Column(name = "updated")
	private boolean updated;

	// Attributes without relations to other entities
	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	// Attributes which relate to other entites
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "admin")
	private UserEntity admin;

	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "team", targetEntity = UserEntity.class, cascade =
			CascadeType.ALL)
	private List<UserEntity> users;

	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "team", targetEntity = ProjectEntity.class,
			cascade = CascadeType.REMOVE)
	private List<ProjectEntity> projects;

	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "team", targetEntity = TaskEntity.class,
			cascade = CascadeType.REMOVE)
	private List<TaskEntity> tasks;

	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "team", targetEntity = RegisterEntity.class,
			cascade = CascadeType.REMOVE)
	private List<RegisterEntity> registers;

	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "team", targetEntity = ChatEntity.class,
			cascade = CascadeType.REMOVE)
	private List<ChatEntity> chats;

	/**
	 * Creates a TeamEntity object with all attributes set with the default
	 * values.
	 */
	public TeamEntity() {
		super();
		users = new ArrayList<UserEntity>();
		projects = new ArrayList<ProjectEntity>();
		tasks = new ArrayList<TaskEntity>();
		registers = new ArrayList<RegisterEntity>();
		chats = new ArrayList<ChatEntity>();
		requestsOfUsers = new ArrayList<>();
		updated = false;
	}

	/**
	 * Creates a TeamEntity object with all the attributes set with parameter
	 * values.
	 *
	 * @param name The name of the team.
	 * @param description The description of this team.
	 * @param admin The administrator of this team.
	 * @param users The users taking part in this team.
	 * @param projects The projects in this team.
	 * @param tasks The tasks in this team.
	 * @param registers The registers in this team.
	 * @param chats The chats in this team.
	 */
	public TeamEntity(String name, String description, UserEntity admin,
					  ArrayList<UserEntity> users,
					  ArrayList<ProjectEntity> projects,
					  ArrayList<TaskEntity> tasks,
					  ArrayList<RegisterEntity> registers,
					  ArrayList<ChatEntity> chats) {
		super();
		this.name = name;
		this.description = description;
		this.admin = admin;
		this.users = users;
		this.projects = projects;
		this.tasks = tasks;
		this.registers = registers;
		this.chats = chats;
		requestsOfUsers = new ArrayList<>();
		updated = false;
	}

	/**
	 * Creates a TeamEntity object with name, description and admin set with
	 * parameter values. Everything else has default values.
	 *
	 * @param name The name of the team.
	 * @param description The description of the team.
	 * @param admin The administrator of this team.
	 */
	public TeamEntity(String name, String description, UserEntity admin) {
		super();
		this.name = name;
		this.description = description;
		this.admin = admin;
		this.users = new ArrayList<UserEntity>();
		this.projects = new ArrayList<ProjectEntity>();
		this.tasks = new ArrayList<TaskEntity>();
		this.registers = new ArrayList<RegisterEntity>();
		this.chats = new ArrayList<ChatEntity>();
		requestsOfUsers = new ArrayList<>();
		updated = false;
	}

	/**
	 * Returns the name of the team.
	 *
	 * @return The name of the team.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the team.
	 *
	 * @param name The name of this team.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the description of this team.
	 *
	 * @return The description of this team.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of this team.
	 *
	 * @param description The description of this team.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the administrator of this team.
	 *
	 * @return The user who manages the team.
	 */
	public UserEntity getAdmin() {
		return admin;
	}

	/**
	 * Sets the administrator of this team.
	 *
	 * @param admin The user who manages this team.
	 */
	public void setAdmin(UserEntity admin) {
		this.admin = admin;
	}

	/**
	 * Returns a {@see List} of the users belonging to this team.
	 *
	 * @return The members of this team.
	 */
	public List<UserEntity> getUsers() {
		return users;
	}

	/**
	 * Sets the users who are in this team.
	 *
	 * @param users The team members.
	 */
	public void setUsers(ArrayList<UserEntity> users) {
		this.users = users;
	}

	/**
	 * Returns a {@see List} of the projects in this team.
	 *
	 * @return The projects in this team.
	 */
	public List<ProjectEntity> getProjects() {
		return projects;
	}

	/**
	 * Sets the projects of this team.
	 *
	 * @param projects The projects of this team.
	 */
	public void setProjects(ArrayList<ProjectEntity> projects) {
		this.projects = projects;
	}

	/**
	 * Returns a {@List} of tasks of this team.
	 *
	 * @return All tasks in this team.
	 */
	public List<TaskEntity> getTasks() {
		return tasks;
	}

	/**
	 * Sets the tasks of this team.
	 *
	 * @param tasks The tasks of this team.
	 */
	public void setTasks(List<TaskEntity> tasks) {
		this.tasks = tasks;
	}

	/**
	 * Returns a {@see List} of the registers in this team.
	 *
	 * @return All registers of this team.
	 */
	public List<RegisterEntity> getRegisters() {
		return registers;
	}

	/**
	 * Sets the registers of this team.
	 *
	 * @param registers The registers of this team.
	 */
	public void setRegisters(ArrayList<RegisterEntity> registers) {
		this.registers = registers;
	}

	/**
	 * Returns a {@see Collection} of all chats in this team.
	 *
	 * @return All chats in this team.
	 */
	public Collection<ChatEntity> getChats() {
		return chats;
	}

	/**
	 * Sets thw chats in this team.
	 *
	 * @param chats The chats of this team.
	 */
	public void setChats(ArrayList<ChatEntity> chats) {
		this.chats = chats;
	}

	/**
	 * Transforms this TeamEntity object into a JSON format String.
	 *
	 * @return This object as a JSON String.
	 */
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		String result;
		stringBuilder.append("{");
		stringBuilder.append("\"id\": " + "\"" + this.getId() + "\",");
		stringBuilder.append(appendJSONName(name));
		stringBuilder.append(appendJSONDescription(description));
		stringBuilder.append(appendJSONAdmin(admin));
		stringBuilder.append("}");
		result = stringBuilder.toString();
		return result;
	}

	/*
	Returns the name of this team as a JSON attribute.
	 */
	private String appendJSONName(String name) {
		if (name != null && !(name.equals(""))) {
			return "\"name\": " + "\"" + name + "\", ";
		} else {
			return "\"name\": " + null + ", ";
		}
	}

	/*
	Returns the description of this team as a JSON attribute.
	 */
	private String appendJSONDescription(String description) {
		if (description != null && !(description.equals(""))) {
			return "\"description\": " + "\"" + description + "\", ";
		} else {
			return "\"description\": " + null + ", ";
		}
	}

	/*
	Returns the username of the administrator of this team.
	 */
	private String appendJSONAdmin(UserEntity admin) {
		if (admin != null) {
			return "\"admin\": " + admin.getUsername();
		} else {
			return "\"admin\": " + null;
		}
	}

	/**
	 * Adds a request of a user to join this team.
	 *
	 * @param username The username of the user who wants to join this team.
	 */
	public void addRequestOfUser(String username) {
		if (requestsOfUsers == null) {
			requestsOfUsers = new ArrayList<>();
			requestsOfUsers.add(username);
			updated = true;
		} else {
			if (!requestsOfUsers.contains(username)) {
				requestsOfUsers.add(username);
				updated = true;
			}
		}
	}

	/**
	 * Removes a request of a user to join this team.
	 *
	 * @param username The username of the user to remove.
	 *
	 * @return A boolean value. Returns true, if the operation was a success
	 * and false if it was a failure.
	 */
	public boolean removeRequestOfUser(String username) {
		boolean result = false;
		if (requestsOfUsers != null) {
			requestsOfUsers.remove(username);
		}
		if (!requestsOfUsers.contains(username)) {
			result = true;
			updated = true;
		}
		return result;
	}

	/**
	 * Returns a {@see List} of the request of the user who want to join this
	 * team.
	 *
	 * @return The users requests to join this team.
	 */
	public List<String> getRequestsOfUsers() {
		return requestsOfUsers;
	}

	/**
	 * Sets the requests of users who want to join this team.
	 *
	 * @param requestsOfUsers The requests.
	 */
	public void setRequestsOfUsers(List<String> requestsOfUsers) {
		this.requestsOfUsers = requestsOfUsers;
	}

	/**
	 * Checks if the data in the team and the related classes have changed
	 * since the last time a user has requested it.
	 *
	 * @return Returns true if the user has to request new data and false if
	 * there was no change since the last request.
	 */
	public boolean isUpdated() {
		return updated;
	}

	/**
	 * Returns the changes in the system since the last request.
	 *
	 * @return Changes made in the system.
	 */
	public List<String> getNews() {
		return news;
	}

	/**
	 * Sets the changes which were made in the system since the last request.
	 *
	 * @param news The changes made.
	 */
	public void setNews(ArrayList<String> news) {
		this.news = news;
	}

	/**
	 * Adds a change to the already existing changes.
	 *
	 * @param news A new change made.
	 */
	public void addNews(String news) {
		this.news.add(news);
	}

	/**
	 * Clears the saved news.
	 */
	public void clearNews() {
		this.news = new ArrayList<>();
	}
}
