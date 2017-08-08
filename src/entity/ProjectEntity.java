package entity;

/**
 * Created by Raphael on 14.06.2017.
 */

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;
//import org.jasypt.hibernate5.type.EncryptedIntegerAsStringType;
import org.json.JSONObject;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "project")
/*
@TypeDef(name = "encryptedInteger",
		defaultForType = EncryptedIntegerAsStringType.class,
		typeClass = EncryptedIntegerAsStringType.class,
		parameters = {
				@Parameter(name = "algorithm", value = "PBEWithMD5AndDES"),
				@Parameter(name = "password", value = "DataService.secretKey")
		}
) */
public class ProjectEntity extends GenericEntity {

	// Attributes without any relation to other entities
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
	private String deadline;

	@Column(name = "numberOfAppointments")
	// @Type(type = "encryptedInteger")
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

	public ProjectEntity() {
		super();
		appointments = new ArrayList<AppointmentEntity>();
		users = new ArrayList<UserEntity>();
		statistics = new ArrayList<StatisticEntity>();
	}

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

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public UserEntity getProjectManager() {
		return projectManager;
	}

	public void setProjectManager(UserEntity projectManager) {
		this.projectManager = projectManager;
	}

	public List<AppointmentEntity> getAppointments() {
		return appointments;
	}

	public void setAppointments(ArrayList<AppointmentEntity> appointments) {
		this.appointments = appointments;
	}

	public int getNumberOfAppointments() {
		return numberOfAppointments;
	}

	public void setNumberOfAppointments(int numberOfAppointments) {
		this.numberOfAppointments = numberOfAppointments;
	}

	public List<UserEntity> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<UserEntity> users) {
		this.users = users;
	}

	public TeamEntity getTeam() {
		return team;
	}

	public void setTeam(TeamEntity team) {
		this.team = team;
	}

	public List<StatisticEntity> getStatistics() {
		return statistics;
	}

	public void setStatistics(ArrayList<StatisticEntity> statistics) {
		this.statistics = statistics;
	}

	public void setAppointments(List<AppointmentEntity> appointments) {
		this.appointments = appointments;
	}

	public void setUsers(List<UserEntity> users) {
		this.users = users;
	}

	public void setStatistics(List<StatisticEntity> statistics) {
		this.statistics = statistics;
	}

	public void increaseNumberOfAppointments() {
		this.numberOfAppointments++;
	}

	public void decreaseNumberOfAppointments() {
		numberOfAppointments--;
	}

	private String calendarToString(Calendar calendar) {
		String result;
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		result = formatter.format(calendar.getTime());
		return result;
	}

	// TODO rework
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		String result;
		stringBuilder.append("{\"id\": " + "\"" + this.getId() + "\", ");
		stringBuilder.append("\"name\": " + "\"" + this.getName() + "\", ");
		stringBuilder.append("\"description\": " + "\"" + this.description
				+ "\", ");
		stringBuilder.append("\"deadline\": " + "\"" + this.getDeadline()
				+ "\", ");
		stringBuilder.append("\"numberOfAppoinments\": " + "\""
				+ this.numberOfAppointments + "\", ");
		stringBuilder.append("\"manager\": " + this.projectManager.toSring()
				+ ", ");
		stringBuilder.append("\"team\": " + this.team.toString());
		stringBuilder.append("}");
		result = stringBuilder.toString();
		return result;
	}

}

