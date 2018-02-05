package service;

/**
 * Created by Raphael on 15.06.2017.
 */

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import entity.*;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import service.helper.*;

import java.io.*;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Path("/pmservice")
public class RESTService {

	private final String REQUEST = "request";
	private final String INVITATION = "invitation";
	private DataService dataService = new DataService();
	public static final byte[] SHARED_SECRET = generateSharedSecret();
	public static final long EXPIRE_TIME = 900000; // Within a 15 minutes
								                   // period a token is valid.

	//--------------------------------------------------------------------------
	// Team methods

	@POST
	@Path("/create/team")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject createTeam(JSONObject data) {
		return TeamHelper.createTeam(data, dataService);
	}

	@POST
	@Path("/team/news")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTeamsNews(JSONObject data) {
		return TeamHelper.getTeamsNews(data, dataService);
	}

	@POST
	@Path("/team/tasks")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTeamsTasks(JSONObject data) {
		return TeamHelper.getTeamsTasks(data, dataService);
	}

	@POST
	@Path("/delete/team")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject deleteTeam(JSONObject data) {
		return TeamHelper.deleteTeam(data, dataService);
	}

	@POST
	@Path("member/edit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject editTeamMember(JSONObject data) {
		return TeamHelper.editTeamMember(data, dataService);
	}

	@POST
	@Path("/team/members")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTeamMembers(JSONObject data) {
		return TeamHelper.getTeamMembers(data, dataService);
	}

	@POST
	@Path("/edit/team")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject editTeam(JSONObject data) {
		return TeamHelper.editTeam(data, dataService);
	}

	@POST
	@Path("/team")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTeam(JSONObject data) {
		return TeamHelper.getTeam(data, dataService);
	}

	@POST
	@Path("/teams")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTeams(JSONObject data) {
		return TeamHelper.getTeams(data, dataService);
	}

	@POST
	@Path("/request")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject addRequestToTeam(JSONObject data) {
		return TeamHelper.addRequestToTeam(data, dataService);
	}

	@POST
	@Path("/requests")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getRequestsOfTeam(JSONObject data) {
		return TeamHelper.getRequestsOfTeam(data, dataService);
	}

	//--------------------------------------------------------------------------
	// User methods

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject loginUser(JSONObject data) {
		return UserHelper.loginUser(data, dataService);
	}

	@POST
	@Path("user/chats")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject getUsersChats(JSONObject data) {
		return UserHelper.getUsersChats(data, dataService);
	}

	@POST
	@Path("/user/projects")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getUsersProjects(JSONObject data) {
		return UserHelper.getUsersProjects(data, dataService);
	}

	@POST
	@Path("/user/tasks")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getUsersTasks(JSONObject data) {
		return UserHelper.getUsersTasks(data, dataService);
	}

	@POST
	@Path("/invite")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject inviteUserToTeam(JSONObject data) {
		return UserHelper.inviteUserToTeam(data, dataService);
	}

	@POST
	@Path("/invitations")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getInvitationsOfUser(JSONObject data) {
		return UserHelper.getInvitationsOfUser(data, dataService);
	}

	@POST
	@Path("/user/tributes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getUsersTribute(JSONObject data) {
		return UserHelper.getUsersTribute(data, dataService);
	}

	@POST
	@Path("appointment/answer/participation")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject saveUsersParticipation(JSONObject data) {
		return UserHelper.saveUsersParticipation(data,
				dataService);
	}

	@POST
	@Path("/leave/team")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject leaveTeam(JSONObject data) {
		return UserHelper.leaveTeam(data, dataService);
	}

	@POST
	@Path("/edit/user")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject editUser(JSONObject data) {
		return UserHelper.editUser(data, dataService);
	}

	@POST
	@Path("/users")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getAllUsers(JSONObject data) {
		return UserHelper.getAllUsers(data, dataService);
	}

	@POST
	@Path("/user")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject getUser(JSONObject data) {
		return UserHelper.getUser(data, dataService);
	}

	@POST
	@Path("/register/user")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject registerUser(JSONObject data) {
		return UserHelper.registerUser(data, dataService);
	}

	//--------------------------------------------------------------------------
	// Project methods

	@POST
	@Path("/project/appointments")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getProjectsAppointments(JSONObject data) {
		return ProjectHelper.getProjectsAppointment(data,
				dataService);
	}

