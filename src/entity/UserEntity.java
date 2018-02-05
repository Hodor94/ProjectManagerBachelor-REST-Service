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
 * This class represents a user in the database. It is used by the framework
 * Hibernate.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "user")
public class UserEntity extends GenericEntity {

	// Normal attributes
	@ElementCollection
	@Column(name = "invitations")
	private List<String> invitationsOfTeams;

	// Represents whether the user has requested his news or not.
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
	private RegisterEntity register;	// The register of the user.

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "team")
	private TeamEntity team;	// The team of the user.

	@OneToOne
	@JoinColumn(name = "adminOfProject")
	private ProjectEntity adminOfProject;	// The project this user manages.

	// The appointments this user is taking part.
	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "appointmentsOfUsers")
	private List<AppointmentEntity> appointmentsTakingPart;

	// A list of projects the user is involved in.
	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "projectsOfUsers")
	private List<ProjectEntity> projectsTakingPart;

	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany
	private List<ChatEntity> chats; // The chats of the user.

	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "worker", targetEntity = TaskEntity.class, cascade
			= CascadeType.ALL)
	private List<TaskEntity> tasks; // The user's task.

	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "user", targetEntity = StatisticEntity.class,
			cascade = CascadeType.ALL)
	private List<StatisticEntity> statistics; // The participation statistic.

	/**
	 * A UserEntity object is been created with all the attributes being
	 * initiated with the default value.
	 */
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
	 * A UserEntity object is been created with all the attributes being
	 * initiated as the given parameter.
	 *
	 * @param username The username of the user.
	 * @param password The password of the user.
	 * @param firstName The first name of the user.
	 * @param surname   The surname of the user.
	 * @param email The e-mail address of the user.
	 * @param phoneNr The phone number of the user.
	 * @param address The address of the user.
	 * @param tributes The honors the user has gained.
	 * @param birthday The birthday of the user.
	 * @param dayOfEntry The day the user entered a team.
	 * @param register The register the user belongs to.
	 * @param appointmentsTakingPart A list of the appointments the user is
	 *                                  taking part.
	 * @param team The team the user belongs to.
	 * @param projectsTakingPart A list of appointments to user wants to take
	 *                             part.
	 * @param chats The chats of the user.
	 * @param tasks The tasks of the user.
	 * @param statistics The statistics of the user.
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

	/**
	 * A UserEntity object is been created with only the primitive attributes
	 * set with parameter values but no relations to other classes.
	 *
	 * @param username The username of the user.
	 * @param password The password of the user.
	 * @param firstName The first name of the user.
	 * @param surname The surname of the user.
	 * @param email The e-mail address of the user.
	 * @param phoneNr The phone number of the user.
	 * @param address The address of the user.
	 * @param birthday The brithday of the user.
	 */
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

	/**
	 * Returns the username.
	 *
	 * @return The value of the attribute 'username'.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username of the user.
	 *
	 * @param username The new username of this user.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Returns the password of the user.
	 *
	 * @return The value of the attribute 'password'.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the  password of this user.
	 *
	 * @param password The new password of the user.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Returns the first name of the user.
	 *
	 * @return The first name of the user.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets the first name of this user.
	 *
	 * @param firstName The new first name of the user.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Returns the surname of the user.
	 *
	 * @return The surname of the user.
	 */
	public String getSurname() {
		return surname;
	}

	/** Sets the surname of the user.
	 *
	 * @param surname The new surname of the user.
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}

	/**
	 * Returns the e-mail address of the user.
	 *
	 * @return The e-mail address of the user.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the e-mail address of the user.
	 *
	 * @param email The new e-mail address of the user.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Returns the pone number of the user.
	 *
	 * @return The phone number of the user.
	 */
	public String getPhoneNr() {
		return phoneNr;
	}

	/**
	 * Sets the phone number of the user.
	 *
	 * @param phoneNr The new phone number of the user.
	 */
	public void setPhoneNr(String phoneNr) {
		this.phoneNr = phoneNr;
	}

	/**
	 * Returns the address of the user.
	 *
	 * @return The address of the user.
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Sets the address of the user.
	 *
	 * @param address The new address of the user.
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Returns the honors the user has gained.
	 *
	 * @return The honors of the user.
	 */
	public String getTributes() {
		return tributes;
	}

	/**
	 * Sets the honors the user has gained.
	 *
	 * @param tributes The new honors of the user.
	 */
	public void setTributes(String tributes) {
		this.tributes = tributes;
	}

	/**
	 * Returns the birthday of the user.
	 *
	 * @return The birthday of the user.
	 */
	public String getBirthday() {
		return birthday;
	}

	/**
	 * Sets the birthday of the user.
	 *
	 * @param birthday The birthday of the user.
	 */
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	/**
	 * Returns the register the user is involved in.
	 *
	 * @return The register the user is involved in.
	 */
	public RegisterEntity getRegister() {
		return register;
	}

	/**
	 * Sets the register the user belongs to.
	 *
	 * @param register The new register of the user.
	 */
	public void setRegister(RegisterEntity register) {
		this.register = register;
	}

	/**
	 * Returns the role the user obtains in this system.
	 *
	 * @return The role of the user´.
	 */
	public UserRole getRole() {
		return role;
	}

	/**
	 * Sets the role of the user.
	 *
	 * @param role The new user role.
	 */
	public void setRole(UserRole role) {
		this.role = role;
	}

	/**
	 * Returns a {@see List} of appointments the user is taking part.
	 *
	 * @return The list of appointments the user is taking part.
	 */
	public List<AppointmentEntity> getAppointmentsTakingPart() {
		return appointmentsTakingPart;
	}

	/**
	 * Sets the appointments the user is taking part.
	 *
	 * @param appointmentsTakingPart A list of appointments the user is
	 *                                  taking part.
	 */
	public void setAppointmentsTakingPart
			(ArrayList<AppointmentEntity> appointmentsTakingPart) {
		this.appointmentsTakingPart = appointmentsTakingPart;
	}

	/**
	 * Returns the date the user joined the current team.
	 *
	 * @return The date the user joined the current team.
	 */
	public Calendar getDayOfEntry() {
		return dayOfEntry;
	}

	/**
	 * Sets the date the user joined the current team.
	 *
	 * @param dayOfEntry The team entry date of the user.
	 */
	public void setDayOfEntry(Calendar dayOfEntry) {
		this.dayOfEntry = dayOfEntry;
	}

	/**
	 * Returns the team the user is part of.
	 *
	 * @return The team of the user.
	 */
	public TeamEntity getTeam() {
		return team;
	}

	/**
	 * Sets the team of the user.
	 *
	 * @param team The new team of the user.
	 */
	public void setTeam(TeamEntity team) {
		this.team = team;
	}

	/**
	 * Returns a {@see List} of the projects the user is member of.
	 *
	 * @return A list of the projects the user takes part.
	 */
	public List<ProjectEntity> getProjectsTakingPart() {
		return projectsTakingPart;
	}

	/**
	 * Sets the projects the user is taking part.
	 *
	 * @param projectsTakingPart All the projects the user is taking part.
	 */
	public void setProjectsTakingPart
			(ArrayList<ProjectEntity> projectsTakingPart) {
		this.projectsTakingPart = projectsTakingPart;
	}

	/**
	 * Returns a {@see List} of the chats the user is involved.
	 *
	 * @return The chats of the user.
	 */
	public List<ChatEntity> getChats() {
		return chats;
	}

	/**
	 * Sets the chats of the user.
	 *
	 * @param chats The new chats of the user.
	 */
	public void setChats(ArrayList<ChatEntity> chats) {
		this.chats = chats;
	}

	/**
	 * Returns a {@see List} of tasks the user has to work on.
	 *
	 * @return The tasks of the user.
	 */
	public List<TaskEntity> getTasks() {
		return tasks;
	}

	/**
	 * Sets the tasks the user has to work on,
	 *
	 * @param tasks The tasks the user works on.
	 */
	public void setTasks(ArrayList<TaskEntity> tasks) {
		this.tasks = tasks;
	}

	/**
	 * Returns a {@see List} of the participation statistics of the user.
	 *
	 * @return
	 */
	public List<StatisticEntity> getStatistics() {
		return statistics;
	}

	/**
	 * Sets the participation statistics of the user.
	 *
	 * @param statistics The participation statistics of the user.
	 */
	public void setStatistics(ArrayList<StatisticEntity> statistics) {
		this.statistics = statistics;
	}

	/**
	 * Sets the appointments the user takes part.
	 *
	 * @param appointmentsTakingPart The appointments the user takes part.
	 */
	public void setAppointmentsTakingPart(List<AppointmentEntity> appointmentsTakingPart) {
		this.appointmentsTakingPart = appointmentsTakingPart;
	}

	/**
	 * Sets the projects the user is involved with.
	 *
	 * @param projectsTakingPart The projects of the user.
	 */
	public void setProjectsTakingPart(List<ProjectEntity> projectsTakingPart) {
		this.projectsTakingPart = projectsTakingPart;
	}

	/**
	 * Sets the chats of the user.
	 *
	 * @param chats The chats of the user.
	 */
	public void setChats(List<ChatEntity> chats) {
		this.chats = chats;
	}

	/**
	 * Sets the tasks the user has to work on.
	 *
	 * @param tasks The tasks the user has to work on.
	 */
	public void setTasks(List<TaskEntity> tasks) {
		this.tasks = tasks;
	}

	public void setStatistics(List<StatisticEntity> statistics) {
		this.statistics = statistics;
	}

	/**
	 *  Transforms a {@see Calendar} object into a {@see String} attribute.
	 *
	 * @param calendar The object to transform.
	 *
	 * @return A String of the transformed object.
	 */
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

	/**
	 * Transforms this user into a JSON String object.
	 *
	 * @return The user in JSON format.
	 */
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

	/*
	Transforms the attribute 'adminOfProject' into a JSON attribute and
	returns it.
	 */
	private String appendJSONAdminOfProject(ProjectEntity adminOfProject) {
		if (adminOfProject != null) {
			return "\"adminOfProject\": \"" + adminOfProject.getName() + "\", ";
		} else {
			return "\"adminOfProject\": " + null + ", ";
		}
	}

	/*
	Returns the name of the team as a JSON attribute.
	 */
	private String appendTeamName(TeamEntity team) {
		if (team != null) {
			return "\"team\": \"" + team.getName() + "\"";
		} else {
			return "\"team\": " + null;
		}
	}

	/*
	Returns the username as a JSON attribute.
	 */
	private String appendJSONUsername(String username) {
		if (username != null && !(username.equals(""))) {
			return "\"username\": " + "\"" + username + "\", ";
		} else {
			return "\"username\": " + null + ", ";
		}
	}

	/*
	Returns the first name of the user as a JSON attribute.
	 */
	private String appendJSONFirstName(String firstName) {
		if (firstName != null && !(firstName.equals(""))) {

			return "\"firstName\": " + "\"" + firstName + "\", ";
		} else {
			return "\"firstName\": " + null + ", ";
		}
	}

	/*
	Returns the surname of the user as a JSON attribute.
	 */
	private String appendJSONSurname(String surname) {
		if (surname != null && !(surname.equals(""))) {
			return "\"surname\": " + "\"" + surname + "\", ";
		} else {
			return "\"surname\": " + null + ", ";
		}
	}

	/*
	Returns the e-mail address as a JSON attribute.
	 */
	private String appendJSONEmail(String email) {
		if (email != null && !(email.equals(""))) {
			return "\"email\": " + "\"" + email + "\", ";
		} else {
			return "\"email\": " + null + ", ";
		}
	}

	/*
	Returns the phone number of the user as a JSON attribute.
	 */
	private String appendJSONPhoneNr(String phoneNr) {
		if (phoneNr != null && !(phoneNr.equals(""))) {
			return "\"phoneNr\": " + "\"" + phoneNr + "\", ";
		} else {
			return "\"phoneNr\": " + null + ", ";
		}
	}

	/*
	Returns the address of the user as a JSON attribute.
	 */
	private String appendJSONAddress(String address) {
		if (address != null && !(address.equals(""))) {
			return "\"address\": " + "\"" + address + "\", ";
		} else {
			return "\"address\": " + null + ", ";
		}
	}

	/*
	Returns the honors of the user as a JSON attribute.
	 */
	private String appendJSONTributes(String tributes) {
		if (tributes != null && !(tributes.equals(""))) {
			return "\"tributes\": " + "\"" + tributes + "\", ";
		} else {
			return "\"tributes\": " + null + ", ";
		}
	}

	/*
	Returns the birthday of the user as a JSON attribute.
	 */
	private String appendJSONBirthday(String birthday) {
		if (birthday != null && !(birthday.equals(""))) {
			return "\"birthday\": " + "\"" + birthday + "\", ";
		} else {
			return "\"birthday\": " + null + ", ";
		}
	}

	/*
	Returns the date the user has joined the team as a JSON attribute.
	 */
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

	/*
	Returns the role of the user as a JSON attribute.
	 */
	private String appendJSONUserRole(String userRole) {
		if (userRole != null) {
			return "\"userRole\": " + "\"" + userRole + "\", ";
		} else {
			return "\"userRole\": " + null + ", ";
		}
	}

	/*
	Returns the group the user is part of as a JSON attribute.
	 */
	private String appendJSONRegisterName(RegisterEntity register) {
		if (register != null && register.getName() != null &&
				!(register.getName().equals(""))) {
			return "\"registerName\": " + "\"" + register.getName() + "\", ";
		} else {
			return "\"registerName\": " + null + ", ";
		}
	}

	/**
	 * Returns the project the user is admin of.
	 *
	 * @return The project managed by this user.
	 */
	public ProjectEntity getAdminOfProject() {
		return adminOfProject;
	}

	/**
	 * Returns the project the user manages.
	 *
	 * @param adminOfProject The project the user manages.
	 */
	public void setAdminOfProject(ProjectEntity adminOfProject) {
		this.adminOfProject = adminOfProject;
	}

	/**
	 * Adds an invitation of a team. The user can answer a invitation and
	 * joins the team.
	 *
	 * @param teamName The name of the team which invited the user.
	 */
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

	/**
	 * Returns a {@see List} of the invitations for the user.
	 * @return
	 */
	public List<String> getInvitationsOfTeams() {
		return invitationsOfTeams;
	}

	/**
	 * Sets the invitations the user got.
	 *
	 * @param invitationsOfTeams The team names of the teams which invited
	 *                              the user to join.
	 */
	public void setInvitationsOfTeams(List<String> invitationsOfTeams) {
		this.invitationsOfTeams = invitationsOfTeams;
	}

	/**
	 * Removes a invitation the user has declined.
	 *
	 * @param teamName The name of the team which should get removed.
	 *
	 * @return true if the operation was a success and false if it was a
	 * failure.
	 */
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

	/**
	 * Returns the last time the user check for his or her messages.
	 * @return
	 */
	public Calendar getLastCheckedMessages() {
		return lastCheckedMessages;
	}

	/**
	 * Sets the point of time the user checked his or her messages the last
	 * time.
	 *
	 * @param lastCheckedMessages The point of time the user checked his or
	 *                               her messages for the last time.
	 */
	public void setLastCheckedMessages(Calendar lastCheckedMessages) {
		this.lastCheckedMessages = lastCheckedMessages;
	}
}

