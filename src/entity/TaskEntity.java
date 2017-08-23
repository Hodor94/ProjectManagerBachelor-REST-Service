package entity;

/**
 * Created by Raphael on 14.06.2017.
 */

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.ColumnTransformer;
import org.json.JSONObject;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity
@Table(name = "task")
@JsonSerialize
public class TaskEntity extends GenericEntity {

	// Attributes - not related to any other Entity
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

	// Attributes which refer to other entities
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "worker")
	private UserEntity worker;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "team")
	private TeamEntity team;

	public TaskEntity() {
		super();
	}

	public TaskEntity(String name, String description, String deadline,
					  UserEntity worker, TeamEntity team) {
		super();
		this.name = name;
		this.description = description;
		this.deadline = deadline;
		this.worker = worker;
		this.team = team;
	}

	public TaskEntity(String name, String description, String deadline,
					  TeamEntity team) {
		this.name = name;
		this.deadline = deadline;
		this.description = description;
		this.team = team;
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

	public UserEntity getWorker() {
		return worker;
	}

	public void setWorker(UserEntity worker) {
		this.worker = worker;
	}

	public TeamEntity getTeam() {
		return team;
	}

	public void setTeam(TeamEntity team) {
		this.team = team;
	}

	private String calendarToString(Calendar calendar) {
		String result;
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		result = formatter.format(calendar.getTime());
		return result;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		String result;
		stringBuilder.append("{");
		stringBuilder.append("\"id\": " + "\"" + this.getId() + "\",");
		stringBuilder.append(appendJSONName(name));
		stringBuilder.append(appendJSONDescription(description));
		stringBuilder.append(appendJSONDeadline(deadline));
		stringBuilder.append("}");
		result = stringBuilder.toString();
		return result;
	}

	private String appendJSONName(String name) {
		if (name != null && !(name.equals(""))) {
			return "\"name\": " + "\"" + encodeToUTF8(name) + "\", ";
		} else {
			return "\"name\": " + null + ", ";
		}
	}

	private String appendJSONDescription(String description) {
		if (description != null && !(description.equals(""))) {
			return "\"description\": " + "\""
					+ encodeToUTF8(description) + "\", ";
		} else {
			return "\"description\": " + null + ", ";
		}
	}

	private String appendJSONDeadline(String deadline) {
		if (deadline != null && !(deadline.equals(""))) {
			return "\"deadline\": " + "\"" + encodeToUTF8(deadline) + "\"";
		} else {
			return "\"deadline\": " + null;
		}
	}

}