	@POST
	@Path("/edit/project/membership")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject editProjectMembership(JSONObject data) {
		return ProjectHelper.editProjectMemebership(data,
				dataService);
	}

	@POST
	@Path("/project/members")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getProjectMembers(JSONObject data) {
		return ProjectHelper.getProjectMembers(data,
				dataService);
	}

	@POST
	@Path("/delete/project")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject deleteProject(JSONObject data) {
		return ProjectHelper.deleteProject(data, dataService);
	}

	@POST
	@Path("/edit/project")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject editProject(JSONObject data) {
		return ProjectHelper.editProject(data, dataService);
	}

	@POST
	@Path("/project")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getProject(JSONObject data) {
		return ProjectHelper.getProject(data, dataService);
	}

	@POST
	@Path("/team/projects")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getAllProjects(JSONObject data) {
		return ProjectHelper.getAllProjects(data, dataService);
	}

	@POST
	@Path("/create/project")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject createProject(JSONObject data) {
		return ProjectHelper.createProject(data, dataService);
	}

	//--------------------------------------------------------------------------
	// Appointment methods

	@POST
	@Path("/appointment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getAppointment(JSONObject data) {
		return AppointmentHelper.getAppointment(data, dataService);
	}

	@POST
	@Path("/delete/appointment")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject deleteAppointment(JSONObject data) {
		return AppointmentHelper.deleteAppointment(data,
				dataService);
	}

	@POST
	@Path("/edit/appointment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject editAppointment(JSONObject data) {
		return AppointmentHelper.editAppointment(data,
				dataService);
	}

	@POST
	@Path("/create/appointment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject createAppointment(JSONObject data) {
		return AppointmentHelper.createAppointment(data,
				dataService);
	}

	//--------------------------------------------------------------------------
	// Task methods

	@POST
	@Path("/delete/task")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject deleteTask(JSONObject data) {
		return TaskHelper.deleteTask(data, dataService);
	}

	@POST
	@Path("/edit/task")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject editTask(JSONObject data) {
		return TaskHelper.editTask(data, dataService);
	}

	@POST
	@Path("/task")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTask(JSONObject data) {
		return TaskHelper.getTask(data, dataService);
	}

	@POST
	@Path("/create/task")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject createTask(JSONObject data) {
		return TaskHelper.createTask(data, dataService);
	}

	//--------------------------------------------------------------------------
	// Chat methods

	@POST
	@Path("/delete/chat")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject deleteChat(JSONObject data) {
		return ChatHelper.deleteChat(data, dataService);
	}

	@POST
	@Path("/chat/messages")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject getChatsMessages(JSONObject data) {
		return ChatHelper.getChatsMessages(data, dataService);
	}

	@POST
	@Path("/create/chat")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject createChat(JSONObject data) {
		return ChatHelper.createChat(data, dataService);
	}

	//--------------------------------------------------------------------------
	// Message methods

	@POST
	@Path("/messages/new")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getNewMessagesForUser(JSONObject data) {
		return MessageHelper.getNewMessagesForUser(data,
				dataService);
	}

	@POST
	@Path("/receive/message")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject receiveMessage(JSONObject data) {
		return MessageHelper.receiveMessage(data, dataService);
	}

	//--------------------------------------------------------------------------
    // Register methods

	@POST
	@Path("/delete/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject delteRegister(JSONObject data) {
		return RegisterHelper.deleteRegister(data, dataService);
	}

	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getRegister(JSONObject data) {
		return RegisterHelper.getRegister(data, dataService);
	}

	@POST
	@Path("/edit/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject editRegister(JSONObject data) {
		return RegisterHelper.editRegister(data, dataService);
	}

	@POST
	@Path("/create/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject createRegister(JSONObject data) {
		return RegisterHelper.createRegister(data, dataService);
	}

	@POST
	@Path("/team/registers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getRegisters(JSONObject data) {
		return RegisterHelper.getRegisters(data, dataService);
	}

	//--------------------------------------------------------------------------
	// General needed methods for services etc.

