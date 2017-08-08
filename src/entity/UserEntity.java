package entity;

/**
 * Created by Raphael on 14.06.2017.
 */

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonManagedReference;
import org.hibernate.annotations.*;
import org.json.JSONObject;
import service.DataService;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
@Table(name = "user")

public class UserEntity extends GenericEntity {

	// Attributes - no relation to other entities
	@Column(name = "username")
	private String username;

	@Column(name = "password")
	@ColumnTransformer(read = "AES_DECRYPT(password, 'DataService.secretKey')",
			           write = "AES_ENCRYPT(?, 'DataService.secretKey')")
	private String password;

	@ColumnTransformer(read = "AES_DECRYPT(firstName, 'DataService.secretKey')",
			write = "AES_ENCRYPT(?, 'DataService.secretKey')")
	@Column(name = "firstName")
	private String firstName;

	@ColumnTransformer(read = "AES_DECRYPT(surname, 'DataService.secretKey')",
			write = "AES_ENCRYPT(?, 'DataService.secretKey')")
	@Column(name = "surname")
	private String surname;

	@ColumnTransformer(read = "AES_DECRYPT(email, 'DataService.secretKey')",
			write = "AES_ENCRYPT(?, 'DataService.secretKey')")
	@Column(name = "email")
	private String email;

	@ColumnTransformer(read = "AES_DECRYPT(phoneNumber, 'DataService.secretKey')",
			write = "AES_ENCRYPT(?, 'DataService.secretKey')")
	@Column(name = "phoneNumber")
	private String phoneNr;

	@ColumnTransformer(read = "AES_DECRYPT(address, 'DataService.secretKey')",
			write = "AES_ENCRYPT(?, 'DataService.secretKey')")
	@Column(name = "address")
	private String address;

	@Column(name = "tributes")
	private String tributes;

	@ColumnTransformer(read = "AES_DECRYPT(birthday, 'DataService.secretKey')",
			write = "AES_ENCRYPT(?, 'DataService.secretKey')")
	@Column(name = "birthday")
	private String birthday;

	@Column(name = "enteredTeamOn")
	private Calendar dayOfEntry;

	@Column(name = "role")
	UserRole role;

