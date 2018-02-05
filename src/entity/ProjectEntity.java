package entity;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.*;
import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This class is used by the framework Hibernate to work with the database
 * and represents a project in the system.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "project")
public class ProjectEntity extends GenericEntity {

	// Attributes without any relation to other entities
	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "deadline")
	private String deadline;

	@Column(name = "numberOfAppointments")
	private int numberOfAppointments;

	// Attributes related to other entities
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "projectManager")
	private UserEntity projectManager;

	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "project", targetEntity = AppointmentEntity.class,
			cascade = CascadeType.ALL)
	private List<AppointmentEntity> appointments;

	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(mappedBy = "projectsTakingPart")
	private List<UserEntity> users;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team")
	private TeamEntity team;

	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "project", targetEntity = StatisticEntity.class,
			cascade = CascadeType.ALL)
	private List<StatisticEntity> statistics;

	/**
	 * Creates a ProjectEntity object with all attributes set with default
	 * values.
	 */
	public ProjectEntity() {
		super();
		appointments = new ArrayList<AppointmentEntity>();
		users = new ArrayList<UserEntity>();
		statistics = new ArrayList<StatisticEntity>();
	}

	/**
	 * Creates a ProjectEntity with the parameter setting the values of the
	 * attributes.
	 *
	 * @param name The name of the project.
	 * @param description The description of the project.
	 * @param deadline The deadline of the project.
	 * @param projectManager The manager of the project.
	 * @param appointments The appointments belonging to the project.
	 * @param users The users taking part in this project.
	 * @param team The team of the project.
	 * @param statistics The participation statistics of the users in the
	 *                      project.
	 */
	public ProjectEntity(String name, String description, String deadline,
						 UserEntity projectManager,
						 ArrayList<AppointmentEntity> appointments,
						 ArrayList<UserEntity> users, TeamEntity team,
						 ArrayList<StatisticEntity> statistics) {
		super();
		this.name = name;
		this.description = description;
		this.deadline = deadline;
		this.projectManager = projectManager;
		this.appointments = appointments;
		this.users = users;
		this.team = team;
		this.statistics = statistics;
	}


	/**
	 * Creates a ProjectEntity object with all the attributes set with
	 * parameter values except the field attributes.
	 *
	 * @param name The name of this project.
	 * @param description The description of this project.
	 * @param deadline The deadline of this project.
	 * @param projectManager The manager of this project.
	 * @param team The team of this project.
	 */
	public ProjectEntity(String name, String description, String deadline,
						 UserEntity projectManager,
						 TeamEntity team) {
		this.name = name;
		this.description = description;
		this.deadline = deadline;
		this.projectManager = projectManager;
		this.team = team;
		this.appointments = new ArrayList<AppointmentEntity>();
		this.users = new ArrayList<UserEntity>();
		this.statistics = new ArrayList<StatisticEntity>();
	}

	/**
	 *  Returns the name of this project.
	 *
	 * @return The name of this project.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this project.
	 *
	 * @param name The new name of this project.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the description of this project.
	 *
	 * @return The description of this project.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of this project.
	 *
	 * @param description The new description of this project.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the deadline of this project.
	 *
	 * @return The deadline of this project.
	 */
	public String getDeadline() {
		return deadline;
	}

	/**
	 * Sets the deadline of this project.
	 *
	 * @param deadline The new deadline of this project.
	 */
	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	/**
	 * Returns the manager of this project.
	 *
	 * @return The manager of this project.
	 */
	public UserEntity getProjectManager() {
		return projectManager;
	}

	/**
	 * Sets the manager of this project.
	 *
	 * @param projectManager The new project manager.
	 */
	public void setProjectManager(UserEntity projectManager) {
		this.projectManager = projectManager;
	}

	/**
	 * Returns a {@see List} of the appointments which  belong to this project.
	 *
	 * @return The appointments of this project.
	 */
	public List<AppointmentEntity> getAppointments() {
		return appointments;
	}

	/**
	 * Sets the appointments belonging to this project.
	 *
	 * @param appointments All appointments belonging to this project.
	 */
	public void setAppointments(ArrayList<AppointmentEntity> appointments) {
		this.appointments = appointments;
	}

	/**
	 * Returns the number of all appointments belonging to this project.
	 *
	 * @return The number of all appointments in this project.
	 */
	public int getNumberOfAppointments() {
		return numberOfAppointments;
	}

	/**
	 * Sets the number of all appointments in this project.
	 *
	 * @param numberOfAppointments The number of all appointments.
	 */
	public void setNumberOfAppointments(int numberOfAppointments) {
		this.numberOfAppointments = numberOfAppointments;
	}

	/**
	 * Returns a {@see List} of all users taking part in this project.
	 *
	 * @return The users of this project.
	 */
	public List<UserEntity> getUsers() {
		return users;
	}

	/**
	 * Sets all users taking part in this project.
	 *
	 * @param users All users in this project.
	 */
	public void setUsers(ArrayList<UserEntity> users) {
		this.users = users;
	}

	/**
	 * Returns the team of this project.
	 *
	 * @return The team of this project.
	 */
	public TeamEntity getTeam() {
		return team;
	}

	/**
	 * Sets the team of this project.
	 *
	 * @param team The new team of this project.
	 */
	public void setTeam(TeamEntity team) {
		this.team = team;
	}

	/**
	 * Returns a {@see List} of the participation statistics of the users in
	 * this project.
	 *
	 * @return All participation statistics of this project.
	 */
	public List<StatisticEntity> getStatistics() {
		return statistics;
	}

	/**
	 * Sets the participation statistics in this project.
	 *
	 * @param statistics The statistics of this project.
	 */
	public void setStatistics(ArrayList<StatisticEntity> statistics) {
		this.statistics = statistics;
	}

	/**
	 * Sets all appointments belonging to this project.
	 *
	 * @param appointments All appointments of this project.
	 */
	public void setAppointments(List<AppointmentEntity> appointments) {
		this.appointments = appointments;
	}

	/**
	 * Sets the users of this project.
	 *
	 * @param users The users of this project.
	 */
	public void setUsers(List<UserEntity> users) {
		this.users = users;
	}

	/**
	 * Sets the statistics of this project.
	 *
	 * @param statistics The statistics of this project.
	 */
	public void setStatistics(List<StatisticEntity> statistics) {
		this.statistics = statistics;
	}

	/**
	 * Increases the attribute 'numberOfAllAppointments' by one.
	 */
	public void increaseNumberOfAppointments() {
		this.numberOfAppointments++;
	}

	/**
	 * Decreases the attribute 'numberOfAllAppointments' by one.
	 */
	public void decreaseNumberOfAppointments() {
		numberOfAppointments--;
	}

	/**
	 * Transforms this ProjectEntity object into a JSON format String.
	 *
	 * @return The JSON format String of this project.
	 */
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		String result;
		stringBuilder.append("{\"id\": " + "\"" + this.getId() + "\", ");
		stringBuilder.append(appendJSONName(name));
		stringBuilder.append(appendJSONDescription(description));
		stringBuilder.append(appendJSONDeadline(deadline));
		stringBuilder.append("\"numberOfAppoinments\": " + "\"" // A number is never null so it
				+ this.numberOfAppointments + "\", ");			// does not need extra method
		stringBuilder.append(appendJSONProjectManagerName(projectManager));
		stringBuilder.append(appendJSONTeamName(team));
		stringBuilder.append("}");
		result = stringBuilder.toString();
		return result;
	}

	/*
	Returns the name of the project as a JSON attribute.
	 */
	private String appendJSONName(String name) {
		if (name != null && !(name.equals(""))) {
			return "\"name\": " + "\"" + name + "\", ";
		} else {
			return "\"name\": " + null + ", ";
		}
	}

	/*
	Returns the description of this porject as a JSON attribute.
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
	Returns the deadline of this project as a JSON attribute.
	 */
	private String appendJSONDeadline(String deadline) {
		if (deadline != null) {
			return "\"deadline\": " + "\"" + deadline + "\", ";
		} else {
			return "\"deadline:\" " + null + ", ";
		}
	}

	/*
	Returns the username of the manager of this project as a JSON attribute.
	 */
	private String appendJSONProjectManagerName(UserEntity projectManager) {
		if (projectManager != null) {
			return "\"manager\": " + projectManager.toSring() + ", ";
		} else {
			return "\"manager\": " + null + ", ";
		}
	}

	/*
	Returns the name of the team of this project.
	 */
	private String appendJSONTeamName(TeamEntity team) {
		if (team != null && team.getName() != null && !(team.getName().equals(""))) {
			return "\"team\": " + "\"" + team.getName() + "\"";
		} else {
			return "\"team\": " + null;
		}
	}

}

