package service;


import dao.*;
import entity.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * An instance of this class is used to connect the RESTService object with
 * the database. It calls the specific methods in the DAOs and gets the data
 * from the database. It edits the data if needed and returns it to the
 * RESTService object.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since version 1.0
 */
public class DataService {
	// The format of a date used in this system.
	protected static final String DATE_FORMAT = "dd.MM.yyyy hh:mm:ss";

	// The DAO objects needed for interaction with the database.
	private AppointmentDAO appointmentDAO;
	private ChatDAO chatDAO;
	private MessageDAO messageDAO;
	private ProjectDAO projectDAO;
	private RegisterDAO registerDAO;
	private StatisticDAO statisticDAO;
	private TaskDAO taskDAO;
	private TeamDAO teamDAO;
	private UserDAO userDAO;

	private SimpleDateFormat formatter;
	// Text fragemnts used to create the information texts for the clients.
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

	/**
	 * Creates a new object of DataService with all needed DAOs initiated.
	 */
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
	}

	/**
	 * Returns a user from the database.
	 *
	 * @param username The username of the user entry.
	 *
	 * @return The specific UserEntity database entry or null if the user
	 * with the username does not exist.
	 */
	public UserEntity getUser(String username) {
		UserEntity result = userDAO.getUserByUsername(username);
		return result;
	}

	/**
	 * Gets all users belonging to a specific team from the database.
	 *
	 * @param teamName The name of the team the users are members of.
	 *
	 * @return A {@see Collection} of all UserEntity entries belonging to a
	 * specific TeamEntity or null if the TeamEntity does not exist.
	 */
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

	/**
	 * Validates the login data of a user.
	 *
	 * @param username The username to login.
	 * @param password The password of the user.
	 *
	 * @return Returns true, if there exists a UserEntity database entry with
	 * the username and password as sent and false if the data is no correct.
	 */
	public boolean login(String username, String password) {
		UserEntity userToLogin = userDAO.getUserByUsername(username);
		return userToLogin != null
				&& userToLogin.getPassword().equals(password);
	}

	/**
	 * Returns the participation statistic of a user belonging to a
	 * specific team and project from the database.
	 *
	 * @param username The username of the user the statistic belong to.
	 * @param projectName The name of the project the participation
	 *                       statistic belong to.
	 * @param teamName The name of the team the user is member of.
	 *
	 * @return A StatisticEntity instance or null if the sent data is not
	 * existing in the database.
	 */
	public StatisticEntity getStatisticOfUser(String username,
											  String projectName,
											  String teamName) {
		StatisticEntity result;
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
		return null;
	}

	/**
	 * Returns all participation statistics of the users who take part at a
	 * specific project.
	 *
	 * @param projectName The name of the project of which the statistics
	 *                       should get returned.
	 * @param teamName The name of the team the project belongs to.
	 *
	 * @return A {@see Collection} of all StatisticEntity entries belonging
	 * to a specific TeamEntity and ProjectEntity or null if the data does
	 * not exist.
	 */
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

	/**
	 * Returns a specific task which exists in a team.
	 *
	 * @param taskName The name of the task which should be loaded.
	 * @param teamName The name of the team in which teh task exists.
	 *
	 * @return A TaskEntity object or null if the data does not exist in the
	 * database.
	 */
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

	/**
	 * Returns a register of a team from the database.
	 *
	 * @param registerName The name of the register which should get returned.
	 * @param teamName The name of the team the register belongs to.
	 *
	 * @return A RegisterEntity object or null if the requested data does not
	 * exist.
	 */
	public RegisterEntity getRegister(String registerName, String teamName) {
		RegisterEntity result;
		result = registerDAO.getRegisterByName(registerName, teamName);
		return result;
	}

	/**
	 * Creates a new user in the system.
	 *
	 * @param username The username of the new user.
	 * @param password The password of the new user.
	 * @param firstName The first name of the new user.
	 * @param surname The surname of the new user.
	 * @param email The email address of the new user.
	 * @param phonNr The phone number of the new user.
	 * @param address The address of the new user.
	 * @param birthday The birthday of the user.
	 *
	 * @return If there is already a UserEntity entry with the same username,
	 * the action will not be performed and it returns false. Else it returns
	 * true after creating the new UserEntity entry.
	 */
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

	/**
	 * Creates a new team in the system.
	 *
	 * @param name The name of the new team.
	 * @param description The description of the new team.
	 * @param adminName The username of the administrator of the new team.
	 *
	 * @return Returns false, if the action could not be finished and true if
	 * it was done successfully.
	 */
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

	/**
	 * Edits the participation statistics of a user who will take part at a
	 * certain appointment of a project.
	 *
	 * @param teamName The name of the team the user is member of.
	 * @param projectName The name of the project the appointment is
	 *                       belonging to.
	 * @param appointmentId The identifier of the appointment the user will
	 *                         take part.
	 * @param username The username of the user who will take part at the
	 *                    appointment.
	 */
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

	/**
	 * Creates a new project in a team.
	 *
	 * @param teamName The name of the team the new project should belong to.
	 * @param projectName The name of the new project.
	 * @param description The description of thew new project.
	 * @param projectManager The username of the manager of the new project.
	 * @param deadline The deadline the new project will end.
	 *
	 * @return Returns true if the project has been created successfully and
	 * false if the action could not be finished.
	 */
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

	/**
	 * Creates a new task in a team environment.
	 *
	 * @param name The name of the new task.
	 * @param description The description of the new task.
	 * @param date The deadline of the new task.
	 * @param teamName The name of the team the task belongs to.
	 * @param username The username of the task's worker.
	 *
	 * @return Returns true if the action could be performed successfully and
	 * false if the task could not be created.
	 */
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
				task = new TaskEntity(name, description, date, user, team);
				taskDAO.saveOrUpdate(task);
				userDAO.saveOrUpdate(user);
				team.addNews(NEW_TASK + name + CREATED);
				teamDAO.saveOrUpdate(team);
				result = true;
			}
		}
		return result;
	}

	/**
	 * Sets a user as the worker of a specific task.
	 *
	 * @param taskName The name of the task.
	 * @param userName The username of the user.
	 * @param teamName The name of the team the task and the user belong to.
	 *
	 * @return Returns true if the action could be performed successfully and
	 * false if not.
	 */
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

	/**
	 * Creates a new register of a team.
	 *
	 * @param registerName The name of the new register.
	 * @param teamName The name of the team the register should belong to.
	 * @param color The color of the register as a hexadecimal code.
	 *
	 * @return Returns true if the action could be performed successfully and
	 * false if the action could not be completed.
	 */
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

	/**
	 * Adds a user to a existing register of his or her team.
	 *
	 * @param username The username of the user.
	 * @param registerName The name of the register the user should be part of.
	 * @param teamName The name of the team the user and register belong to.
	 *
	 * @return Returns true if the action could be completed successfully and
	 * false if the action could not be completed.
	 */
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

	/**
	 * Creates a new appointment of a project.
	 *
	 * @param name The name of the new appointment.
	 * @param description The description of the new appointment.
	 * @param date The deadline of the new appointment.
	 * @param projectName The name of the project the new appointment will
	 *                       belong to.
	 * @param teamName The name of the team the user, project and appointment
	 *                   belong to.
	 *
	 * @return Returns true if the action could be completed successfully and
	 * false if not.
	 */
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

	/**
	 * Returns an appointment from the database.
	 *
	 * @param projectName The name of the project the appointment belongs to.
	 * @param appointmentId The identifier of the appointment.
	 * @param teamName The name of the team the project and appointment
	 *                    belong to.
	 *
	 * @return An AppointmentEntity object or null if the data does not exist.
	 */
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

	/**
	 * Deletes an appointment from the database.
	 *
	 * @param projectName The name of the project the appointment belongs to.
	 * @param teamName The name of the team the project and appointment
	 *                    belong to.
	 * @param appointmentId The identifier of the appointment to delete.
	 */
	public void removeAppointment(String projectName, String teamName,
								  long appointmentId) {
		ProjectEntity project = getProject(projectName, teamName);
		project.decreaseNumberOfAppointments();
		projectDAO.saveOrUpdate(project);
		AppointmentEntity appointment = appointmentDAO.get(appointmentId);
		appointmentDAO.remove(appointment);
	}

	/**
	 * Removes a project from the database.
	 *
	 * @param projectName The name of the project to delete.
	 * @param teamName The name of the team the project belongs to.
	 *
	 * @return Returns true if the action could be performed successfully and
	 * false if not.
	 */
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

	/**
	 * Deletes a register from the database.
	 *
	 * @param registerName The name of the register to delete.
	 * @param teamName The name of teh team the register belongs to.
	 */
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

	/*
	Returns a {@see Collection} of the users belonging to a specific register
	 of a team.
	 */
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

	/**
	 * Deletes a task from the database.
	 *
	 * @param taskName The name of the task to delete.
	 * @param teamName The name of the team the task belongs to.
	 *
	 * @return Returns true if the action could be completed successfully and
	 * false if not.
	 */
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

	/**
	 * Deletes a team from the database.
	 *
	 * @param teamName The name of the team to delete.
	 *
	 * @return Returns true, if the action could be performed successfully
	 * and false if not.
	 */
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

	/**
	 * Deletes a user from the database.
	 *
	 * @param username The username of the user to delete.
	 *
	 * @return Returns true if the action could be performed successfully and
	 * false if not.
	 */
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

	/**
	 * Removes a user from a team and all other entities inside of the team
	 * environment.
	 *
	 * @param username The username of the user to remove from the team.
	 * @param teamName The naem of the team the user should be removed from.
	 */
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

	/**
	 * Removes a user from a project and the appointments belonging to the
	 * project inside of a team environment.
	 *
	 * @param username The username of the user who should be removed.
	 * @param projectName The name of the project the user will be removed from.
	 * @param teamName The name of the team the user and the project belong to.
	 *
	 * @return Returns true if the action could be performed successfully and
	 * false if not.
	 */
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

	/**
	 * Adds a user to a existing team.
	 *
	 * @param teamName The name of the team the user should be added to.
	 * @param username The username of the user to add.
	 *
	 * @return Returns true if the action could be performed successfully and
	 * false if not.
	 */
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

	/**
	 * Adds a user to a existing project inside of a existing team.
	 *
	 * @param username The username of the user to add.
	 * @param projectName The project the user should be added to.
	 * @param teamName The name of the team the user and the project belong to.
	 *
	 * @return Returns true if the action was performed successfully and
	 * false if it wasn't.
	 */
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

	/**
	 * Returns a team from the database.
	 *
	 * @param teamName The name of the team which should get loaded.
	 *
	 * @return Returns the TeamEntity object or null if it does not exist in
	 * the database.
	 */
	public TeamEntity getTeam(String teamName) {
		TeamEntity result = teamDAO.getTeamByTeamName(teamName);
		return result;
	}

	/**
	 * Returns a project of a team from the database.
	 *
	 * @param projectName The name of the project which should get returned.
	 * @param teamName The name or the team the project belongs to.
	 *
	 * @return Returns a ProjectEntity object or null if it does not exist in
	 * the database.
	 */
	public ProjectEntity getProject(String projectName, String teamName) {
		ProjectEntity project = projectDAO.getProject(projectName, teamName);
		return project;
	}

	/**
	 * Adds a user to a specific appointment.
	 *
	 * @param userName The username of the user to be added.
	 * @param projectName The name of the project the appointment belongs to.
	 * @param teamName The name of thte team the project, the user and the
	 *                    appointemnt belong to.
	 * @param appointmentId The identifier of the appointment.
	 *
	 * @return Returns true if the action was performed successfully and
	 * false if it yould not be completed.
	 */
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

	/**
	 * Returns all users existing in the database.
	 *
	 * @return A {@see Collection} of all UserEntity entries of the database.
	 */
	public Collection<UserEntity> getAllUsers() {
		return userDAO.getAll();
	}

	/**
	 * Returns all teams existing in the database.
	 *
	 * @return A {@see Collection} of all TeamEntity entries of the database.
	 */
	public Collection<TeamEntity> getAllTeams() {
		return teamDAO.getAll();
	}

	/**
	 * Edits a tean's data and saves it in the database.
	 *
	 * @param team The TeamEntity entry to be edited.
	 * @param teamName The new name of the team.
	 * @param teamDescription The new description of the team.
	 *
	 * @return Returns true if the data could be edited successfully and
	 * false if the team could not be edited.
	 */
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

	/**
	 * Edits a user's data and saves it in the database.
	 *
	 * @param toEdit The UserEntity data to be edited.
	 * @param firstName The new first name of the user.
	 * @param surname The new surname of the user.
	 * @param address The new address of the user.
	 * @param phoneNr The new phone number of the user.
	 * @param email The new email address of the user.
	 * @param birthday The new birthday of the user.
	 *
	 * @return Returns true if the data  could be edited successfully and
	 * false if it could not be edited.
	 */
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

	/**
	 * Adds a team's invitation to a user.
	 *
	 * @param user The user data to which the invitation should be added.
	 * @param team The team data which invited the user to join.
	 *
	 * @return Returns true if the action was completed successfully and
	 * false if not.
	 */
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

	/**
	 * Adds a user's request to a specific team in the system.
	 *
	 * @param team The team the user wants to join.
	 * @param user The user who sent the request to the team.
	 *
	 * @return Returns true if the action was performed successfully and
	 * false if not.
	 */
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

	/**
	 * Saves a user in the database.
	 *
	 * @param user The user who will be saved.
	 */
	public void saveUser(UserEntity user) {
		userDAO.saveOrUpdate(user);
	}

	/**
	 * Saves a team in the database.
	 *
	 * @param team The team which will be saved in the database.
	 */
	public void saveTeam(TeamEntity team) {
		teamDAO.saveOrUpdate(team);
	}

	/**
	 * Saves a register in the database.
	 *
	 * @param register The register which will be saved.
	 */
	public void saveRegister(RegisterEntity register) {
		registerDAO.saveOrUpdate(register);
	}

	/**
	 * Deletes a register from the database.
	 *
	 * @param register The register which will be deleted.
	 */
	public void deleteRegister(RegisterEntity register) {
		registerDAO.remove(register);
	}

	/**
	 * Deletes a user from the database.
	 * If the user is an admin of a team,
	 * all users of the team will be with óut a team and the whole data
	 * belonging to the team will be deleted (except the other users).
	 * If the user is an admin of a project the project, appointments and
	 * statistics belonging to it will be deleted.
	 *
	 * @param user The user who will be deleted.
	 */
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

	/*
	Deletes a user who is a project manager. The whole project data (project,
	appointments and statistics) are deleted to. All users who took part in
	the project are removed from it before but keep existing in the team.
	 */
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

	/*
	Deletes a team administrator. All the other users will be removed from
	the team but keep on existing in the system. The whole team data and the
	related data will be deleted.
	 */
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

	/*
	Deletes a {@see List} of appointments from the database.
	 */
	private void deleteAppointments(List<AppointmentEntity> appointments) {
		for (AppointmentEntity appointment : appointments) {
			appointment.setUserTakinPart(new ArrayList<>());
			appointment.setProject(null);
			appointmentDAO.saveOrUpdate(appointment);
			appointmentDAO.remove(appointment);
		}
	}

	/**
	 * Deletes a team and all it's related data. Before the action all users
	 * will be removed from teh team but keep on existing in the system.
	 *
	 * @param team The team to be deleted.
	 */
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

	/**
	 * Removes all relationships to other data in the database from a user.
	 *
	 * @param user The user who will loose all relationships to other
	 *                database entries.
	 */
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

	/**
	 * Removes a user from a team.
	 * If the user is an admin the whole team the action will not be performed.
	 * If teh user is a manager of a project, all users belonging to the
	 * project will be removed from it and the project and the whole data
	 * belonging to it will be deleted.
	 *
	 * @param user The user to be removed.
	 * @param team Teh team the user will be removed from.
	 *
	 * @return Returns true, if the action was successfully done and false if
	 * not.
	 */
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

	/**
	 * Removes all users from a project and deletes the whole data which
	 * belongs to the project including the project itself.
	 *
	 * @param project The project to be deleted.
	 */
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

	/*
	Deletes a List of statistics form the database.
	 */
	private void deleteStatistics(List<StatisticEntity> statistics) {
		for (StatisticEntity statistic : statistics) {
			deleteStatistic(statistic);
		}
	}

	/*
	Deletes a a single statistic from the database.
	 */
	private void deleteStatistic(StatisticEntity statistic) {
		statistic.setUser(null);
		statistic.setProject(null);
		statisticDAO.saveOrUpdate(statistic);
		statisticDAO.remove(statistic);
	}

	/**
	 * Updates the data of a project.
	 *
	 * @param project The project data to be edited.
	 * @param description The new description of the project.
	 * @param deadline The new deadline of the project.
	 */
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

	/**
	 * Adds or removes users from a project. Takes a {@see List} of usernames
	 * iterates through it. If the user with the current username already is
	 * member of the project, he or she will be removed from it. If there is
	 * no user with the username the user is added to the project as a new
	 * member.
	 *
	 * @param project The project to update it's members.
	 * @param usersToEdit The usernames of the users who's membership to the
	 *                       project have to be edited.
	 */
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

	/*
	Removes the participation statistics of the users who were removed from a
	project.
	 */
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
	/*
	Adds a List of users to a project and creates their participation
	statistics for this project.
	 */
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

	/*
	Removes a List of users from a project.
	 */
	private void removeUsersFromProject(List<String> usersToRemove,
										ProjectEntity project) {
		for (String username : usersToRemove) {
			UserEntity user = getUser(username);
			user.getProjectsTakingPart().remove(project);
			userDAO.saveOrUpdate(user);
		}
	}

	/**
	 * Edits the data of a specific task.
	 *
	 * @param task The task data to be updated.
	 * @param description The new description of the task.
	 * @param deadline The new deadline of the task.
	 */
	public void editTask(TaskEntity task, String description,
						 String deadline) {
		task.setDeadline(deadline);
		task.setDescription(description);
		taskDAO.saveOrUpdate(task);
	}

	/**
	 * Deletes a task from teh database.
	 *
	 * @param task Teh task to be deleted.
	 */
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

	/**
	 * Updates the data of an appointment.
	 *
	 * @param appointment The appointment to be updated.
	 * @param appointmentName The new name of the appointment.
	 * @param appointmentDescription The new description of the appointment.
	 * @param deadline The new deadline of the appointment.
	 * @param deadline The new deadline of the appointment.
	 */
	public void editAppointment(AppointmentEntity appointment,
								String appointmentName,
								String appointmentDescription, String deadline) {
		appointment.setName(appointmentName);
		appointment.setDescription(appointmentDescription);
		appointment.setDeadline(deadline);
		appointmentDAO.saveOrUpdate(appointment);
	}

	/**
	 * Deletes an appointment from the database.
	 *
	 * @param appointment The appointment to be deleted.
	 */
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

	/**
	 * Returns an appointment entry from the database.
	 *
	 * @param id The identifier of the database.
	 *
	 * @return An AppointmentEntity object or null if it does not exist in
	 * the database.
	 */
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

	/**
	 * Saves the participation answer of a specific user for a specific
	 * appointment.
	 *
	 * @param user The user who answered his or her participation.
	 * @param appointment The appointment the participation belongs to.
	 * @param answer The answer the user gave.
	 *
	 * @return Returns true if the action was performed successfully and
	 * false if it was not.
	 */
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

	/**
	 * Changes the password of a user.
	 *
	 * @param user The user who changes his/her password.
	 * @param newPassword The new password of the user.
	 */
	public void changePasswordOfUser(UserEntity user, String newPassword) {
		user.setPassword(newPassword);
		userDAO.saveOrUpdate(user);
	}

	/**
	 * Creates a new chat.
	 *
	 * @param team The team the chat belongs to.
	 * @param usersOfChat The {@see ArrayList} of users using the new chat.
	 * @param chatName The name of the new chat.
	 */
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

	/**
	 * Returns a chat from the database.
	 *
	 * @param team The team teh chat belongs to.
	 * @param chatName The name of the chat which should get returned.
	 *
	 * @return Returns a ChatEntity instance or null if it does not exist.
	 */
	public ChatEntity getChatByName(TeamEntity team, String chatName) {
		return chatDAO.getChatByName(team, chatName);
	}

	/**
	 * Creates a new message for a specific chat.
	 *
	 * @param message The text of the message.
	 * @param author The author of the message.
	 * @param chat The chat the message belongs to.
	 * @param timestamp The point of time the message was written.
	 *
	 * @throws ParseException Throws this exception if the parameter
	 * timestamp has the wrong format.
	 */
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

	/**
	 * Returns a specific chat.
	 *
	 * @param chatId The id of the chat.
	 *
	 * @return An instance of ChatEntity or null if it does not exist.
	 */
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

