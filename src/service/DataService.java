package service;

/**
 * Created by Raphael on 14.06.2017.
 */

import dao.*;
import entity.*;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


// TODO: Proof at every method if user is allowed to perform operation.

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
	private SimpleDateFormat formatter;

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
			statisticOfUser.increaseNumberOfParticipiation();
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
			project.getStatistics().add(newStatistic);
			projectDAO.saveOrUpdate(project);
			if (manager.getRole() != UserRole.ADMINISTRATOR) {
				manager.setRole(UserRole.PROJECT_OWNER);
				manager.setAdminOfProject(project);
			}
			manager.getProjectsTakingPart().add(project);
			manager.getStatistics().add(newStatistic);
			userDAO.saveOrUpdate(manager);
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

	public boolean createNewChat(String name, Collection<String> users,
								 String teamName, String creatorName) {
		TeamEntity team = teamDAO.getTeamByTeamName(teamName);
		UserEntity creator = userDAO.getUserByUsername(creatorName);
		if (team != null && creator != null) {
			ChatEntity newChat = new ChatEntity(name, new ArrayList<UserEntity>(),
					new ArrayList<MessageEntity>(), team, creator);
			team.getChats().add(newChat);
			chatDAO.saveOrUpdate(newChat);
			teamDAO.saveOrUpdate(team);
			ChatEntity chat = chatDAO.getChatByChatName(name, team, creator);
			addUsersToChat(chat, users);
		} else {
			return false;
		}
		return true;
	}

	private void addUsersToChat(ChatEntity chat, Collection<String> users) {
		for (String username : users) {
			UserEntity user = getUser(username);
			user.getChats().add(chat);
			userDAO.saveOrUpdate(user);
		}
	}

	public boolean createNewTask(String name, String description,
								 String date, String teamName) {
		boolean result = false;
		TeamEntity team = teamDAO.getTeamByTeamName(teamName);
		if (team != null) {
			TaskEntity task = taskDAO.getTaskByTaskName(name, team);
			if (task == null) {
				task = new TaskEntity(name, description, date, team);
				taskDAO.saveOrUpdate(task);
				result = true;
			}
		}
		return result;
	}

	public boolean createNewMessage(String message, String date,
									String userName, String chatName,
									String teamName, String creatorOfChat) {
		boolean result = false;
		TeamEntity team = getTeam(teamName);
		UserEntity creator = getUser(creatorOfChat);
		ChatEntity fetchedChat;
		UserEntity author;
		if (team != null && creator != null) {
			fetchedChat
					= chatDAO.getChatByChatName(chatName, team, creator);
			author = userDAO.getUserByUsername(userName);
			if (fetchedChat != null && author != null) {
				MessageEntity newMessage
						= new MessageEntity(message, date, author,
						fetchedChat);
				messageDAO.saveOrUpdate(newMessage);
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

	public boolean createNewRegister(String registerName, String teamName) {
		boolean result = false;
		RegisterEntity register = registerDAO.getRegisterByName(registerName,
				teamName);
		TeamEntity team = teamDAO.getTeamByTeamName(teamName);
		if (register == null && team != null) {
			register = new RegisterEntity(registerName, new ArrayList<UserEntity>(), team);
			registerDAO.saveOrUpdate(register);
			result = true;
		}
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
		if (project != null) {
			AppointmentEntity appointment = new AppointmentEntity(name,
					description, date, project, new ArrayList<UserEntity>());
			appointmentDAO.saveOrUpdate(appointment);
			project.increaseNumberOfAppointments();
			ArrayList<StatisticEntity> statistics
					= new ArrayList<StatisticEntity>(project.getStatistics());
			for (StatisticEntity statistic : statistics) {
				statistic.increaseNumberOfAllAppointments();
			}
			result = true;
			projectDAO.saveOrUpdate(project);
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

	public void removeChat(String chatName, String teamName,
						   String creatorName) {
		ChatEntity chat = getChat(chatName, teamName, creatorName);
		if (chat != null) {
			TeamEntity team = getTeam(teamName);
			ArrayList<UserEntity> users
					= new ArrayList<UserEntity>(chat.getUsers());
			if (team != null && users != null) {
				for (UserEntity user : users) {
					user.getChats().remove(chat);
					userDAO.saveOrUpdate(user);
				}
			}
			chatDAO.remove(chat);
		}
	}

	public boolean removeProject(String projectName, String teamName) {
		boolean result = false;
		ProjectEntity project = projectDAO.getProject(projectName, teamName);
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
		boolean result = false;
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
				= new ArrayList<UserEntity>(project.getUsers());
		ArrayList<ProjectEntity> projectsTakingPart
				= new ArrayList<ProjectEntity>(user.getProjectsTakingPart());
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

	// TODO set value of dayOfEntry
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
		TeamEntity usersTeam = user.getTeam();
		if (user != null && team != null && project != null
				&& usersTeam.getName().equals(team.getName())) {
			StatisticEntity newStatistic = new StatisticEntity(user, project);
			user.getStatistics().add(newStatistic);
			user.getProjectsTakingPart().add(project);
			userDAO.saveOrUpdate(user);
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

	public ChatEntity getChat(String chatName, String teamName,
							  String creatorName) {
		TeamEntity team = getTeam(teamName);
		UserEntity creator = getUser(creatorName);
		ChatEntity result = null;
		if (team != null && creator != null) {
			result = chatDAO.getChatByChatName(chatName, team, creator);
		}
		return result;
	}

	public Collection<UserEntity> getAllUsers() {
		return userDAO.getAll();
	}

	public Collection<TeamEntity> getAllTeams() {
		return teamDAO.getAll();
	}

	public TeamEntity editTeam(TeamEntity team, String teamName,
							String teamDescription) {
		boolean result = false;
		TeamEntity fetchedTeam = teamDAO.get(team.getId());
		if (fetchedTeam != null) {
			fetchedTeam.setName(teamName);
			fetchedTeam.setDescription(teamDescription);
			teamDAO.saveOrUpdate(fetchedTeam);
		}
		return fetchedTeam;
	}
}

