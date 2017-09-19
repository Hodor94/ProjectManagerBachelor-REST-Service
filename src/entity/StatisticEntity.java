package entity;

/**
 * Created by Raphael on 14.06.2017.
 */

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;
//import org.jasypt.hibernate5.type.EncryptedDoubleAsStringType;
import org.json.JSONObject;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "statistic")
/*@TypeDef(
		name = "encryptedDouble",
		defaultForType = EncryptedDoubleAsStringType.class,
		typeClass = EncryptedDoubleAsStringType.class,
		parameters = {
				@Parameter(name = "algorithm", value = "PBEWithMD5AndDES"),
				@Parameter(name = "password", value = "DataService.secretKey")
		}
)
*/
public class StatisticEntity extends GenericEntity {

	// Attributes without any relation to other entities
	@Column(name = "numberOfParticipiation")
	private double numberOfParticipiation;

	@Column(name = "percentage")
	private double percentage;

	@Column(name = "numberOfAllAppointments")
	private double numberOfAllAppointments;

	// Attributes related to other entities
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user")
	private UserEntity user;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "project")
	private ProjectEntity project;

	public StatisticEntity() {
		super();
	}

	public StatisticEntity(UserEntity user, ProjectEntity project) {
		this.user = user;
		this.project = project;
	}

	public StatisticEntity(int numberOfParticipiation, double percentage,
						   int numberOfAllAppointments) {
		super();
		this.numberOfParticipiation = numberOfParticipiation;
		this.percentage = percentage;
		this.numberOfAllAppointments = numberOfAllAppointments;


	}

	public double getNumberOfParticipiation() {
		return numberOfParticipiation;
	}

	public void setNumberOfParticipiation(int numberOfParticipiation) {
		this.numberOfParticipiation = numberOfParticipiation;
	}
	public double getPercentage() {
		return numberOfParticipiation / numberOfAllAppointments;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	public double getNumberOfAllAppointments() {
		return numberOfAllAppointments;
	}

	public void setNumberOfAllAppointments(int numberOfAllAppointments) {
		this.numberOfAllAppointments = numberOfAllAppointments;
	}

	public UserEntity getUser() {
		return user;
	}

	public ProjectEntity getProject() {
		return project;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public void setProject(ProjectEntity project) {
		this.project = project;
	}

	public String toString() {
		String result;
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{");
		stringBuilder.append("\"id\": " + "\"" + this.getId() + "\", ");
		stringBuilder.append("\"numberOfParticipiation\": " + "\"" // Numbers are never null, no proof needed
				+ this.numberOfParticipiation + "\", ");
		stringBuilder.append("\"numberOfAllAppointments\": " + "\""
				+ this.numberOfAllAppointments + "\", ");
		stringBuilder.append("\"percentage\": " + "\"" + this.percentage
				+ "\", ");
		stringBuilder.append(appendJSONUserName(user));
		stringBuilder.append(appendJSONProject(project));
		stringBuilder.append("}");
		result = stringBuilder.toString();
		return result;
	}

	private String appendJSONUserName(UserEntity user) {
		if (user != null && user.getUsername() != null && !(user.getUsername().equals(""))) {
			return "\"user\": " + "\""
					+ encodeToUTF8(user.getUsername()) + "\", ";
		} else {
			return "\"user\": " + null + ", ";
		}
	}

	private String appendJSONProject(ProjectEntity project) {
		if (project != null) {
			return "\"project\": " + project.toString();
		} else {
			return "\"project\": " + null;
		}
	}

	public void increaseNumberOfAllAppointments() {
		numberOfAllAppointments++;
	}

	public void increaseNumberOfParticipiation() {
		numberOfParticipiation++;
	}

}
