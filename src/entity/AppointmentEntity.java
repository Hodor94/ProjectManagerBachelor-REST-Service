package entity;

import com.google.gson.Gson;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.json.JSONObject;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

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

	@ColumnTransformer(read = "AES_DECRYPT(name, 'DataService.secretKey')",
					   write = "AES_ENCRYPT(?, 'DataService.secretKey')")
	@Column(name = "name")
	private String name;

	@ColumnTransformer(read = "AES_DECRYPT(description, 'DataService" +
			".secretKey')",
					   write = "AES_ENCRYPT(?, 'DataService.secretKey')")
	@Column(name = "description")
	private String description;

	@ColumnTransformer(read = "AES_DECRYPT(deadline, 'DataService.secretKey')",
					   write = "AES_ENCRYPT(?, 'DataService.secretKey')")
	@Column(name = "deadline")
	private String deadline; // The date of the appointment.

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
	}

	/**
	 *
	 * @param name The name of the new appointment.
	 * @param description The description of the appointment.
	 * @param deadline The date of the appointment.
	 * @param project The project this appointment belongs to.
	 * @param userTakingPart A list of all the users who are coming to the
	 *                          appointment.
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
	 *                         appointment.
	 */
	public void setUserTakinPart(ArrayList<UserEntity> userTakinPart) {
		this.userTakingPart = userTakinPart;
	}

	// TODO: proof if needed
	private String calendarToString(Calendar calendar) {
		String result;
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		result = formatter.format(calendar.getTime());
		return result;
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
		Gson gson = new Gson();
		StringBuilder stringBuilder = new StringBuilder();
		String result = "";
		stringBuilder.append("{");
		stringBuilder.append("\"id\": " + "\"" + this.getId() + "\",");
		stringBuilder.append("\"name\": " + "\"" + this.name + "\",");
		stringBuilder.append("\"description\": " + "\"" + this.description
				+ "\",");
		stringBuilder.append("\"deadline\": " + "\"" + this.deadline + "\"");
		stringBuilder.append("}");
		result = stringBuilder.toString();
		return result;
	}

}

