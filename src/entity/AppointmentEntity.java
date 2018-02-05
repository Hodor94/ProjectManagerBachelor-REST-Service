package entity;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.*;

/**
 * This class is used by the framework Hibernate to work with the database and
 * represents an appointment of a project in this system.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "appointment")
public class AppointmentEntity extends GenericEntity {

	// Attributes not related to any entities
	@Column(name = "name")
	private String name;

	@Column(name = "isDeadline")
	private boolean isDeadline;

	@Column(name = "description")
	private String description;

	@Column(name = "deadline")
	private String deadline; // The date of the appointment.

	@Column(name = "userParticipationAnswer")
	private HashMap<String, StatisticParticipationAnswer>
			participationAnswersUser;	// The answers of the users for
									    // participation in this appointment.

	// Attributes related to entities

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project")
	private ProjectEntity project;

	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(mappedBy = "appointmentsTakingPart")
	private List<UserEntity> userTakingPart;

	/**
	 * Creates an instance of AppointmentEntity with all attributes set with
	 * default values.
	 */
	public AppointmentEntity() {
		super();
		userTakingPart = new ArrayList<UserEntity>();
		isDeadline = false;
		participationAnswersUser = new HashMap<>();
	}

	/**
	 * Creates a AppointmentEntity object with all the attributes set with
	 * parameter values.
	 *
	 * @param name           The name of the new appointment.
	 * @param description    The description of the appointment.
	 * @param deadline       The date of the appointment.
	 * @param project        The project this appointment belongs to.
	 * @param userTakingPart A list of all the users who are coming to the
	 *                       appointment.
	 */
	public AppointmentEntity(String name, String description, String deadline,
							 ProjectEntity project,
							 ArrayList<UserEntity> userTakingPart) {
		super();
		this.name = name;
		this.description = description;
		this.deadline = deadline;
		this.project = project;
		this.userTakingPart = userTakingPart;
		participationAnswersUser = new HashMap<>();
		isDeadline = false;
	}

	/**
	 * Returns the name of an appointment.
	 *
	 * @return The name of an appointment.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of an appointment.
	 *
	 * @param name The new name of an appointment.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the description of an appointment.
	 *
	 * @return The description of an appointment.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of an appointment.
	 *
	 * @param description The new description of an appointment.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the date on which this appointment is set.
	 *
	 * @return The date of an appointment.
	 */
	public String getDeadline() {
		return deadline;
	}

	/**
	 * Sets the date on which this appointment will be set.
	 *
	 * @param deadline The deadline of an appointment.
	 */
	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	/**
	 * Returns the project to which this appointment belongs to.
	 *
	 * @return The project to which this appointment belongs.
	 */
	public ProjectEntity getProject() {
		return project;
	}

	/**
	 * Sets the project to which this appointment belongs.
	 *
	 * @param project The new project of the appointment.
	 */
	public void setProject(ProjectEntity project) {
		this.project = project;
	}

	/**
	 * Returns a {@see List} of users who are taking part in this appointment.
	 *
	 * @return The list of users taking part in this appointment.
	 */
	public List<UserEntity> getUserTakinPart() {
		return userTakingPart;
	}

	/**
	 * Sets a {@see List} of users taking part in this appointment.
	 *
	 * @param userTakinPart The new list of users taking part in this
	 *                      appointment.
	 */
	public void setUserTakinPart(ArrayList<UserEntity> userTakinPart) {
		this.userTakingPart = userTakinPart;
	}

	/**
	 * Sets the deadline of this appointment.
	 *
	 * @param isDeadline The point of time the appointment will take place.
	 */
	public void setIsDeadline(boolean isDeadline) {
		this.isDeadline = isDeadline;
	}

	/**
	 * Returns the deadline of this appointment.
	 *
	 * @return The point of time this appointment is taking place.
	 */
	public boolean getIsDeadline() {
		return isDeadline;
	}

	/**
	 * Transforms this AppointmentEntity object into a JSON format String.
	 *
	 * @return This appointment as a JSON format String.
	 */
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		String result;
		stringBuilder.append("{");
		stringBuilder.append("\"id\": " + "\"" + this.getId() + "\",");
		stringBuilder.append(appendJSONName(name));
		stringBuilder.append(appendJSONDescription(description));
		stringBuilder.append(appendJSONDeadline(deadline));
		stringBuilder.append(appendJSONIDeadline(isDeadline));
		stringBuilder.append("}");
		result = stringBuilder.toString();
		return result;
	}

	/*
	Returns the deadline of this appointment as a JSON attribute.
	 */
	private String appendJSONIDeadline(boolean isDeadline) {
		return "\"isDeadline\": \"" + isDeadline + "\"";
	}

	/*
	Returns the name of this appointment as a JSON attribute.
	 */
	private String appendJSONName(String name) {
		if (name != null && !(name.equals(""))) {
			return "\"name\": " + "\"" + name + "\",";
		} else {
			return "\"name\": " + null + ", ";
		}
	}

	/*
	Returns the description of this appointment as a JSON attribute.
	 */
	private String appendJSONDescription(String description) {
		if (description != null && !(description.equals(""))) {
			return "\"description\": " + "\"" + description +
					"\", ";
		} else {
			return "\"description\": " + null + ", ";
		}
	}

	/*
	Returns the deadline of this appointment as a JSON attribute.
	 */
	private String appendJSONDeadline(String deadline) {
		if (deadline != null && !(deadline.equals(""))) {
			return "\"deadline\": " + "\"" + deadline + "\", ";
		} else {
			return "\"deadline\": " + null + ", ";
		}
	}

	/**
	 * Sets the participation answer of a specific user for this appointment.
	 *
	 * @param username The username of the user.
	 * @param answer The answer given by the user.
	 */
	public void addUserAnswer(String username,
							  StatisticParticipationAnswer answer) {
		participationAnswersUser.put(username, answer);
	}

	/**
	 * Removes a user and his/her answer from the attribute
	 * 'participationAnswerUser'.
	 *
	 * @param username The username of the user who should get removed.
	 */
	public void removeUserFromUserAnswer(String username) {
		participationAnswersUser.remove(username);
	}

	/**
	 * Returns the users and their asnwers for participation in this
	 * appointment.
	 *
	 * @return The users and their answers for participation in this
	 * appointment.
	 */
	public HashMap<String, StatisticParticipationAnswer> getUserAnswers() {
		return participationAnswersUser;
	}

}

