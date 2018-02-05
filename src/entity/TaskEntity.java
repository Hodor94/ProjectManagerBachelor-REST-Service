package entity;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This class is used by the framework Hibernate to work with the database
 * and represents a task in the system.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "task")
@JsonSerialize
public class TaskEntity extends GenericEntity {

	// Attributes - not related to any other Entity
	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "deadline")
	private String deadline;

	// Attributes which refer to other entities
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "worker")
	private UserEntity worker;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "team")
	private TeamEntity team;

	/**
	 * Creates a TaskEntity object with all the attributes set with the
	 * default values.
	 */
	public TaskEntity() {
		super();
	}

	/**
	 * Creates a TaskEntity object with the attributes set with the parameter
	 * values.
	 *
	 * @param name The name of the task.
	 * @param description The description of the task.
	 * @param deadline The point of time the task has to be fulfilled.
	 * @param worker The user who is working on the task.
	 * @param team The team the task belongs to.
	 */
	public TaskEntity(String name, String description, String deadline,
					  UserEntity worker, TeamEntity team) {
		super();
		this.name = name;
		this.description = description;
		this.deadline = deadline;
		this.worker = worker;
		this.team = team;
	}

	/**
	 * Returns the name of the task.
	 *
	 * @return The name of the task.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the register.
	 *
	 * @param name The new name of the register.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the description of the task.
	 *
	 * @return The description of the task.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of the task.
	 *
	 * @param description The new description of the task.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the point of time this task has to be fulfilled.
	 *
	 * @return The deadline of the task.
	 */
	public String getDeadline() {
		return deadline;
	}

	/**
	 * Sets the deadline of this task.
	 *
	 * @param deadline The new deadline of the task.
	 */
	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	/**
	 * Returns the user who works on this task.
	 *
	 * @return The worker for the task.
	 */
	public UserEntity getWorker() {
		return worker;
	}

	/**
	 * Sets the worker for the task.
	 *
	 * @param worker The user who works on this task.
	 */
	public void setWorker(UserEntity worker) {
		this.worker = worker;
	}

	/**
	 * Returns the team this task belongs to.
	 *
	 * @return The team of the task.
	 */
	public TeamEntity getTeam() {
		return team;
	}

	/**
	 * Sets the team this task belongs to.
	 *
	 * @param team The team of the task.
	 */
	public void setTeam(TeamEntity team) {
		this.team = team;
	}

	/**
	 * Transforms this TaskEntity object into a JSON format String.
	 *
	 * @return This object as a JSON format String.
	 */
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		String result;
		stringBuilder.append("{");
		stringBuilder.append("\"id\": " + "\"" + this.getId() + "\",");
		stringBuilder.append(appendJSONName(name));
		stringBuilder.append(appendJSONDescription(description));
		stringBuilder.append(appendJSONDeadline(deadline));
		stringBuilder.append(appendJSONWorker());
		stringBuilder.append("}");
		result = stringBuilder.toString();
		return result;
	}

	/*
	Returns the name of this task as a JSON attribute.
	 */
	private String appendJSONName(String name) {
		if (name != null && !(name.equals(""))) {
			return "\"name\": " + "\"" + name + "\", ";
		} else {
			return "\"name\": " + null + ", ";
		}
	}

	/*
	Returns the description of this task as a JSON attribute.
	 */
	private String appendJSONDescription(String description) {
		if (description != null && !(description.equals(""))) {
			return "\"description\": " + "\""
					+ description + "\", ";
		} else {
			return "\"description\": " + null + ", ";
		}
	}

	/*
	Returns the deadline of this task as a JSON attribute.
	 */
	private String appendJSONDeadline(String deadline) {
		if (deadline != null && !(deadline.equals(""))) {
			return "\"deadline\": " + "\"" + deadline + "\", ";
		} else {
			return "\"deadline\": " + null + ", ";
		}
	}

	/*
	Returns the username of the worker of this task as a JSON attribute.
	 */
	private String appendJSONWorker() {
		if (this.getWorker() != null) {
			return "\"worker\":  \"" + getWorker().getUsername() + "\"";
		} else {
			return "\"worker\": " + null;
		}
	}

}