	// Attributes which are related to other entities
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "register")
	RegisterEntity register;

	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "appointmentsOfUsers")
	private List<AppointmentEntity> appointmentsTakingPart;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "team")
	private TeamEntity team;

	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "projectsOfUsers")
	private List<ProjectEntity> projectsTakingPart;

	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(targetEntity = ChatEntity.class, cascade = CascadeType.ALL)
	@JoinTable(name = "chatsOfUsers")
	private List<ChatEntity> chats;

	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "worker", targetEntity = TaskEntity.class, cascade
			= CascadeType.ALL)
	private List<TaskEntity> tasks;

	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "user", targetEntity = StatisticEntity.class,
			cascade = CascadeType.ALL)
	private List<StatisticEntity> statistics;

	public UserEntity() {
		super();
		role = UserRole.USER;
		register = null;
		team = null;
		appointmentsTakingPart = new ArrayList<AppointmentEntity>();
		projectsTakingPart = new ArrayList<ProjectEntity>();
		chats = new ArrayList<ChatEntity>();
		tasks = new ArrayList<TaskEntity>();
		statistics = new ArrayList<StatisticEntity>();
	}

	/**
	 * @param username
	 * @param password
	 * @param firstName
	 * @param surname
	 * @param email
	 * @param phoneNr
	 * @param address
	 * @param tributes
	 * @param birthday
	 * @param dayOfEntry
	 * @param register
	 * @param appointmentsTakingPart
	 * @param team
	 * @param projectsTakingPart
	 * @param chats
	 * @param tasks
	 * @param statistics
	 */
	public UserEntity(String username, String password, String firstName,
					  String surname, String email, String phoneNr,
					  String address, String tributes, String birthday,
					  Calendar dayOfEntry, RegisterEntity register,
					  ArrayList<AppointmentEntity> appointmentsTakingPart,
					  TeamEntity team,
					  ArrayList<ProjectEntity> projectsTakingPart,
					  ArrayList<ChatEntity> chats,
					  ArrayList<TaskEntity> tasks,
					  ArrayList<StatisticEntity> statistics) {
		super();
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.surname = surname;
		this.email = email;
		this.phoneNr = phoneNr;
		this.address = address;
		this.tributes = tributes;
		this.birthday = birthday;
		this.dayOfEntry = dayOfEntry;
		this.role = UserRole.USER;
		this.register = register;
		this.appointmentsTakingPart = appointmentsTakingPart;
		this.team = team;
		this.projectsTakingPart = projectsTakingPart;
		this.chats = chats;
		this.tasks = tasks;
		this.statistics = statistics;
	}

	public UserEntity(String username, String password, String firstName,
					  String surname, String email, String phoneNr,
					  String address, String birthday) {
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.surname = surname;
		this.email = email;
		this.phoneNr = phoneNr;
		this.address = address;
		this.birthday = birthday;
		this.role = UserRole.USER;
		appointmentsTakingPart = new ArrayList<AppointmentEntity>();
		projectsTakingPart = new ArrayList<ProjectEntity>();
		chats = new ArrayList<ChatEntity>();
		tasks = new ArrayList<TaskEntity>();
		statistics = new ArrayList<StatisticEntity>();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNr() {
		return phoneNr;
	}

	public void setPhoneNr(String phoneNr) {
		this.phoneNr = phoneNr;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTributes() {
		return tributes;
	}

	public void setTributes(String tributes) {
		this.tributes = tributes;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public RegisterEntity getRegister() {
		return register;
	}

	public void setRegister(RegisterEntity register) {
		this.register = register;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public List<AppointmentEntity> getAppointmentsTakingPart() {
		return appointmentsTakingPart;
	}

	public void setAppointmentsTakingPart
			(ArrayList<AppointmentEntity> appointmentsTakingPart) {
		this.appointmentsTakingPart = appointmentsTakingPart;
	}

	public Calendar getDayOfEntry() {
		return dayOfEntry;
	}

	public void setDayOfEntry(Calendar dayOfEntry) {
		this.dayOfEntry = dayOfEntry;
	}

	public TeamEntity getTeam() {
		return team;
	}

	public void setTeam(TeamEntity team) {
		this.team = team;
	}

	public List<ProjectEntity> getProjectsTakingPart() {
		return projectsTakingPart;
	}

	public void setProjectsTakingPart(ArrayList<ProjectEntity> projectsTakingPart) {
		this.projectsTakingPart = projectsTakingPart;
	}

	public List<ChatEntity> getChats() {
		return chats;
	}

	public void setChats(ArrayList<ChatEntity> chats) {
		this.chats = chats;
	}

	public List<TaskEntity> getTasks() {
		return tasks;
	}

	public void setTasks(ArrayList<TaskEntity> tasks) {
		this.tasks = tasks;
	}

	public List<StatisticEntity> getStatistics() {
		return statistics;
	}

	public void setStatistics(ArrayList<StatisticEntity> statistics) {
		this.statistics = statistics;
	}

	private String calendarToString(Calendar calendar) {
		String result;
		if (calendar != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
			result = formatter.format(calendar.getTime());
		} else {
			result = null;
		}
		return result;
	}

	public void setAppointmentsTakingPart(List<AppointmentEntity> appointmentsTakingPart) {
		this.appointmentsTakingPart = appointmentsTakingPart;
	}

	public void setProjectsTakingPart(List<ProjectEntity> projectsTakingPart) {
		this.projectsTakingPart = projectsTakingPart;
	}

	public void setChats(List<ChatEntity> chats) {
		this.chats = chats;
	}

	public void setTasks(List<TaskEntity> tasks) {
		this.tasks = tasks;
	}

	public void setStatistics(List<StatisticEntity> statistics) {
		this.statistics = statistics;
	}

	public String toSring() {
		StringBuilder stringBuilder = new StringBuilder();
		String result;
		stringBuilder.append("{");
		stringBuilder.append("\"id\": " + "\"" + this.getId() + "\", ");
		stringBuilder = appendJSONUsername(stringBuilder, username);
		stringBuilder = appendJSONFirstName(stringBuilder, firstName);
		stringBuilder = appendJSONSurname(stringBuilder, surname);
		stringBuilder = appendJSONEmail(stringBuilder, email);
		stringBuilder = appendJSONPhoneNr(stringBuilder, phoneNr);
		stringBuilder = appendJSONAddress(stringBuilder, address);
		stringBuilder = appendJSONTributes(stringBuilder, tributes);
		stringBuilder = appendJSONBirthday(stringBuilder, birthday);
		stringBuilder = appendJSONDayOfEntry(stringBuilder, dayOfEntry);
		stringBuilder = appendJSONUserRole(stringBuilder, role.toString());
		stringBuilder = appendJSONRegisterName(stringBuilder, register);
		stringBuilder.append("}");
		result = stringBuilder.toString();
		return result;
	}

	//Appends the correct json format for the username depending on the value
	private StringBuilder appendJSONUsername(StringBuilder toAppendTo,
											 String username) {
		if (username != null && !(username.equals(""))) {
			toAppendTo.append("\"username\": " + "\"" + username + "\", ");
		} else {
			toAppendTo.append("\"username\": " + null +", ");
		}
		return toAppendTo;
	}

	//Appends the correct json format for the first name of the user depending
	// on the value
	private StringBuilder appendJSONFirstName(StringBuilder toAppendTo,
											  String firstName) {
		if (firstName != null && !(firstName.equals(""))) {
			toAppendTo.append("\"firstName\": " + "\"" + firstName
					+ "\", ");
		} else {
			toAppendTo.append("\"firstName\": " + null + ", ");
		}
		return toAppendTo;
	}

	private StringBuilder appendJSONSurname(StringBuilder toAppendTo,
											String surname) {
		if (surname != null && !(surname.equals(""))) {
			toAppendTo.append("\"surname\": " + "\"" + surname + "\", ");
		} else {
			toAppendTo.append("\"surname\": " + null + ", ");
		}
		return toAppendTo;
	}

	private StringBuilder appendJSONEmail(StringBuilder toAppendTo,
										  String email) {
		if (email != null && !(email.equals(""))) {
			toAppendTo.append("\"email\": " + "\"" + this.email + "\", ");
		} else {
			toAppendTo.append("\"email\": " + null + ", ");
		}
		return toAppendTo;
	}

	private StringBuilder appendJSONPhoneNr(StringBuilder toAppendTo,
							  String phoneNr) {
		if (phoneNr != null && !(phoneNr.equals(""))) {
			toAppendTo.append("\"phoneNr\": " + "\"" + phoneNr + "\", ");
		} else {
			toAppendTo.append("\"phoneNr\": " + null + ", ");
		}
		return toAppendTo;
	}

	private StringBuilder appendJSONAddress(StringBuilder toAppendTo,
											String address) {
		if (address != null && !(address.equals(""))) {
			toAppendTo.append("\"address\": " + "\"" + address + "\", ");
		} else {
			toAppendTo.append("\"address\": " + null + ", ");
		}
		return toAppendTo;
	}

	private StringBuilder appendJSONTributes(StringBuilder toAppendTo,
											 String tributes) {
		if (tributes != null) {
			toAppendTo.append("\"tributes\": " + "\"" + tributes + "\", ");
		} else {
			toAppendTo.append("\"tributes\": " + null + ", ");
		}
		return toAppendTo;
	}

	private StringBuilder appendJSONBirthday(StringBuilder toAppendTo,
											 String birthday) {
		if (birthday != null && !(birthday.equals(""))) {
			toAppendTo.append("\"birthday\": " + "\"" + birthday + "\", ");
		} else {
			toAppendTo.append("\"birthday\": " + null + ", ");
		}
		return toAppendTo;
	}

	private StringBuilder appendJSONDayOfEntry(StringBuilder toAppendTo,
											   Calendar dayOfEntry) {
		if (dayOfEntry != null) {
			SimpleDateFormat formatter
					= new SimpleDateFormat(GenericEntity.DATE_FORMAT);
			String dayAsSring = formatter.format(dayOfEntry.getTime());
			toAppendTo.append("\"dayOfEntry\": " + "\"" + dayAsSring + "\", ");
		} else {
			toAppendTo.append("\"dayOfEntry\": " + null + ", ");
		}
		return toAppendTo;
	}

	private StringBuilder appendJSONUserRole(StringBuilder toAppendTo,
											 String userRole) {
		if (userRole != null) {
			toAppendTo.append("\"userRole\": " + "\"" + userRole + "\", ");
		} else {
			toAppendTo.append("\"userRole\": " + null + ", ");
		}
		return toAppendTo;
	}

	private StringBuilder appendJSONRegisterName(StringBuilder toAppendTo,
												 RegisterEntity register) {
		if (register != null) {
			toAppendTo.append("\"registerName\": " + "\"" + register.getName()
					+ "\"");
		} else {
			toAppendTo.append("\"registerName\": " + null);
		}
		return toAppendTo;
	}

}

