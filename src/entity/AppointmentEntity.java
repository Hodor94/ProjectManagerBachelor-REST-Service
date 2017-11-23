package entity;

import com.google.gson.Gson;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.json.JSONObject;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class represents an appointment of a project.
 *
 * @author Raphael Grum
 * @version 1.0
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
			participationAnswersUser;

	// Attributes related to entities

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project")
	private ProjectEntity project;

	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(mappedBy = "appointmentsTakingPart")
	private List<UserEntity> userTakingPart;

	/**
	 * Creates an instance of AppointmentEntity with no information saved in it.
	 */
	public AppointmentEntity() {
		super();
		userTakingPart = new ArrayList<UserEntity>();
		isDeadline = false;
		participationAnswersUser = new HashMap<>();
	}

	/**
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
	 * @return name - The name of an appointment.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of an appointment.
	 *
	 * @param name - The new name of an appointment.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the description of an appointment.
	 *
	 * @return description - The description of an appointment.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of an appointment.
	 *
	 * @param description - The new description of an appointment.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the date on which this appointment is set.
	 *
	 * @return deadline - The date of an appointment.
	 */
	public String getDeadline() {
		return deadline;
	}

	/**
	 * Sets the date on which this appointment will be set.
	 *
	 * @param deadline - The deadline of an appointment.
	 */
	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	/**
	 * Returns the project to which this appointment belongs to.
	 *
	 * @return project - The project to which this appointment belongs.
	 */
	public ProjectEntity getProject() {
		return project;
	}

	/**
	 * Sets the project to which this appointment belongs.
	 *
	 * @param project - The new project of the appointment.
	 */
	public void setProject(ProjectEntity project) {
		this.project = project;
	}

	/**
	 * Returns the list of users who are taking part in this appointment.
	 *
	 * @return userTakingPart - The list of users taking part in this
	 * appointment.
	 */
	public List<UserEntity> getUserTakinPart() {
		return userTakingPart;
	}

	/**
	 * Sets the list of users taking part in this appointment.
	 *
	 * @param userTakinPart - The new list of users taking part in this
	 *                      appointment.
	 */
	public void setUserTakinPart(ArrayList<UserEntity> userTakinPart) {
		this.userTakingPart = userTakinPart;
	}

	public void setIsDeadline(boolean isDeadline) {
		this.isDeadline = isDeadline;
	}

	public boolean getIsDeadline() {
		return isDeadline;
	}

	/**
	 * Formats all the information contained by an instance of
	 * AppointmentEntity except the project and the list of users taking part
	 * in this appointment to a String version in JSON format.
	 *
	 * @return String - The JSON format version of the primitive information
	 * not including users taking part in this appointment and the project of
	 * the appointment.
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

	private String appendJSONIDeadline(boolean isDeadline) {
		return "\"isDeadline\": \"" + isDeadline + "\"";
	}

	private String appendJSONName(String name) {
		if (name != null && !(name.equals(""))) {
			return "\"name\": " + "\"" + encodeToUTF8(name) + "\",";
		} else {
			return "\"name\": " + null + ", ";
		}
	}

	private String appendJSONDescription(String description) {
		if (description != null && !(description.equals(""))) {
			return "\"description\": " + "\"" + encodeToUTF8(description) +
					"\", ";
		} else {
			return "\"description\": " + null + ", ";
		}
	}

	private String appendJSONDeadline(String deadline) {
		if (deadline != null && !(deadline.equals(""))) {
			return "\"deadline\": " + "\"" + encodeToUTF8(deadline) + "\", ";
		} else {
			return "\"deadline\": " + null + ", ";
		}
	}

	public void addUserAnswer(String username,
							  StatisticParticipationAnswer answer) {
		participationAnswersUser.put(username, answer);
	}

	public void removeUserFromUserAnswer(String username) {
		participationAnswersUser.remove(username);
	}

	public HashMap<String, StatisticParticipationAnswer> getUserAnswers() {
		return participationAnswersUser;
	}

}

