package entity;

/**
 * Created by Raphael on 14.06.2017.
 */

import com.google.gson.Gson;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.json.JSONObject;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "team")
public class TeamEntity extends GenericEntity {

	// Attributes without relations to other entities
	@ColumnTransformer(read = "AES_DECRYPT(name, 'DataService.secretKey')",
					   write = "AES_ENCRYPT(?, 'DataService.secretKey')")
	@Column(name = "name")
	private String name;

	@ColumnTransformer(read = "AES_DECRYPT(description, 'DataService.secretKey')",
					write = "AES_ENCRYPT(?, 'DataService.secretKey')")
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
	 *
	 */
	public TeamEntity() {
		super();
		users = new ArrayList<UserEntity>();
		projects = new ArrayList<ProjectEntity>();
		tasks = new ArrayList<TaskEntity>();
		registers = new ArrayList<RegisterEntity>();
		chats = new ArrayList<ChatEntity>();
	}

	/**
	 * @param name
	 * @param description
	 * @param admin
	 * @param users
	 * @param projects
	 * @param tasks
	 * @param registers
	 * @param chats
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
	}

	/**
	 * @param name
	 * @param description
	 * @param admin
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
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UserEntity getAdmin() {
		return admin;
	}

	public void setAdmin(UserEntity admin) {
		this.admin = admin;
	}

	public List<UserEntity> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<UserEntity> users) {
		this.users = users;
	}

	public List<ProjectEntity> getProjects() {
		return projects;
	}

	public void setProjects(ArrayList<ProjectEntity> projects) {
		this.projects = projects;
	}

	public List<TaskEntity> getTasks() {
		return tasks;
	}

	public void setTasks(ArrayList<TaskEntity> tasks) {
		this.tasks = tasks;
	}

	public List<RegisterEntity> getRegisters() {
		return registers;
	}

	public void setRegisters(ArrayList<RegisterEntity> registers) {
		this.registers = registers;
	}

	public Collection<ChatEntity> getChats() {
		return chats;
	}

	public void setChats(ArrayList<ChatEntity> chats) {
		this.chats = chats;
	}

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

	private String appendJSONName(String name) {
		if (name != null && !(name.equals(""))) {
			return "\"name\": " + "\"" + this.getName() + "\", ";
		} else {
			return "\"name\": " + null + ", ";
		}
	}

	private String appendJSONDescription(String description) {
		if (description != null && !(description.equals(""))) {
			return "\"description\": " + "\"" + description + "\", ";
		} else {
			return "\"description\": " + null + ", ";
		}
	}

	private String appendJSONAdmin(UserEntity admin) {
		if (admin != null) {
			return "\"admin\": " + this.admin.toSring();
		} else {
			return "\"admin\": " + null;
		}
	}

}