	@POST
	@Path("/change/password")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject changePassword(JSONObject data) {
		JSONObject result;
		try {
			String username = data.getString("username");
			String oldPassword = data.getString("oldPassword");
			String newPassword = data.getString("newPassword");
			UserEntity user = dataService.getUser(username);
			if (user != null) {
				if (user.getPassword().equals(oldPassword)) {
					dataService.changePasswordOfUser(user, newPassword);
					result = new JSONObject();
					result.put("success", "true");
				} else {
					result = new JSONObject();
					result.put("success", "false");
					result.put("reason", "Das alte Passwort stimmt nicht mit " +
							"Ihrem derzeitigen Passwort überein!");
				}
			} else {
				result = ErrorCreator.returnEmptyResult();
			}
		} catch (JSONException e) {
			result = ErrorCreator.returnClientError();
		}
		return result;
	}

	@POST
	@Path("/password/forgotten")
	@Consumes(MediaType.APPLICATION_JSON)
	public void forgottenPassword(JSONObject data) {
		try {
			String username = data.getString("username");
			UserEntity user = dataService.getUser(username);
			PasswordService passwordService = new PasswordService(user);
			passwordService.sendFromGmail();
		} catch (JSONException e) {
			// Do nothing
		}
	}

	@POST
	@Path("/newsflash/tokenless")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject getNewsForWeekTokenless(JSONObject data) {
		JSONObject result;
		String teamName;
		String username;
		String mondayOfWeek;
		String currentDate;
		String sundayOfWeek;
		try {
			teamName = data.getString("teamName");
			username = data.getString("username");
			mondayOfWeek = data.getString("mondayOfWeek");
			currentDate = data.getString("currentDate");
			sundayOfWeek = data.getString("sundayOfWeek");
			TeamEntity team = dataService.getTeam(teamName);
			UserEntity user = dataService.getUser(username);
			if (user != null && team != null) {
				JSONObject relevantDatesForTheWeek
						= getRelevantDates(user, team, currentDate,
						mondayOfWeek, sundayOfWeek);
				result = new JSONObject();
				result.put("success", "true");
				result.put("dates", relevantDatesForTheWeek);
			} else {
				result = ErrorCreator.returnEmptyResult();
			}
		} catch (
				JSONException e)

		{
			result = ErrorCreator.returnClientError();
		}
		return result;
	}

