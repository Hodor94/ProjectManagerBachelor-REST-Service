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
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;

@Entity
@Table(name = "user")

public class UserEntity extends GenericEntity {

	// Normal attributes
	@ElementCollection
	@Column(name = "invitations")
	private List<String> invitationsOfTeams;

	@Column(name = "updated")
	private boolean updated;

	// Attributes - no relation to other entities
	@Column(name = "username")
	private String username;

	@Column(name = "password")
	@ColumnTransformer(write = "AES_ENCRYPT(?, 'secret')",
	read = "AES_DECRYPT(password, 'secret')", forColumn = "password")
	private String password;

	@Column(name = "firstName")
	private String firstName;

	@Column(name = "surname")
	private String surname;

	@Column(name = "email")
	private String email;

	@Column(name = "phoneNumber")
	private String phoneNr;

	@Column(name = "address")
	private String address;

	@Column(name = "tributes")
	private String tributes;

	@Column(name = "birthday")
	private String birthday;

	@Column(name = "enteredTeamOn")
	private Calendar dayOfEntry;

	@Column(name = "lastCheckedMessages")
	private Calendar lastCheckedMessages;

	@Column(name = "role")
	private UserRole role;

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

	@OneToOne
	@JoinColumn(name = "adminOfProject")
	private ProjectEntity adminOfProject;

	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "projectsOfUsers")
	private List<ProjectEntity> projectsTakingPart;

	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany
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
		appointmentsTakingPart = new ArrayList<>();
		projectsTakingPart = new ArrayList<>();
		chats = new ArrayList<>();
		tasks = new ArrayList<>();
		statistics = new ArrayList<>();
		invitationsOfTeams = new ArrayList<>();
		updated = false;
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
		invitationsOfTeams = new ArrayList<>();
		updated = false;
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
		invitationsOfTeams = new ArrayList<>();
		updated = false;
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
		stringBuilder.append(appendJSONUsername(username));
		stringBuilder.append(appendJSONFirstName(firstName));
		stringBuilder.append(appendJSONSurname(surname));
		stringBuilder.append(appendJSONEmail(email));
		stringBuilder.append(appendJSONPhoneNr(phoneNr));
		stringBuilder.append(appendJSONAddress(address));
		stringBuilder.append(appendJSONTributes(tributes));
		stringBuilder.append(appendJSONBirthday(birthday));
		stringBuilder.append(appendJSONDayOfEntry(dayOfEntry));
		stringBuilder.append(appendJSONUserRole(role.toString()));
		stringBuilder.append(appendJSONRegisterName(register));
		stringBuilder.append(appendJSONAdminOfProject(adminOfProject));
		stringBuilder.append(appendTeamName(team));
		stringBuilder.append("}");
		result = stringBuilder.toString();
		return result;
	}

	private String appendJSONAdminOfProject(ProjectEntity adminOfProject) {
		if (adminOfProject != null) {
			return "\"adminOfProject\": \"" + adminOfProject.getName() + "\", ";
		} else {
			return "\"adminOfProject\": " + null + ", ";
		}
	}

	private String appendTeamName(TeamEntity team) {
		if (team != null) {
			return "\"team\": \"" + team.getName() + "\"";
		} else {
			return "\"team\": " + null;
		}
	}

	//Appends the correct json format for the username depending on the value
	private String appendJSONUsername(String username) {
		if (username != null && !(username.equals(""))) {
			return "\"username\": " + "\"" + username + "\", ";
		} else {
			return "\"username\": " + null + ", ";
		}
	}

	//Appends the correct json format for the first name of the user depending
	// on the value
	private String appendJSONFirstName(String firstName) {
		if (firstName != null && !(firstName.equals(""))) {

			return "\"firstName\": " + "\"" + firstName + "\", ";
		} else {
			return "\"firstName\": " + null + ", ";
		}
	}

	private String appendJSONSurname(String surname) {
		if (surname != null && !(surname.equals(""))) {
			return "\"surname\": " + "\"" + surname + "\", ";
		} else {
			return "\"surname\": " + null + ", ";
		}
	}

	private String appendJSONEmail(String email) {
		if (email != null && !(email.equals(""))) {
			return "\"email\": " + "\"" + email + "\", ";
		} else {
			return "\"email\": " + null + ", ";
		}
	}

	private String appendJSONPhoneNr(String phoneNr) {
		if (phoneNr != null && !(phoneNr.equals(""))) {
			return "\"phoneNr\": " + "\"" + phoneNr + "\", ";
		} else {
			return "\"phoneNr\": " + null + ", ";
		}
	}

	private String appendJSONAddress(String address) {
		if (address != null && !(address.equals(""))) {
			return "\"address\": " + "\"" + address + "\", ";
		} else {
			return "\"address\": " + null + ", ";
		}
	}

	private String appendJSONTributes(String tributes) {
		if (tributes != null && !(tributes.equals(""))) {
			return "\"tributes\": " + "\"" + tributes + "\", ";
		} else {
			return "\"tributes\": " + null + ", ";
		}
	}

	private String appendJSONBirthday(String birthday) {
		if (birthday != null && !(birthday.equals(""))) {
			return "\"birthday\": " + "\"" + birthday + "\", ";
		} else {
			return "\"birthday\": " + null + ", ";
		}
	}

	private String appendJSONDayOfEntry(Calendar dayOfEntry) {
		if (dayOfEntry != null) {
			SimpleDateFormat formatter
					= new SimpleDateFormat(GenericEntity.DATE_FORMAT);
			String dayAsSring = formatter.format(dayOfEntry.getTime());
			return "\"dayOfEntry\": " + "\"" + dayAsSring + "\", ";
		} else {
			return "\"dayOfEntry\": " + null + ", ";
		}
	}

	private String appendJSONUserRole(String userRole) {
		if (userRole != null) {
			return "\"userRole\": " + "\"" + userRole + "\", ";
		} else {
			return "\"userRole\": " + null + ", ";
		}
	}

	private String appendJSONRegisterName(RegisterEntity register) {
		if (register != null && register.getName() != null && !(register.getName().equals(""))) {
			return "\"registerName\": " + "\"" + register.getName() + "\", ";
		} else {
			return "\"registerName\": " + null + ", ";
		}
	}

	public ProjectEntity getAdminOfProject() {
		return adminOfProject;
	}

	public void setAdminOfProject(ProjectEntity adminOfProject) {
		this.adminOfProject = adminOfProject;
	}

	public void addInvitation(String teamName) {
		if (invitationsOfTeams == null) {
			invitationsOfTeams = new ArrayList<>();
			invitationsOfTeams.add(teamName);
			updated = true;
		} else {
			if (!invitationsOfTeams.contains(teamName)) {
				invitationsOfTeams.add(teamName);
				updated = true;
			}
		}
	}

	public List<String> getInvitationsOfTeams() {
		return invitationsOfTeams;
	}

	public void setInvitationsOfTeams(List<String> invitationsOfTeams) {
		this.invitationsOfTeams = invitationsOfTeams;
	}

	public boolean removeInvitation(String teamName) {
		boolean result = false;
		if (invitationsOfTeams != null) {
			invitationsOfTeams.remove(teamName);
		}
		if (!invitationsOfTeams.contains(username)) {
			result = true;
			updated = true;
		}
		return result;
	}

	public boolean isUpdated() {
		return updated;
	}

	public Calendar getLastCheckedMessages() {
		return lastCheckedMessages;
	}

	public void setLastCheckedMessages(Calendar lastCheckedMessages) {
		this.lastCheckedMessages = lastCheckedMessages;
	}
}

