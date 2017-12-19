package service;

/**
 * Created by Raphael on 14.06.2017.
 */

import com.sun.jersey.json.impl.provider.entity.JSONArrayProvider;
import com.sun.org.glassfish.external.statistics.Statistic;
import dao.*;
import entity.*;
import org.codehaus.jettison.json.JSONArray;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataService {
	protected static final String DATE_FORMAT = "dd.MM.yyyy hh:mm:ss";

	private AppointmentDAO appointmentDAO;
	private ChatDAO chatDAO;
	private MessageDAO messageDAO;
	private ProjectDAO projectDAO;
	private RegisterDAO registerDAO;
	private StatisticDAO statisticDAO;
	private TaskDAO taskDAO;
	private TeamDAO teamDAO;
	private UserDAO userDAO;
	private static SecretKey secretKey;
	public static final String secret = generateSecret();
	private SimpleDateFormat formatter;
	private final String NEW_PROJECT = "Ein neues Projekt ";
	private final String NEW_APPOINTMENT = "Ein neues Meeting ";
	private final String NEW_TASK = "Eine neue Aufgabe ";
	private final String NEW_REGISTER = "Eine neue Gruppe ";
	private final String CREATED = " wurde erstellt!\n";
	private final String PROJECT = "Das Projekt ";
	private final String APPOINTMENT = "Das Meeting ";
	private final String TASK = "Die Aufgabe ";
	private final String REGISTER = "Die Gruppe ";
	private final String DELETED = " wurde gelöscht!\n";

	public DataService() {
		formatter = new SimpleDateFormat("dd.MM.yyyy");
		appointmentDAO = new AppointmentDAO();
		chatDAO = new ChatDAO();
		messageDAO = new MessageDAO();
		projectDAO = new ProjectDAO();
		registerDAO = new RegisterDAO();
		statisticDAO = new StatisticDAO();
		taskDAO = new TaskDAO();
		teamDAO = new TeamDAO();
		userDAO = new UserDAO();
		KeyGenerator keyGenerator;
		try {
			keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(256);
			secretKey = keyGenerator.generateKey();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	private static String generateSecret() {
		char[] chars = "abcdefghijklmnopqrstuvwABCDEFGHIJKLMNOPQRSTUVWXYZ"
				.toCharArray();
		StringBuilder stringBuilder = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 20; i++) {
			char c  = chars[random.nextInt(chars.length)];
			stringBuilder.append(c);
		}
		return stringBuilder.toString();
	}

	// Generates a secret key which is used for user authentication
	protected SecretKey generateSecretKey() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(256); // The key size
			SecretKey secretKey = keyGenerator.generateKey();
			return secretKey;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	public UserEntity getUser(String username) {
		UserEntity result = userDAO.getUserByUsername(username);
		return result;
	}

	public Collection<UserEntity> getUsersOfTeam(String teamName) {
		Collection<UserEntity> result;
		TeamEntity team = teamDAO.getTeamByTeamName(teamName);
		if (team != null) {
			result = team.getUsers();
		} else {
			result = null;
		}
		return result;
	}

	public boolean login(String username, String password) {
		UserEntity userToLogin = userDAO.getUserByUsername(username);
		if (userToLogin != null && userToLogin.getPassword().equals(password)) {
			return true;
		} else {
			return false;
		}
	}

	public StatisticEntity getStatisticOfUser(String username,
											  String projectName,
											  String teamName) {
		StatisticEntity result = null;
		ProjectEntity project = projectDAO.getProject(projectName, teamName);
		UserEntity user = userDAO.getUserByUsername(username);
		if (project != null && user != null) {
			ArrayList<StatisticEntity> statistics
					= new ArrayList<StatisticEntity>(project.getStatistics());
			if (!statistics.isEmpty()) {
				for (StatisticEntity statistic : statistics) {
					if (statistic.getUser().getUsername().equals(user
							.getUsername())
							&& statistic.getProject().getName().equals
							(projectName)) {
						result = statistic;
						return result;
					}
				}
			}
		}
		return result;
	}

	public Collection<StatisticEntity> getStatisticsOfProject
			(String projectName, String teamName) {
		Collection<StatisticEntity> result;
		ProjectEntity project = projectDAO.getProject(projectName, teamName);
		if (project != null) {
			result = project.getStatistics();
		} else {
			result = null;
		}
		return result;
	}

	public TaskEntity getTask(String taskName, String teamName) {
		TeamEntity team = teamDAO.getTeamByTeamName(teamName);
		TaskEntity result;
		if (team != null) {
			result = taskDAO.getTaskByTaskName(taskName, team);
		} else {
			result = null;
		}
		return result;
	}

	public RegisterEntity getRegister(String registerName, String teamName) {
		RegisterEntity result;
		result = registerDAO.getRegisterByName(registerName, teamName);
		return result;
	}

	public boolean registerUser(String username, String password,
								String firstName, String surname, String email,
								String phonNr, String address,
								String birthday) {
		boolean result = false;
		if (userDAO.getUserByUsername(username) == null) {

			UserEntity newUser = new UserEntity(username, password,
					firstName, surname, email, phonNr, address,
					birthday);
			SimpleDateFormat formatter
					= new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			Calendar initialTime = Calendar.getInstance();
			try {
				initialTime.setTime(formatter.parse("01.01.2000 23:59:00"));
			} catch (ParseException e) {
				// Do nothing. Try block will always work
			}
			newUser.setLastCheckedMessages(initialTime);
			userDAO.saveOrUpdate(newUser);
			result = true;

		}
		return result;
	}

	private Calendar parseStringToCalendar(String date) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		try {
			calendar.setTime(formatter.parse(date));
		} catch (ParseException e) {
			calendar = null;
			System.out.println("Error! Could not parse date!");
		}
		return calendar;
	}

	public boolean createNewTeam(String name, String description, String adminName) {
		boolean result = false;
		UserEntity admin = userDAO.getUserByUsername(adminName);
		TeamEntity team = teamDAO.getTeamByTeamName(name);
		if (admin != null && team == null) {
			admin.setRole(UserRole.ADMINISTRATOR);
			Date now = new Date(System.currentTimeMillis());
			Calendar dayOfEntry = Calendar.getInstance();
			dayOfEntry.setTime(now);
			admin.setDayOfEntry(dayOfEntry);
			team = new TeamEntity(name, description, admin);
			team.getUsers().add(admin);
			admin.setTeam(team);
			userDAO.saveOrUpdate(admin);
			result = true;
		}
		return result;
	}

	public void takePartAtAppointment(String teamName, String projectName,
									  int appointmentId, String username) {
		AppointmentEntity appointment = getAppointment(projectName,
				appointmentId, teamName);
		UserEntity user = getUser(username);
		StatisticEntity statisticOfUser = getStatisticOfUser(username,
				projectName, teamName);
		if (user != null && appointment != null && statisticOfUser != null) {
			user.getAppointmentsTakingPart().add(appointment);
			appointment.getUserTakinPart().add(user);
			statisticOfUser.increaseNumberOfParticipation();
			userDAO.saveOrUpdate(user);
			appointmentDAO.saveOrUpdate(appointment);
			statisticDAO.saveOrUpdate(statisticOfUser);
		}

	}


	public boolean createNewProject(String teamName, String projectName,
									String description, String projectManager,
									String deadline) {
		boolean result = false;
		UserEntity manager = userDAO.getUserByUsername(projectManager);
		TeamEntity team = teamDAO.getTeamByTeamName(teamName);
		TeamEntity managersTeam = manager.getTeam();
		if (team != null && manager != null
				&& managersTeam.getName().equals(team.getName())) {
			ProjectEntity project = new ProjectEntity(projectName, description,
					deadline, manager, team);
			StatisticEntity newStatistic
					= new StatisticEntity(manager, project);
			newStatistic.setNumberOfAllAppointments(1);
			project.getStatistics().add(newStatistic);
			project.increaseNumberOfAppointments();
			projectDAO.saveOrUpdate(project);
			if (manager.getRole() != UserRole.ADMINISTRATOR) {
				manager.setRole(UserRole.PROJECT_OWNER);
			}
			team.addNews(NEW_PROJECT + projectName + CREATED);
			teamDAO.saveOrUpdate(team);
			manager.setAdminOfProject(project);
			manager.getProjectsTakingPart().add(project);
			manager.getStatistics().add(newStatistic);
			userDAO.saveOrUpdate(manager);
			ArrayList<UserEntity> userTakingPart = new ArrayList<>();
			AppointmentEntity appointment
					= new AppointmentEntity(projectName + " DEADLINE",
					"Abschluss des Projekts " + projectName, deadline,
					project, userTakingPart);
			appointment.setIsDeadline(true);
			appointment.addUserAnswer(projectManager,
					StatisticParticipationAnswer.MAYBE);
			appointmentDAO.saveOrUpdate(appointment);
			result = true;
		}
		return result;
	}

	private StatisticEntity createStatistic(String username,
											String projectName, String
													teamName) {
		UserEntity user = userDAO.getUserByUsername(username);
		ProjectEntity project = projectDAO.getProject(projectName, teamName);
		StatisticEntity statistic = new StatisticEntity(user, project);
		return statistic;
	}

	private void addUsersToChat(ChatEntity chat, Collection<String> users) {
		for (String username : users) {
			UserEntity user = getUser(username);
			user.getChats().add(chat);
			userDAO.saveOrUpdate(user);
		}
	}

	public boolean createNewTask(String name, String description,
								 String date, String teamName,
								 String username) {
		boolean result = false;
		TeamEntity team = teamDAO.getTeamByTeamName(teamName);
		UserEntity user = getUser(username);
		if (team != null && user != null) {
			TaskEntity task = taskDAO.getTaskByTaskName(name, team);
			if (task == null) {
				user.getTasks().add(task);
				task = new TaskEntity(name, description, date, team, user);
				taskDAO.saveOrUpdate(task);
				userDAO.saveOrUpdate(user);
				team.addNews(NEW_TASK + name + CREATED);
				teamDAO.saveOrUpdate(team);
				result = true;
			}
		}
		return result;
	}

	public boolean setWorkerToTask(String taskName, String userName, String
			teamName) {
		boolean result = false;
		TeamEntity team = teamDAO.getTeamByTeamName(teamName);
		TaskEntity task = taskDAO.getTaskByTaskName(taskName, team);
		UserEntity user = userDAO.getUserByUsername(userName);
		if (task != null && team != null) {
			task.setWorker(user);
			taskDAO.saveOrUpdate(task);
			result = true;
		}
		return result;
	}

	public boolean createNewRegister(String registerName, String teamName,
									 String color) {
		boolean result = false;
		RegisterEntity register = registerDAO.getRegisterByName(registerName,
				teamName);
		TeamEntity team = teamDAO.getTeamByTeamName(teamName);
		if (register == null && team != null) {
			register = new RegisterEntity(registerName,
					new ArrayList<UserEntity>(), team, color);
			registerDAO.saveOrUpdate(register);
			result = true;
		}
		team.addNews(NEW_REGISTER + registerName + CREATED);
		teamDAO.saveOrUpdate(team);
		return result;
	}

	public boolean setUsersRegister(String username, String registerName,
									String teamName) {
		boolean result = false;
		if (userDAO.checkIfUserExists(username) && registerDAO
				.checkIfRegisterExists(registerName)) {
			UserEntity user = userDAO.getUserByUsername(username);
			user.setRegister(registerDAO.getRegisterByName(registerName, teamName));
			userDAO.saveOrUpdate(user);
			result = true;
		}
		return result;
	}

	public boolean createNewAppointment(String name, String description,
										String date, String projectName,
										String teamName) {
		boolean result = false;
		ProjectEntity project = projectDAO.getProject(projectName, teamName);
		List<UserEntity> users = project.getUsers();
		if (project != null) {
			AppointmentEntity appointment = new AppointmentEntity(name,
					description, date, project, new ArrayList<UserEntity>());
			for (UserEntity user : users) {
				appointment.addUserAnswer(user.getUsername(),
						StatisticParticipationAnswer.MAYBE);
			}
			appointmentDAO.saveOrUpdate(appointment);
			project.increaseNumberOfAppointments();
			ArrayList<StatisticEntity> statistics
					= new ArrayList<StatisticEntity>(project.getStatistics());
			for (StatisticEntity statistic : statistics) {
				statistic.increaseNumberOfAllAppointments();
			}
			result = true;
			projectDAO.saveOrUpdate(project);
			TeamEntity team = project.getTeam();
			team.addNews(NEW_APPOINTMENT + name + CREATED);
			teamDAO.saveOrUpdate(team);
		}
		return result;
	}

	public AppointmentEntity getAppointment(String projectName,
											long appointmentId, String
													teamName) {
		AppointmentEntity result = null;
		ProjectEntity project = projectDAO.getProject(projectName, teamName);
		if (project != null) {
			List<AppointmentEntity> appointments = project.getAppointments();
			for (AppointmentEntity appointment : appointments) {
				if (appointment.getId() == appointmentId) {
					result = appointment;
				}
			}
		}
		return result;
	}

	public void removeAppointment(String projectName, String teamName,
								  long appointmentId) {
		ProjectEntity project = getProject(projectName, teamName);
		project.decreaseNumberOfAppointments();
		projectDAO.saveOrUpdate(project);
		AppointmentEntity appointment = appointmentDAO.get(appointmentId);
		appointmentDAO.remove(appointment);
	}

	public boolean removeProject(String projectName, String teamName) {
		boolean result = false;
		ProjectEntity project = projectDAO.getProject(projectName, teamName);
		List<StatisticEntity> statistics = project.getStatistics();
		for (StatisticEntity statistic : statistics) {
			deleteStatistic(statistic);
		}
		List<AppointmentEntity> appointments = project.getAppointments();
		deleteAppointments(appointments);
		UserEntity manager = project.getProjectManager();
		if (manager.getRole() != UserRole.ADMINISTRATOR) {
			manager.setRole(UserRole.USER);
		}
		TeamEntity team = teamDAO.getTeamByTeamName(teamName);
		team.getProjects().remove(project);
		teamDAO.saveOrUpdate(team);
		userDAO.saveOrUpdate(manager);
		projectDAO.remove(project);
		return result;
	}

	public void removeRegister(String registerName, String teamName) {
		RegisterEntity register = registerDAO.getRegisterByName(registerName,
				teamName);
		Collection<UserEntity> users = getUsersOfRegister(registerName,
				teamName);
		if (!users.isEmpty()) {
			for (UserEntity user : users) {
				user.setRegister(null);
				userDAO.saveOrUpdate(user);
			}
		}
		TeamEntity team = getTeam(teamName);
		team.addNews(REGISTER + registerName + DELETED);
		teamDAO.saveOrUpdate(team);
		ArrayList<RegisterEntity> registers
				= new ArrayList<RegisterEntity>(team.getRegisters());
		if (team != null && !registers.isEmpty()) {
			for (int i = 0; i < registers.size(); i++) {
				if (registers.get(i).getName().equals(registerName)) {
					registers.remove(i);
				}
			}
			team.setRegisters(registers);
		}
		register.setTeam(null);
		register.setUsers(null);
		registerDAO.saveOrUpdate(register);
		teamDAO.saveOrUpdate(team);
		registerDAO.remove(register);
	}

	private Collection<UserEntity> getUsersOfRegister(String registerName,
													  String teamName) {
		Collection<UserEntity> result = new ArrayList<UserEntity>();
		TeamEntity team = teamDAO.getTeamByTeamName(teamName);
		Collection<RegisterEntity> registers
				= new ArrayList<RegisterEntity>(registerDAO.getRegisters(team));
		for (RegisterEntity register : registers) {
			if (register.getName().equals(registerName)) {
				result = register.getUsers();
			}
		}
		return result;
	}

	public boolean removeTask(String taskName, String teamName) {
		boolean result = false;
		TeamEntity team = teamDAO.getTeamByTeamName(teamName);
		ArrayList<TaskEntity> tasks
				= new ArrayList<TaskEntity>(team.getTasks());
		TaskEntity task = taskDAO.getTaskByTaskName(taskName, team);
		UserEntity user = task.getWorker();
		if (team != null && task != null && (!tasks.isEmpty())) {
			for (int i = 0; i < tasks.size(); i++) {
				if (tasks.get(i).getId() == task.getId()) {
					tasks.remove(i);
				}
			}
		}
		team.setTasks(tasks);
		task.setTeam(null);
		teamDAO.saveOrUpdate(team);
		if (user != null) {
			ArrayList<TaskEntity> usersTasks
					= new ArrayList<TaskEntity>(user.getTasks());
			for (int i = 0; i < usersTasks.size(); i++) {
				if (usersTasks.get(i).getName().equals(taskName)) {
					usersTasks.remove(i);
				}
			}
			user.setTasks(usersTasks);
			userDAO.saveOrUpdate(user);
		}
		task.setWorker(null);
		taskDAO.saveOrUpdate(task);
		taskDAO.remove(task);
		return result;
	}

	public boolean removeTeam(String teamName) {
		boolean result = false;
		TeamEntity team = teamDAO.getTeamByTeamName(teamName);
		if (team != null) {
			ArrayList<UserEntity> users
					= new ArrayList<UserEntity>(getUsersOfTeam(teamName));
			for (UserEntity user : users) {
				if (user.getRole() != UserRole.USER) {
					user.setRole(UserRole.USER);
				}
				user.setTeam(null);
				user.setTasks(null);
				user.setRegister(null);
				user.setDayOfEntry(null);
				user.setAppointmentsTakingPart(null);
				user.setChats(null);
				user.setProjectsTakingPart(null);
				user.setStatistics(null);
				userDAO.saveOrUpdate(user);
			}
		}
		team.setUsers(null);
		teamDAO.saveOrUpdate(team);
		teamDAO.remove(team);
		return result;
	}

	public boolean removeUserFromApp(String username) {
		// TODO dlete user and it's messages
		boolean result = false;
		UserEntity user = userDAO.getUserByUsername(username);
		TeamEntity team = user.getTeam();
		if (user != null && user.getRole() != UserRole.ADMINISTRATOR) {
			user.setStatistics(null);
			user.setProjectsTakingPart(null);
			user.setChats(null);
			user.setAppointmentsTakingPart(null);
			user.setRegister(null);
			user.setTasks(null);
			user.setTeam(null);
			userDAO.saveOrUpdate(user);
			userDAO.remove(user);
		} else if (user != null && user.getRole() == UserRole.ADMINISTRATOR) {
			removeTeam(team.getName());
			user.setTeam(null);
			userDAO.remove(user);
		}
		if (userDAO.getUserByUsername(username) == null) {
			result = true;
		}
		return result;
	}

	public void removeUserFromTeam(String username, String teamName) {
		UserEntity user = userDAO.getUserByUsername(username);
		TeamEntity team = teamDAO.getTeamByTeamName(teamName);
		ProjectEntity deleteProject = null;
		if (user != null) {
			user.setTasks(null);
			user.setRegister(null);
			user.setAppointmentsTakingPart(null);
			user.setChats(null);
			user.setStatistics(null);
			if (user.getRole() == UserRole.ADMINISTRATOR) {
				removeTeam(teamName);
				user.setTeam(null);
			} else if (user.getRole() == UserRole.PROJECT_OWNER) {
				user.setTeam(null);
				for (ProjectEntity project : user.getProjectsTakingPart()) {
					if (project.getProjectManager().getUsername().equals
							(user.getUsername())) {
						deleteProject = project;
					}
				}
				user.setProjectsTakingPart(null);
			}
			if (deleteProject != null) {
				removeProject(deleteProject.getName(), teamName);
			}
			userDAO.saveOrUpdate(user);
		}
	}

	public boolean removeUserFromProject(String username, String projectName,
										 String teamName) {
		boolean result = false;
		UserEntity user = userDAO.getUserByUsername(username);
		ProjectEntity project = projectDAO.getProject(projectName, teamName);
		UserEntity manager = project.getProjectManager();
		ArrayList<UserEntity> usersTakingPart
				= new ArrayList<>(project.getUsers());
		ArrayList<ProjectEntity> projectsTakingPart
				= new ArrayList<>(user.getProjectsTakingPart());
		if (user != null && project != null && usersTakingPart.size() != 0
				&& projectsTakingPart.size() != 0 && manager != null) {
			if (manager.getUsername().equals(user.getUsername())
					&& user.getRole() == UserRole.PROJECT_OWNER) {
				removeProject(projectName, teamName);
			} else if (user.getRole() == UserRole.USER
					|| user.getRole() == UserRole.ADMINISTRATOR) {
				for (int i = 0; i < usersTakingPart.size(); i++) {
					if (usersTakingPart.get(i).getUsername()
							.equals(user.getUsername())) {
						usersTakingPart.remove(i);
					}
				}
				for (int i = 0; i < projectsTakingPart.size(); i++) {
					if (projectsTakingPart.get(i).getId()
							== project.getId()) {
						List<AppointmentEntity> appointments
								= project.getAppointments();
						for (AppointmentEntity appointment : appointments) {
							appointment.removeUserFromUserAnswer(username);
							appointmentDAO.saveOrUpdate(appointment);
						}
						projectsTakingPart.remove(i);
					}
				}
				project.setUsers(usersTakingPart);
				user.setProjectsTakingPart(projectsTakingPart);
				projectDAO.saveOrUpdate(project);
				userDAO.saveOrUpdate(user);
				result = true;
			}
		}
		return result;
	}

	public List<MessageEntity> getMessagesOfChat(long id) {
		List<MessageEntity> result = new ArrayList<MessageEntity>();
		ChatEntity chat = chatDAO.get(id);
		if (chat != null) {
			result = chat.getMessages();
		}
		return result;
	}

	public boolean addUserToTeam(String teamName, String username) {
		boolean result = false;
		TeamEntity team = teamDAO.getTeamByTeamName(teamName);
		UserEntity user = userDAO.getUserByUsername(username);
		if (user != null && team != null) {
			user.setTeam(team);
			userDAO.saveOrUpdate(user);
			result = true;
		}
		return result;
	}

	public boolean addUserToProject(String username, String projectName,
									String teamName) {
		boolean result = false;
		UserEntity user = userDAO.getUserByUsername(username);
		TeamEntity team = teamDAO.getTeamByTeamName(teamName);
		ProjectEntity project = projectDAO.getProject(projectName, teamName);
		List<AppointmentEntity> appointments = project.getAppointments();
		TeamEntity usersTeam = user.getTeam();
		if (user != null && team != null && project != null
				&& usersTeam.getName().equals(team.getName())) {
			StatisticEntity newStatistic = new StatisticEntity(user, project);
			user.getStatistics().add(newStatistic);
			user.getProjectsTakingPart().add(project);
			userDAO.saveOrUpdate(user);
			project.getUsers().add(user);
			projectDAO.saveOrUpdate(project);
			for (AppointmentEntity appointment : appointments) {
				appointment.addUserAnswer(username,
						StatisticParticipationAnswer.MAYBE);
			}
			result = true;
		}

		return result;
	}

	public TeamEntity getTeam(String teamName) {
		TeamEntity result = teamDAO.getTeamByTeamName(teamName);
		return result;
	}

	public ProjectEntity getProject(String projectName, String teamName) {
		ProjectEntity project = projectDAO.getProject(projectName, teamName);
		return project;
	}

	public boolean addUserToAppointment(String userName, String projectName,
										String teamName, long appointmentId) {
		boolean result = false;
		UserEntity user = userDAO.getUserByUsername(userName);
		ProjectEntity project = projectDAO.getProject(projectName, teamName);
		AppointmentEntity appointment = appointmentDAO.get(appointmentId);
		if (user != null) {
			user.getAppointmentsTakingPart().add(appointment);
			userDAO.saveOrUpdate(user);
		}
		return result;
	}

	public Collection<UserEntity> getAllUsers() {
		return userDAO.getAll();
	}

	public Collection<TeamEntity> getAllTeams() {
		return teamDAO.getAll();
	}

	public boolean editTeam(TeamEntity team, String teamName,
							String teamDescription) {
		boolean result = false;
		TeamEntity fetchedTeam = teamDAO.get(team.getId());
		if (fetchedTeam != null) {
			fetchedTeam.setName(teamName);
			fetchedTeam.setDescription(teamDescription);
			teamDAO.saveOrUpdate(fetchedTeam);
			result = true;
		}
		return result;
	}

	public boolean editUser(UserEntity toEdit, String firstName, String surname,
							String address, String phoneNr, String email,
							String birthday) {
		boolean result = false;
		UserEntity fetchedUser = userDAO.get(toEdit.getId());
		if (fetchedUser != null) {
			toEdit.setFirstName(firstName);
			toEdit.setSurname(surname);
			toEdit.setAddress(address);
			toEdit.setPhoneNr(phoneNr);
			toEdit.setEmail(email);
			toEdit.setBirthday(birthday);
			userDAO.saveOrUpdate(toEdit);
			result = true;
		}
		return result;
	}

	public boolean addInvitationToUser(UserEntity user, TeamEntity team) {
		if (user != null && team != null) {
			String teamName = team.getName();
			user.addInvitation(teamName);
			userDAO.saveOrUpdate(user);
			return true;
		} else {
			return false;
		}
	}

	public boolean removeInvitationFromUser(UserEntity user, TeamEntity team) {
		boolean result;
		if (user != null && team != null) {
			result = user.removeInvitation(team.getName());
		} else {
			result = false;
		}
		return result;
	}

	public boolean addRequestToTeam(TeamEntity team, UserEntity user) {
		if (team != null && user != null) {
			String username = user.getUsername();
			team.addRequestOfUser(username);
			teamDAO.saveOrUpdate(team);
			return true;
		} else {
			return false;
		}
	}

	public boolean removeRequestOfTeam(TeamEntity team, UserEntity user) {
		boolean result;
		if (team != null && user != null) {
			result = team.removeRequestOfUser(user.getUsername());
		} else {
			result = false;
		}
		return result;
	}

	public void saveUser(UserEntity user) {
		userDAO.saveOrUpdate(user);
	}

	public void saveTeam(TeamEntity team) {
		teamDAO.saveOrUpdate(team);
	}

	public void saveRegister(RegisterEntity register) {
		registerDAO.saveOrUpdate(register);
	}

	public void deleteRegister(RegisterEntity register) {
		registerDAO.remove(register);
	}

	public void deleteUser(UserEntity user) {
		UserRole role = user.getRole();
		switch (role) {
			case USER:
				removeDependenciesFromUser(user);
				userDAO.remove(user);
				break;
			case PROJECT_OWNER:
				deleteProjectOwner(user);
				break;
			case ADMINISTRATOR:
				deleteAdmin(user);
				break;
		}
	}

	private void deleteProjectOwner(UserEntity user) {
		ProjectEntity projectEntity = user.getAdminOfProject();
		List<UserEntity> usersTakingPart = projectEntity.getUsers();
		for (UserEntity tempUser : usersTakingPart) {
			tempUser.getProjectsTakingPart().remove(projectEntity);
			userDAO.saveOrUpdate(tempUser);
		}
		user.setAdminOfProject(null);
		user.setTeam(null);
		user.setAppointmentsTakingPart(null);
		user.setChats(null);
		user.setProjectsTakingPart(null);
		user.setRegister(null);
		user.setTasks(null);
		userDAO.saveOrUpdate(user);
		projectDAO.remove(projectEntity);
		userDAO.removeUser(user.getId());
	}

	private void deleteAdmin(UserEntity user) {
		TeamEntity team = user.getTeam();
		for (UserEntity teamMember : team.getUsers()) {
			teamMember.setAdminOfProject(null);
			teamMember.setTeam(null);
			teamMember.setAppointmentsTakingPart(new ArrayList<>());
			teamMember.setChats(new ArrayList<>());
			teamMember.setProjectsTakingPart(new ArrayList<>());
			teamMember.setRegister(null);
			teamMember.setTasks(new ArrayList<>());
			teamMember.setRole(UserRole.USER);
			userDAO.saveOrUpdate(teamMember);
		}
		List<ProjectEntity> projects = team.getProjects();
		for (ProjectEntity project : projects) {
			List<AppointmentEntity> appointments = project.getAppointments();
			deleteAppointments(appointments);
			project.setUsers(new ArrayList<>());
			project.setAppointments(new ArrayList<>());
			project.setProjectManager(null);
			projectDAO.saveOrUpdate(project);
			projectDAO.remove(project);
		}
		team.setUsers(null);
		team.setTasks(null);
		team.setProjects(null);
		team.setChats(null);
		teamDAO.saveOrUpdate(team);
		teamDAO.remove(team);
		userDAO.removeUser(user.getId());
	}

	private void deleteAppointments(List<AppointmentEntity> appointments) {
		for (AppointmentEntity appointment : appointments) {
			appointment.setUserTakinPart(new ArrayList<>());
			appointment.setProject(null);
			appointmentDAO.saveOrUpdate(appointment);
			appointmentDAO.remove(appointment);
		}
	}

	public void deleteTeam(TeamEntity team) {
		List<UserEntity> teamMembers = team.getUsers();
		for (UserEntity user : teamMembers) {
			removeDependenciesFromUser(user);
			userDAO.saveOrUpdate(user);
		}
		team.setUsers(new ArrayList<>());
		teamDAO.saveOrUpdate(team);
		teamDAO.remove(team);
	}

	private void removeDependenciesFromUser(UserEntity user) {
		user.setRole(UserRole.USER);
		user.setTasks(new ArrayList<>());
		user.setRegister(null);
		user.setProjectsTakingPart(new ArrayList<>());
		user.setChats(new ArrayList<>());
		user.setAppointmentsTakingPart(new ArrayList<>());
		user.setAdminOfProject(null);
		user.setDayOfEntry(null);
		user.setStatistics(new ArrayList<>());
		user.setTributes(null);
		user.setTeam(null);
		user.setChats(new ArrayList<>());
	}

	public boolean leaveTeam(UserEntity user, TeamEntity team) {
		boolean result = false;
		if (user.getRole() != UserRole.ADMINISTRATOR) {
			if (user.getRole() == UserRole.PROJECT_OWNER) {
				ProjectEntity project = user.getAdminOfProject();
				deleteProject(project);
				removeDependenciesFromUser(user);
				List<ChatEntity> chats = user.getChats();
				for (ChatEntity chat : chats) {
					deleteChat(chat);
				}
				userDAO.saveOrUpdate(user);
				result = true;
			} else {
				List<UserEntity> teamMembers = team.getUsers();
				teamMembers.remove(user);
				teamDAO.saveOrUpdate(team);
				removeDependenciesFromUser(user);
				List<ChatEntity> chats = user.getChats();
				for (ChatEntity chat : chats) {
					deleteChat(chat);
				}
				userDAO.saveOrUpdate(user);
				userDAO.saveOrUpdate(user);
				result = true;
			}
		}
		return result;
	}

	public void deleteProject(ProjectEntity project) {
		List<AppointmentEntity> appointments
				= project.getAppointments();
		List<StatisticEntity> statistics = project.getStatistics();
		List<UserEntity> users = project.getUsers();
		TeamEntity team = project.getTeam();
		project.setStatistics(new ArrayList<>());
		project.setAppointments(new ArrayList<>());
		projectDAO.saveOrUpdate(project);
		for (UserEntity user : users) {
			user.getStatistics().removeAll(statistics);
			user.getAppointmentsTakingPart().removeAll(appointments);
			user.getProjectsTakingPart().remove(project);
			if (user.getUsername()
					.equals(project.getProjectManager().getUsername())) {
				user.setAdminOfProject(null);
			}
			userDAO.saveOrUpdate(user);
		}
		deleteStatistics(statistics);
		deleteAppointments(appointments);
		project.setProjectManager(null);
		projectDAO.saveOrUpdate(project);
		projectDAO.remove(project);
		team.addNews(PROJECT + project.getName() + DELETED);
		teamDAO.saveOrUpdate(team);
	}

	private void deleteStatistics(List<StatisticEntity> statistics) {
		for (StatisticEntity statistic : statistics) {
			deleteStatistic(statistic);
		}
	}

	private void deleteStatistic(StatisticEntity statistic) {
		statistic.setUser(null);
		statistic.setProject(null);
		statisticDAO.saveOrUpdate(statistic);
		statisticDAO.remove(statistic);
	}

	public void editProject(ProjectEntity project, String description,
							String deadline) {
		List<AppointmentEntity> appointments = project.getAppointments();
		for (AppointmentEntity appointment : appointments) {
			if (appointment.getIsDeadline()) {
				appointment.setDeadline(deadline);
				appointmentDAO.saveOrUpdate(appointment);
				break;
			}
		}
		project.setAppointments(appointments);
		project.setDescription(description);
		project.setDeadline(deadline);
		projectDAO.saveOrUpdate(project);
	}

	public void editProjectMembership(ProjectEntity project,
									  ArrayList<String> usersToEdit) {
		List<UserEntity> projectMembers = project.getUsers();
		List<String> usersToRemove = new ArrayList<>();
		for (UserEntity user : projectMembers) {
			for (String username : usersToEdit) {
				if (user.getUsername().equals(username)) {
					UserEntity toRemove = getUser(username);
					projectMembers.remove(toRemove);
					removeUserFromProject(username, project.getName(),
							project.getTeam().getName());
					usersToRemove.add(username);
				}
			}
		}
		for (String username : usersToRemove) {
			usersToEdit.remove(username);
		}
		addUsersToProject(usersToEdit, project);
		removeUsersFromProject(usersToRemove, project);
		removeStatisticsOfRemovedUsers(usersToRemove, project);
		project = projectDAO.get(project.getId());
		project.setUsers(projectMembers);
		List<AppointmentEntity> appointments = project.getAppointments();
		for (AppointmentEntity appointment : appointments) {
			for (String username : usersToEdit) {
				appointment.addUserAnswer(username,
						StatisticParticipationAnswer.MAYBE);
			}
			for (String username : usersToRemove) {
				appointment.removeUserFromUserAnswer(username);
			}
			appointmentDAO.saveOrUpdate(appointment);
		}
		projectDAO.saveOrUpdate(project);
	}


	private void removeStatisticsOfRemovedUsers(List<String> usersToRemove,
												ProjectEntity project) {
		for (String username : usersToRemove) {
			UserEntity user = getUser(username);
			List<StatisticEntity> statistics = user.getStatistics();
			for (StatisticEntity statistic : statistics) {
				if (statistic.getProject().getId() == project.getId()) {
					statistic.setUser(null);
					statistic.setProject(null);
					project.getStatistics().remove(statistic);
					statistics.remove(statistic);
					user.setStatistics(statistics);
					userDAO.saveOrUpdate(user);
					projectDAO.saveOrUpdate(project);
					statisticDAO.saveOrUpdate(statistic);
					statisticDAO.remove(statistic);
					break;
				}
			}
		}
	}

	private void addUsersToProject(List<String> usersToEdit,
								   ProjectEntity project) {
		for (String username : usersToEdit) {
			UserEntity user = getUser(username);
			StatisticEntity newStatistic = new StatisticEntity();
			newStatistic.setUser(user);
			newStatistic.setProject(project);
			newStatistic.setNumberOfAllAppointments
					(project.getNumberOfAppointments());
			statisticDAO.saveOrUpdate(newStatistic);
			project.getStatistics().add(newStatistic);
			user.getProjectsTakingPart().add(project);
			user.getStatistics().add(newStatistic);
			userDAO.saveOrUpdate(user);
		}
	}

	private void removeUsersFromProject(List<String> usersToRemove,
										ProjectEntity project) {
		for (String username : usersToRemove) {
			UserEntity user = getUser(username);
			user.getProjectsTakingPart().remove(project);
			userDAO.saveOrUpdate(user);
		}
	}

	public TaskEntity getTask(long id) {
		TaskEntity task = taskDAO.get(id);
		return task;
	}

	public void editTask(TaskEntity task, String description,
						 String deadline) {
		task.setDeadline(deadline);
		task.setDescription(description);
		taskDAO.saveOrUpdate(task);
	}

	public void deleteTask(TaskEntity task) {
		TeamEntity team = task.getTeam();
		List<TaskEntity> tasksOfTeam = team.getTasks();
		tasksOfTeam.remove(task);
		team.setTasks(tasksOfTeam);
		UserEntity worker = task.getWorker();
		List<TaskEntity> tasksOfWorker = worker.getTasks();
		tasksOfWorker.remove(task);
		worker.setTasks(tasksOfWorker);
		task.setTeam(null);
		task.setWorker(null);
		userDAO.saveOrUpdate(worker);
		team.addNews(TASK + task.getName() + DELETED);
		teamDAO.saveOrUpdate(team);
		taskDAO.saveOrUpdate(task);
		taskDAO.remove(task);
	}

	public void editAppointment(AppointmentEntity appointment,
								String appointmentName,
								String appointmentDescription, String deadline) {
		appointment.setName(appointmentName);
		appointment.setDescription(appointmentDescription);
		appointment.setDeadline(deadline);
		appointmentDAO.saveOrUpdate(appointment);
	}

	public void deleteAppointment(AppointmentEntity appointment) {
		List<UserEntity> usersOfAppointment = appointment.getUserTakinPart();
		ProjectEntity project = appointment.getProject();
		TeamEntity team = project.getTeam();
		team.addNews(APPOINTMENT + appointment.getName() + DELETED);
		teamDAO.saveOrUpdate(team);
		List<StatisticEntity> statistics = project.getStatistics();
		for (StatisticEntity statistic : statistics) {
			statistic.decreaseNumberOfAllAppointments();
			statisticDAO.saveOrUpdate(statistic);
		}
		project.getAppointments().remove(appointment);
		project.decreaseNumberOfAppointments();
		projectDAO.saveOrUpdate(project);
		for (UserEntity user : usersOfAppointment) {
			user.getAppointmentsTakingPart().remove(appointment);
			userDAO.saveOrUpdate(user);
		}
		appointment.setProject(null);
		appointment.setUserTakinPart(new ArrayList<>());
		appointmentDAO.saveOrUpdate(appointment);
		appointmentDAO.remove(appointment);
	}

	public AppointmentEntity getAppointment(long id) {
		return appointmentDAO.get(id);
	}

	public boolean isUserTakingPartAtProject(UserEntity user,
											 ProjectEntity project) {
		boolean result = false;
		List<UserEntity> users = project.getUsers();
		for (UserEntity tempUser : users) {
			if (tempUser.getId() == user.getId()) {
				result = true;
				break;
			}
		}
		return result;
	}

	public boolean saveAnswerParticipation(UserEntity user,
										   AppointmentEntity appointment,
										   StatisticParticipationAnswer answer) {
		boolean result = false;
		List<StatisticEntity> statistics = user.getStatistics();
		StatisticEntity statistic = null;
		for (StatisticEntity tempStatistic : statistics) {
			if (tempStatistic.getProject().getId()
					== appointment.getProject().getId()) {
				statistic = tempStatistic;
				break;
			}
		}
		if (statistic != null) {
			if (appointment.getUserAnswers().get(user.getUsername())
					!= answer) {
				appointment.addUserAnswer(user.getUsername(), answer);
				if (answer == StatisticParticipationAnswer.YES) {
					appointment.getUserTakinPart().add(user);
					user.getAppointmentsTakingPart().add(appointment);
					statistic.increaseNumberOfParticipation();
					statisticDAO.saveOrUpdate(statistic);
					userDAO.saveOrUpdate(user);
				} else {
					appointment.getUserTakinPart().remove(user);
					user.getAppointmentsTakingPart().remove(appointment);
					statistic.decreaseNumberOfParticipation();
					statisticDAO.saveOrUpdate(statistic);
					userDAO.saveOrUpdate(user);
				}
			}
			appointmentDAO.saveOrUpdate(appointment);
			result = true;
		}
		return result;
	}

	public void changePasswordOfUser(UserEntity user, String newPassword) {
		user.setPassword(newPassword);
		userDAO.saveOrUpdate(user);
	}

	public void createNewChat(TeamEntity team,
							  ArrayList<UserEntity> usersOfChat,
							  String chatName) {
		ChatEntity newChat = new ChatEntity();
		newChat.setTeam(team);
		newChat.setName(chatName);
		if (usersOfChat.size() == 2) {
			newChat.setIsSoloChat(true);
		} else {
			newChat.setIsSoloChat(false);
		}
		newChat.setUsers(usersOfChat);
		chatDAO.saveOrUpdate(newChat);
		team.getChats().add(newChat);
		teamDAO.saveOrUpdate(team);
		for (int i = 0; i < usersOfChat.size(); i++) {
			UserEntity user = usersOfChat.get(i);
			user.getChats().add(newChat);
			userDAO.saveOrUpdate(user);
		}

	}

	public ChatEntity getChatByName(TeamEntity team, String chatName) {
		return chatDAO.getChatByName(team, chatName);
	}

	public void createNewMessage(String message, UserEntity author,
								 ChatEntity chat, String timestamp)
			throws ParseException {
		SimpleDateFormat formatter
				= new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		Calendar timeOfMessage = Calendar.getInstance();
		timeOfMessage.setTime(formatter.parse(timestamp));
		MessageEntity messageEntity = new MessageEntity(message, timestamp,
				author, chat);
		messageDAO.saveOrUpdate(messageEntity);
		// Add new messages at front of list
		chat.getMessages().add(0, messageEntity);
		chatDAO.saveOrUpdate(chat);
		author.setLastCheckedMessages(timeOfMessage);
		userDAO.saveOrUpdate(author);
	}

	public ChatEntity getChat(long chatId) {
		return chatDAO.get(chatId);
	}

	public void deleteChat(ChatEntity chat) {
		List<MessageEntity> messages = chat.getMessages();
		List<UserEntity> users = chat.getUsers();
		TeamEntity team = chat.getTeam();
		for (UserEntity user : users) {
			user.getChats().remove(chat);
			userDAO.saveOrUpdate(user);
		}
		for (MessageEntity message : messages) {
			message.setChat(null);
			message.setAuthor(null);
			messageDAO.saveOrUpdate(message);
			messageDAO.remove(message);
		}
		team.getChats().remove(chat);
		teamDAO.saveOrUpdate(team);
		chat.setTeam(null);
		chat.setUsers(new ArrayList<>());
		chat.setMessages(new ArrayList<>());
		chatDAO.saveOrUpdate(chat);
		chatDAO.remove(chat);
	}
}