	@POST
	@Path("/newsflash")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject getNewsForWeek(JSONObject data) {
		JSONObject result;
		String token;
		String teamName;
		String username;
		String mondayOfWeek;
		String currentDate;
		String sundayOfWeek;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
				teamName = data.getString("teamName");
				username = data.getString("username");
				mondayOfWeek = data.getString("mondayOfWeek");
				currentDate = data.getString("currentDate");
				sundayOfWeek = data.getString("sundayOfWeek");
				TeamEntity team = dataService.getTeam(teamName);
				UserEntity user = dataService.getUser(username);
				if (user != null && team != null) {
					JSONObject relevantDatesForTheWeek
							= getRelevantDates(user, team, currentDate,
							mondayOfWeek, sundayOfWeek);
					result = new JSONObject();
					result.put("success", "true");
					result.put("dates", relevantDatesForTheWeek);
				} else {
					result = ErrorCreator.returnEmptyResult();
				}
			} else {
				result = ErrorCreator.returnNoRightsError();
			}
		} catch (JSONException e) {
			result = ErrorCreator.returnClientError();
		}
		return result;
	}


	private JSONObject getRelevantDates(UserEntity user, TeamEntity team,
										String currentDate, String mondayOfWeek,
										String sundayOfWeek) throws JSONException {
		JSONObject result;
		SimpleDateFormat formatter
				= new SimpleDateFormat("dd.MM.yyyy");
		Calendar today = Calendar.getInstance();
		Calendar monday = Calendar.getInstance();
		Calendar sunday = Calendar.getInstance();
		try {
			today.setTime(formatter.parse(currentDate));
			monday.setTime(formatter.parse(mondayOfWeek));
			sunday.setTime(formatter.parse(sundayOfWeek));
		} catch (ParseException e) {
			result = null;
		}
		List<ProjectEntity> projects = user.getProjectsTakingPart();
		JSONArray projectsDeadlines = getDeadlinesOfProjects(projects,
				formatter, monday, today, sunday);
		List<TaskEntity> tasks = user.getTasks();
		JSONArray tasksDeadlines = getDeadlinesOfTasks(tasks, formatter,
				monday, today, sunday);
		List<AppointmentEntity> appointments
				= user.getAppointmentsTakingPart();
		JSONArray appointmentsDeadlines
				= getDateOfAppointments(appointments, formatter, monday, today,
				sunday);
		List<UserEntity> users = team.getUsers();
		JSONArray birthdays = getBirthdayOfUsers(users, formatter, monday,
				today, sunday);
		result = new JSONObject();
		if (projectsDeadlines != null) {
			result.put("projects", projectsDeadlines);
		} else {
			result.put("projects", (Collection) null);
		}
		if (tasksDeadlines != null) {
			result.put("tasks", tasksDeadlines);
		} else {
			result.put("tasks", (Collection) null);
		}
		if (appointmentsDeadlines != null) {
			result.put("appointments", appointmentsDeadlines);
		} else {
			result.put("appointments", (Collection) null);
		}
		if (birthdays != null) {
			result.put("birthdays", birthdays);
		} else {
			result.put("birthdays", (Collection) null);
		}
		return result;
	}

	private JSONArray getBirthdayOfUsers(List<UserEntity> users,
										 SimpleDateFormat formatter,
										 Calendar monday, Calendar today,
										 Calendar sunday) {
		JSONArray result = new JSONArray();
		for (UserEntity user : users) {
			Calendar birthday = Calendar.getInstance();
			try {
				birthday.setTime(formatter.parse(user.getBirthday()));
				if (birthday.before(sunday)
						&& (birthday.after(monday) ||
						deadlineIsCurrently(birthday, today))) {
					JSONObject date = new JSONObject();
					date.put("user", user.getUsername());
					date.put("birthday", user.getBirthday());
					result.put(date);
				}
			} catch (ParseException e) {
				result = null;
			} catch (JSONException e) {
				result = null;
			}
		}
		return result;
	}

	private JSONArray getDateOfAppointments(List<AppointmentEntity> appointments,
											SimpleDateFormat formatter,
											Calendar monday, Calendar today,
											Calendar sunday) {
		JSONArray result = new JSONArray();
		for (AppointmentEntity appointment : appointments) {
			Calendar deadline = Calendar.getInstance();
			try {
				deadline.setTime(formatter.parse(appointment.getDeadline()));
				if (deadline.before(sunday)
						&& (deadline.after(monday) ||
						deadlineIsCurrently(deadline, today))) {
					JSONObject date = new JSONObject();
					date.put("appointment", appointment.getName());
					date.put("deadline", appointment.getDeadline());
					result.put(date);
				}
			} catch (ParseException e) {
				result = null;
			} catch (JSONException e) {
				result = null;
			}
		}
		return result;
	}

	private JSONArray getDeadlinesOfTasks(List<TaskEntity> tasks,
										  SimpleDateFormat formatter,
										  Calendar startOfWeek, Calendar today,
										  Calendar endOfTheWeek) {
		JSONArray result = new JSONArray();
		for (TaskEntity task : tasks) {
			Calendar deadline = Calendar.getInstance();
			try {
				deadline.setTime(formatter.parse(task.getDeadline()));
				if (deadline.before(endOfTheWeek)
						&& (deadline.after(startOfWeek) ||
						deadlineIsCurrently(deadline, today))) {
					JSONObject date = new JSONObject();
					date.put("task", task.getName());
					date.put("deadline", task.getDeadline());
					result.put(date);
				}
			} catch (ParseException e) {
				result = null;
			} catch (JSONException e) {
				result = null;
			}
		}
		return result;
	}

	private JSONArray getDeadlinesOfProjects(List<ProjectEntity> projects,
											 SimpleDateFormat formatter,
											 Calendar startOfWeek,
											 Calendar currentDate,
											 Calendar endOfTheWeek) {
		JSONArray result = new JSONArray();
		for (ProjectEntity project : projects) {
			Calendar deadline = Calendar.getInstance();
			try {
				deadline.setTime(formatter.parse(project.getDeadline()));
				if (deadline.before(endOfTheWeek)
						&& (deadline.after(startOfWeek) ||
						deadlineIsCurrently(deadline, currentDate))) {
					JSONObject date = new JSONObject();
					date.put("project", project.getName());
					date.put("deadline", project.getDeadline());
					result.put(date);
				}
			} catch (ParseException e) {
				result = null;
			} catch (JSONException e) {
				result = null;
			}
		}
		return result;
	}

	private boolean deadlineIsCurrently(Calendar deadline, Calendar currentDate) {
		boolean result = false;
		if (deadline.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)
				&& deadline.get(Calendar.DAY_OF_YEAR)
				== currentDate.get(Calendar.DAY_OF_YEAR)) {
			result = true;
		}
		return result;
	}

	@POST
	@Path("/project/statistics")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getStatistics(JSONObject data) {
		JSONObject result;
		String token;
		String projectName;
		String teamName;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
				projectName = data.getString("projectName");
				teamName = data.getString("teamName");
				ProjectEntity project = dataService.getProject(projectName,
						teamName);
				if (project != null) {
					List<UserEntity> users = project.getUsers();
					List<AppointmentEntity> appointments
							= project.getAppointments();
					List<JSONObject> usersStatistics
							= combineUserAndAppointmentStatistic(users,
							appointments);
					if (usersStatistics != null) {
						result = new JSONObject();
						result.put("success", "true");
						result.put("statistics", usersStatistics);
					} else {
						result = ErrorCreator.returnInternalError();
					}
				} else {
					result = ErrorCreator.returnEmptyResult();
				}
			} else {
				result = ErrorCreator.returnNoRightsError();
			}
		} catch (JSONException e) {
			result = ErrorCreator.returnClientError();
		}
		return result;
	}

	private List<JSONObject> combineUserAndAppointmentStatistic(
			List<UserEntity> users,
			List<AppointmentEntity> appointments) throws JSONException {
		List<JSONObject> result = new ArrayList<>();
		int numberOfAllAppoinments = appointments.size();
		for (UserEntity user : users) {
			int countMaybe = 0;
			int countYes = 0;
			int countNo = 0;
			for (AppointmentEntity appointment : appointments) {
				StatisticParticipationAnswer answerOfUser
						= appointment.getUserAnswers().get(user.getUsername());
				if (answerOfUser == StatisticParticipationAnswer.YES) {
					countYes++;
				} else if (answerOfUser == StatisticParticipationAnswer.MAYBE) {
					countMaybe++;
				} else {
					countNo++;
				}
			}
			JSONObject userStatistic = new JSONObject();
			userStatistic.put("user", user.getUsername());
			userStatistic.put("#yes", countYes);
			userStatistic.put("#maybe", countMaybe);
			userStatistic.put("#no", countNo);
			userStatistic.put("#all", numberOfAllAppoinments);
			result.add(userStatistic);
		}
		return result;
	}

	@POST
	@Path("/leave")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject leaveApp(JSONObject data) {
		JSONObject result;
		String token;
		String username;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
				username = data.getString("username");
				UserEntity user = dataService.getUser(username);
				if (user != null) {
					dataService.deleteUser(user);
					result = new JSONObject();
					result.put("success", "true");
				} else {
					result = ErrorCreator.returnEmptyResult();
				}
			} else {
				result = ErrorCreator.returnNoRightsError();
			}
		} catch (JSONException e) {
			result = ErrorCreator.returnClientError();
		}
		return result;
	}

	@POST
	@Path("/answer")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject answerInvitationOrRequest(JSONObject data) {
		JSONObject result;
		String token;
		String username;
		String teamName;
		String agreeOrDisagree;
		String invitationOrRequest;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
				username = data.getString("username");
				teamName = data.getString("teamName");
				UserEntity user = dataService.getUser(username);
				TeamEntity team = dataService.getTeam(teamName);
				if (user != null && team != null) {
					agreeOrDisagree
							= data.getString("agreeOrDisagree");
					invitationOrRequest
							= data.getString("invitationOrRequest");
					result = prepareAnswer(user, team, agreeOrDisagree,
							invitationOrRequest);
				} else {
					result = ErrorCreator.returnEmptyResult();
				}
			} else {
				result = ErrorCreator.returnNoRightsError();
			}
		} catch (JSONException e) {
			result = ErrorCreator.returnClientError();
		}
		return result;
	}

	private JSONObject prepareAnswer(UserEntity user, TeamEntity team,
									 String agreeOrDisagree,
									 String invitationOrRequest) {
		JSONObject result;
		if (invitationOrRequest.equals("invitation")) {
			result = answerInvitation(user, team, agreeOrDisagree);
		} else {
			result = answerRequest(user, team, agreeOrDisagree);
		}
		return result;
	}

	private JSONObject answerInvitation(UserEntity user, TeamEntity team,
										String agreeOrDisagree) {
		JSONObject result;
		List<String> invitationsOfUser = new ArrayList<>();
		invitationsOfUser = user.getInvitationsOfTeams();
		if (invitationsOfUser.size() != 0) {
			if (invitationsOfUser.contains(team.getName())) {
				if (dealWithInvitation(agreeOrDisagree,
						invitationsOfUser, team, user)) {
					try {
						result = new JSONObject("{\"success\":" +
								" \"true\", \"team\": " +
								"\"" + team.getName() + "\"," +
								"\"type\": \"" + INVITATION + "\"}");
					} catch (JSONException e) {
						result = ErrorCreator.returnClientError();
					}
				} else {
					try {
						result = new JSONObject("{\"success\":" +
								"\"true\", \"type\": \"" + INVITATION + "\"}");
					} catch (JSONException e) {
						result = ErrorCreator.returnClientError();
					}
				}
			} else {
				result = ErrorCreator.returnEmptyResult();
			}
		} else {
			result = ErrorCreator.returnEmptyResult();
		}
		return result;
	}

	private JSONObject answerRequest(UserEntity user, TeamEntity team,
									 String agreeOrDisagree) {
		JSONObject result;
		List<String> requestOfTeam = new ArrayList<>();
		requestOfTeam = team.getRequestsOfUsers();
		if (requestOfTeam.size() != 0) {
			if (requestOfTeam.contains(user.getUsername())) {
				if (dealWithRequest(agreeOrDisagree,
						requestOfTeam, team, user)) {
					try {
						result = new JSONObject("{\"success\":" +
								" \"true\", \"team\": " +
								"\"" + team.getName() + "\", " +
								"\"type\": \"" + REQUEST + "\"}");
					} catch (JSONException e) {
						result = ErrorCreator.returnClientError();
					}
				} else {
					try {
						result = new JSONObject("{\"success\":" +
								"\"false\", \"reason\": \"Der User scheint " +
								"schon in einem Team Mitglied zu sein oder " +
								"die Anfrage existiert nicht mehr!\"}");
					} catch (JSONException e) {
						result = ErrorCreator.returnClientError();
					}
				}
			} else {
				result = ErrorCreator.returnEmptyResult();
			}
		} else {
			result = ErrorCreator.returnEmptyResult();
		}
		return result;
	}

	private boolean dealWithRequest(String agreeOrDisagree,
									List<String> requestOfTeam,
									TeamEntity team, UserEntity user) {
		boolean result;
		if (agreeOrDisagree.equals("agree")) {
			if (user.getTeam() == null) {
				user.setTeam(team);
				user.setDayOfEntry(Calendar.getInstance());
				requestOfTeam.remove(user.getUsername());
				dataService.saveUser(user);
				dataService.saveTeam(team);
				result = true;
			} else {
				requestOfTeam.remove(user.getUsername());
				team.setRequestsOfUsers(requestOfTeam);
				dataService.saveTeam(team);
				result = false;
			}
		} else {
			requestOfTeam.remove(user.getUsername());
			team.setRequestsOfUsers(requestOfTeam);
			dataService.saveTeam(team);
			result = false;
		}
		return result;
	}

	private boolean dealWithInvitation(String agreeOrDisagree,
									   List<String> invitations,
									   TeamEntity team, UserEntity user) {
		boolean result;
		if (agreeOrDisagree.equals("agree")) {
			if (user.getTeam() == null) {
				user.setTeam(team);
				user.setDayOfEntry(Calendar.getInstance());
				user.setInvitationsOfTeams(new ArrayList<>());
				dataService.saveUser(user);
				result = true;
			} else {
				user.setInvitationsOfTeams(new ArrayList<>());
				dataService.saveUser(user);
				result = false;
			}
		} else {
			invitations.remove(team.getName());
			user.setInvitationsOfTeams(invitations);
			dataService.saveUser(user);
			result = false;
		}
		return result;
	}

	private static byte[] generateSharedSecret() {
		// Generate random 256-bit shared secret
		FileInputStream fis;
		FileOutputStream fos;
		byte[] result = new byte[32];
		try {
			fis = new FileInputStream(new File("resources.txt"));
			byte[] read = new byte[32];
			fis.read(read);
			fis.close();
			if (read == null) {
				SecureRandom random = new SecureRandom();
				random.nextBytes(result);
				fos = new FileOutputStream(new File("resources.txt"));
				fos.write(result);
			} else {
				result = read;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}