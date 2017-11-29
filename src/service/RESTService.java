package service;

/**
 * Created by Raphael on 15.06.2017.
 */

import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import entity.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.*;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

@JsonSerialize
@Path("/pmservice")
public class RESTService {

	private final String MAYBE = "MAYBE";
	private final String YES = "YES";
	private final String NO = "NO";
	private final String REQUEST = "request";
	private final String INVITATION = "invitation";
	private DataService dataService = new DataService();
	private final byte[] SHARED_SECRET = generateSharedSecret();
	private final long EXPIRE_TIME = 900000; // Within a 15 minutes period a
											 // token is valid

	//--------------------------------------------------------------------------

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
				result = returnEmptyResult();
			}
		} catch (JSONException e) {
			result = returnClientError();
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
	@Path("/user/tributes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getUsersTribute(JSONObject data) {
		JSONObject result;
		String token;
		String username;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				username = data.getString("username");
				UserEntity user = dataService.getUser(username);
				if (user != null) {
					String tributes = user.getTributes();
					if (tributes == null) {
						tributes = "";
					}
					result = new JSONObject();
					result.put("success", "true");
					result.put("tributes", tributes);
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/team/news")
	public JSONObject getTeamsNews(JSONObject data) {
		JSONObject result;
		try {
			String teamName = data.getString("teamName");
			TeamEntity team = dataService.getTeam(teamName);
			if (team != null) {
				result = new JSONObject();
				List<String> news = team.getNews();
				result.put("success", "true");
				result.put("news", news);
				team.clearNews();
				dataService.saveTeam(team);
			} else {
				result = returnEmptyResult();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
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
				result = returnEmptyResult();
			}
		} catch (
				JSONException e)

		{
			result = returnClientError();
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
			if (validateToken(token)) {
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
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
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
			if (validateToken(token)) {
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
						result = returnInternalError();
					}
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
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
	@Path("/appointment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getAppointment(JSONObject data) {
		JSONObject result;
		String token;
		String appointmentId;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				appointmentId = data.getString("id");
				AppointmentEntity appointment = dataService
						.getAppointment(Long.parseLong(appointmentId));
				if (appointment != null) {
					result = new JSONObject();
					JSONObject appointmentJSON
							= new JSONObject(appointment.toString());
					appointmentJSON.put("userAnswer",
							appointment.getUserAnswers());
					result.put("success", "true");
					result.put("appointment", appointmentJSON);
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("appointment/answer/participation")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject saveUsersParticipation(JSONObject data) {
		JSONObject result;
		String token;
		String username;
		String appointmentId;
		String answer;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				username = data.getString("username");
				appointmentId = data.getString("id");
				answer = data.getString("answer");
				UserEntity user = dataService.getUser(username);
				AppointmentEntity appointment = dataService
						.getAppointment(Long.parseLong(appointmentId));
				if (user != null && appointment != null) {
					if (dataService.isUserTakingPartAtProject(user,
							appointment.getProject())) {
						StatisticParticipationAnswer usersAnswer =
								getUsersAnswer(answer);
						if (dataService.saveAnswerParticipation(user,
								appointment, usersAnswer)) {
							result = new JSONObject();
							result.put("success", "true");
							result.put("userAnswer", "" + usersAnswer);
						} else {
							result = returnInternalError();
						}
					} else {
						result = new JSONObject();
						result.put("success", "false");
						result.put("reason", "Der User ist nicht Teil des " +
								"entsprechenden Projekts und kann deshalb " +
								"nicht an dem Meeting teilnehmen!");
					}
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	private StatisticParticipationAnswer getUsersAnswer(String answer) {
		StatisticParticipationAnswer result;
		if (answer.equals(YES)) {
			result = StatisticParticipationAnswer.YES;
		} else if (answer.equals(NO)) {
			result = StatisticParticipationAnswer.NO;
		} else {
			result = StatisticParticipationAnswer.MAYBE;
		}
		return result;
	}

	@POST
	@Path("/delete/appointment")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject deleteAppointment(JSONObject data) {
		JSONObject result;
		String token;
		String projectName;
		String teamName;
		String username;
		String appointmentId;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				projectName = data.getString("projectName");
				teamName = data.getString("teamName");
				username = data.getString("username");
				appointmentId = data.getString("id");
				ProjectEntity project = dataService.getProject(projectName,
						teamName);
				AppointmentEntity appointment
						= dataService.getAppointment(projectName,
						Long.parseLong(appointmentId), teamName);
				if (appointment != null && project != null) {
					if (project.getProjectManager().getUsername()
							.equals(username)) {
						if (!appointment.getIsDeadline()) {
							dataService.deleteAppointment(appointment);
							result = new JSONObject();
							result.put("success", "true");
						} else {
							result = new JSONObject();
							result.put("success", "false");
							result.put("reason", "Das Meeting is ein " +
									"Abschlusstermin eines Projekts!\n" +
									"Es kann nur gelöscht werden, indem das " +
									"Projekt gelöscht wird!");
						}
					} else {
						result = returnNoRightsError();
					}
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/edit/appointment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject editAppointment(JSONObject data) {
		JSONObject result;
		String token;
		String username;
		String projectName;
		String teamName;
		String id;
		String appointmentName;
		String appointmentDescription;
		String deadline;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				username = data.getString("username");
				projectName = data.getString("projectName");
				teamName = data.getString("teamName");
				id = data.getString("id");
				appointmentName = data.getString("appointmentName");
				appointmentDescription
						= data.getString("appointmentDescription");
				deadline = data.getString("deadline");
				ProjectEntity project = dataService.getProject(projectName,
						teamName);
				AppointmentEntity appointment = dataService.getAppointment
						(projectName,
								Long.parseLong(id),
								teamName);
				if (project != null && appointment != null) {
					if (project.getProjectManager().getUsername()
							.equals(username)) {
						dataService.editAppointment(appointment,
								appointmentName, appointmentDescription,
								deadline);
						result = new JSONObject();
						result.put("success", "true");
					} else {
						result = returnNoRightsError();
					}
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}


	@POST
	@Path("/create/appointment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject createAppointment(JSONObject data) {
		JSONObject result;
		String token;
		String projectName;
		String teamName;
		String appointmentName;
		String description;
		String deadline;
		String username;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				projectName = data.getString("projectName");
				teamName = data.getString("teamName");
				appointmentName = data.getString("appointmentName");
				description = data.getString("description");
				deadline = data.getString("deadline");
				username = data.getString("username");
				ProjectEntity project = dataService.getProject(projectName,
						teamName);
				if (project != null) {
					if (project.getProjectManager().getUsername()
							.equals(username)) {
						if (dataService.createNewAppointment(appointmentName,
								description, deadline, projectName, teamName)) {
							result = new JSONObject();
							result.put("success", "true");
						} else {
							result = new JSONObject();
							result.put("success", "false");
							result.put("reason", "Interner Fehler! Das " +
									"Meeting konnte nicht angelegt werden!");
						}
					} else {
						result = returnNoRightsError();
					}
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/project/appointments")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getProjectsAppointments(JSONObject data) {
		JSONObject result;
		String token;
		String projectName;
		String teamName;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				projectName = data.getString("projectName");
				teamName = data.getString("teamName");
				ProjectEntity project = dataService.getProject(projectName,
						teamName);
				if (project != null) {
					List<AppointmentEntity> appointments
							= project.getAppointments();
					List<JSONObject> appointmentsJson = new ArrayList<>();
					for (AppointmentEntity appointment : appointments) {
						JSONObject appointmentJSON
								= new JSONObject(appointment.toString());
						appointmentJSON.put("userAnswer",
								appointment.getUserAnswers());
						appointmentsJson
								.add(appointmentJSON);
					}
					result = new JSONObject();
					result.put("success", "true");
					result.put("appointments", appointmentsJson);
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/delete/task")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject deleteTask(JSONObject data) {
		JSONObject result;
		String token;
		String taskName;
		String teamName;
		String username;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				taskName = data.getString("taskName");
				teamName = data.getString("teamName");
				username = data.getString("username");
				TaskEntity task = dataService.getTask(taskName, teamName);
				if (task != null) {
					if (task.getWorker().getUsername().equals(username)) {
						dataService.deleteTask(task);
						result = new JSONObject();
						result.put("success", "true");
					} else {
						result = returnNoRightsError();
					}
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/edit/task")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject editTask(JSONObject data) {
		JSONObject result;
		String token;
		String taskName;
		String description;
		String deadline;
		String teamName;
		String username;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				taskName = data.getString("taskName");
				description = data.getString("taskDescription");
				deadline = data.getString("taskDeadline");
				teamName = data.getString("teamName");
				username = data.getString("username");
				TaskEntity task
						= dataService.getTask(taskName, teamName);
				TeamEntity team = dataService.getTeam(teamName);
				if (task != null && team != null) {
					if (task.getWorker().getUsername().equals(username)) {
						dataService.editTask(task, description, deadline);
						result = new JSONObject();
						result.put("success", "true");
					} else {
						result = returnNoRightsError();
					}
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/task")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTask(JSONObject data) {
		JSONObject result;
		String token;
		String taskName;
		String teamName;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				taskName = data.getString("taskName");
				teamName = data.getString("teamName");
				TaskEntity task = dataService.getTask(taskName, teamName);
				if (task != null) {
					result = new JSONObject();
					result.put("success", "true");
					result.put("task", new JSONObject(task.toString()));
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/team/tasks")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTeamsTasks(JSONObject data) {
		JSONObject result;
		String token;
		String teamName;
		String username;

		try {
			token = data.getString("token");
			if (validateToken(token)) {
				teamName = data.getString("teamName");
				username = data.getString("username");
				TeamEntity team = dataService.getTeam(teamName);
				if (team != null) {
					if (team.getAdmin().getUsername().equals(username)) {
						List<TaskEntity> tasks = team.getTasks();
						ArrayList<String> projectsOfTeam = new ArrayList<>();
						for (TaskEntity task : tasks) {
							projectsOfTeam.add(task.getName());
						}
						result = new JSONObject();
						result.put("success", "true");
						result.put("tasks", projectsOfTeam);
					} else {
						result = returnNoRightsError();
					}
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/user/tasks")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getUsersTasks(JSONObject data) {
		JSONObject result;
		String token;
		String username;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				username = data.getString("username");
				UserEntity user = dataService.getUser(username);
				if (user != null) {
					List<TaskEntity> tasks = user.getTasks();
					List<String> taskNames = new ArrayList<>();
					for (TaskEntity task : tasks) {
						taskNames.add(task.getName());
					}
					result = new JSONObject();
					result.put("success", "true");
					result.put("tasks", taskNames);

				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/create/task")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject createTask(JSONObject data) {
		JSONObject result;
		String token;
		String taskName;
		String taskDescription;
		String worker;
		String deadline;
		String teamName;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				taskName = data.getString("taskName");
				taskDescription = data.getString("description");
				worker = data.getString("worker");
				teamName = data.getString("teamName");
				deadline = data.getString("deadline");
				TaskEntity task = dataService.getTask(taskName, teamName);
				UserEntity user = dataService.getUser(worker);
				if (user != null) {
					if (task == null) {
						if (dataService.createNewTask(taskName, taskDescription,
								deadline, teamName, worker)) {
							result = new JSONObject();
							result.put("success", "true");
						} else {
							result = new JSONObject();
							result.put("success", "false");
							result.put("reason", "Interner Fehler! Die Aufgabe " +
									"konnte nicht angelegt werden!");
						}
					} else {
						result = returnExistingError();
					}
				} else {
					result = returnNoRightsError();
				}
			} else {
				result = returnEmptyResult();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/edit/project/membership")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject editProjectMembership(JSONObject data) {
		JSONObject result;
		String token;
		String projectName;
		String teamName;
		String username;
		JSONArray usernames;
		ArrayList<String> usersToEdit = new ArrayList<>();
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				projectName = data.getString("projectName");
				teamName = data.getString("teamName");
				username = data.getString("username");
				usernames = data.getJSONArray("users");
				for (int i = 0; i < usernames.length(); i++) {
					usersToEdit.add((String) usernames.get(i));
				}
				ProjectEntity project = dataService.getProject(projectName,
						teamName);
				if (usersToEdit != null) {
					if (project != null) {
						if (project.getProjectManager().getUsername()
								.equals(username)) {
							dataService.editProjectMembership(project,
									usersToEdit);
							result = new JSONObject();
							result.put("success", "true");
						} else {
							result = returnNoRightsError();
						}
					} else {
						result = returnEmptyResult();
					}
				} else {
					result = new JSONObject();
					result.put("success", "false");
					result.put("reason", "Die zu editierenden User konnten " +
							"nicht extrahiert werden! Falsches Format der " +
							"gesendeten Daten! Wenden Sie sich an den " +
							"Systemadminstrator unter grum02@gw.uni-passau.de");
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	private ArrayList<String> extractUsernamesFromData(JSONArray data) {
		ArrayList<String> result = new ArrayList<>();
		for (int i = 0; i < data.length(); i++) {
			try {
				String tempUsername = data.getString(i);
				result.add(tempUsername);
			} catch (JSONException e) {
				result = null;
				break;
			}
		}
		return result;
	}

	@POST
	@Path("/project/members")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getProjectMembers(JSONObject data) {
		JSONObject result;
		String token;
		String projectName;
		String teamName;
		String username;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				projectName = data.getString("projectName");
				teamName = data.getString("teamName");
				username = data.getString("username");
				ProjectEntity project = dataService.getProject(projectName,
						teamName);
				UserEntity user = dataService.getUser(username);
				if (project != null) {
					List<UserEntity> users = project.getUsers();
					users.remove(user);
					JSONArray usersToReturn = new JSONArray();
					for (UserEntity tempUser : users) {
						usersToReturn.put(tempUser.getUsername());
					}
					result = new JSONObject();
					result.put("success", "true");
					result.put("members", usersToReturn);
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/delete/project")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject deleteProject(JSONObject data) {
		JSONObject result;
		String token;
		String projectName;
		String teamName;
		String username;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				projectName = data.getString("projectName");
				teamName = data.getString("teamName");
				username = data.getString("username");
				ProjectEntity project = dataService.getProject(projectName,
						teamName);
				if (project != null) {
					if (project.getProjectManager().getUsername()
							.equals(username)) {
						dataService.deleteProject(project);
						result = new JSONObject();
						result.put("success", "true");
					} else {
						result = returnNoRightsError();
					}
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/edit/project")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject editProject(JSONObject data) {
		JSONObject result = new JSONObject();
		String token;
		String username;
		String projectName;
		String teamName;
		String description;
		String deadline;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				username = data.getString("username");
				projectName = data.getString("projectName");
				teamName = data.getString("teamName");
				description = data.getString("description");
				deadline = data.getString("deadline");
				ProjectEntity project
						= dataService.getProject(projectName, teamName);
				if (project != null) {
					if (project.getProjectManager()
							.getUsername().equals(username)) {
						dataService.editProject(project, description,
								deadline);
						result.put("success", "true");
					} else {
						result = returnNoRightsError();
					}
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/project")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getProject(JSONObject data) {
		JSONObject result;
		String token;
		String teamName;
		String projectName;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				teamName = data.getString("teamName");
				projectName = data.getString("projectName");
				ProjectEntity project = dataService.getProject(projectName,
						teamName);
				if (project != null) {
					result = new JSONObject();
					result.put("success", "true");
					result.put("project", project.toString());
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/delete/team")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject deleteTeam(JSONObject data) {
		JSONObject result;
		String token;
		String username;
		String teamName;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				username = data.getString("username");
				teamName = data.getString("teamName");
				UserEntity user = dataService.getUser(username);
				TeamEntity team = dataService.getTeam(teamName);
				if (user != null && team != null) {
					if (user.getRole() == UserRole.ADMINISTRATOR
							&& user.getTeam().getName()
							.equals(team.getName())) {
						dataService.deleteTeam(team);
						result = new JSONObject();
						if (dataService.getTeam(teamName) == null) {
							result.put("success", "true");
						} else {
							result.put("success", "false");
							result.put("reason", "Interner Server Fehler!");
						}
					} else {
						result = returnNoRightsError();
					}
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/leave/team")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject leaveTeam(JSONObject data) {
		JSONObject result;
		String token;
		String username;
		String teamName;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				username = data.getString("username");
				teamName = data.getString("teamName");
				UserEntity user = dataService.getUser(username);
				TeamEntity team = dataService.getTeam(teamName);
				if (team != null && user != null) {
					if (dataService.leaveTeam(user, team)) {
						UserEntity updatedUser = dataService.getUser(username);
						token = createUserToken("" + updatedUser.getId(),
								username, "" + updatedUser.getRole(),
								"null");
						result = new JSONObject();
						result.put("success", "true");
						result.put("token", token);
					} else {
						// This mistake just can be reached by attackers and is
						// fetched and defended right here.
						result = new JSONObject();
						result.put("success", "false");
						result.put("reason", "");
					}
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
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
			if (validateToken(token)) {
				username = data.getString("username");
				UserEntity user = dataService.getUser(username);
				if (user != null) {
					dataService.deleteUser(user);
					result = new JSONObject();
					result.put("success", "true");
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/delete/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject delteRegister(JSONObject data) {
		JSONObject result;
		String token;
		String username;
		String registerName;
		String teamName;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				username = data.getString("username");
				registerName = data.getString("registerName");
				teamName = data.getString("teamName");
				TeamEntity team = dataService.getTeam(teamName);
				RegisterEntity register = dataService.getRegister(registerName,
						teamName);
				if (team != null && register != null) {
					if (team.getAdmin().getUsername().equals(username)) {
						dataService.deleteRegister(register);
						result = new JSONObject();
						result.put("success", "true");
					} else {
						result = returnNoRightsError();
					}
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getRegister(JSONObject data) {
		JSONObject result;
		String token;
		String registerName;
		String teamName;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				registerName = data.getString("registerName");
				teamName = data.getString("teamName");
				RegisterEntity register = dataService.getRegister(registerName,
						teamName);
				if (register != null) {
					result = new JSONObject();
					result.put("success", "true");
					result.put("register", register.toString());
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/edit/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject editRegister(JSONObject data) {
		JSONObject result;
		String token;
		String registerName;
		String color;
		String teamName;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				registerName = data.getString("registerName");
				color = data.getString("color");
				teamName = data.getString("teamName");
				RegisterEntity register = dataService.getRegister(registerName,
						teamName);
				if (register != null) {
					register.setColor(color);
					dataService.saveRegister(register);
					result = new JSONObject();
					result.put("success", "true");
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/user/projects")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getUsersProjects(JSONObject data) {
		JSONObject result;
		String token;
		String username;
		String teamName;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				username = data.getString("username");
				teamName = data.getString("teamName");
				UserEntity user = dataService.getUser(username);
				TeamEntity team = dataService.getTeam(teamName);
				if (user != null && team != null
						&& user.getTeam().getName().equals(teamName)) {
					List<ProjectEntity> usersProjects
							= user.getProjectsTakingPart();
					List<String> projectsNames = new ArrayList<>();
					for (ProjectEntity project : usersProjects) {
						projectsNames.add(project.getName());
					}
					result = new JSONObject();
					result.put("success", "true");
					result.put("projects", projectsNames);
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/team/projects")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getAllProjects(JSONObject data) {
		JSONObject result;
		String token;
		String teamName;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				teamName = data.getString("teamName");
				TeamEntity team = dataService.getTeam(teamName);
				if (team != null) {
					List<ProjectEntity> fetchedProjects = team.getProjects();
					List<String> projects = new ArrayList<>();
					for (ProjectEntity project : fetchedProjects) {
						projects.add(project.getName());
					}
					result = new JSONObject();
					result.put("success", "true");
					result.put("projects", projects);
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/create/project")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject createProject(JSONObject data) {
		JSONObject result;
		String token;
		String teamName;
		String projectName;
		String projectDescription;
		String projectManager;
		String deadline;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				teamName = data.getString("teamName");
				projectName = data.getString("projectName");
				projectDescription = data.getString("projectDescription");
				projectManager = data.getString("projectManager");
				deadline = data.getString("deadline");
				UserEntity user = dataService.getUser(projectManager);
				ProjectEntity project
						= dataService.getProject(projectName, teamName);
				TeamEntity team = dataService.getTeam(teamName);
				if (project == null && user != null) {
					if (user.getAdminOfProject() == null) {
						if (dataService.createNewProject(teamName, projectName,
								projectDescription, projectManager, deadline)) {
							UserEntity manager = dataService.getUser(projectManager);
							token = createUserToken("" + manager.getId(),
									manager.getUsername(), "" + manager.getRole(),
									manager.getTeam().getName());
							result = new JSONObject();
							result.put("success", "true");
							result.put("token", token);
							if (projectManager.equals(team.getAdmin()
									.getUsername())) {
								result.put("adminOfProject", projectName);
							} else {
								result.put("adminOfProject", "null");
							}
						} else {
							result = new JSONObject("{\"success\": \"false\"}");
						}
					} else {
						result = new JSONObject();
						result.put("success", "false");
						result.put("reason", "Der User ist schon ein " +
								"Projektleiter beim Projekt \"" + user
								.getAdminOfProject().getName() + "\" " +
								"und kann die Leitung deswegen nicht " +
								"übernehmen!");
					}
				} else {
					result = returnExistingError();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("member/edit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject editTeamMember(JSONObject data) {
		JSONObject result;
		String token;
		String toEdit;
		String registerName;
		String admin;
		String teamName;
		String tributes;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				toEdit = data.getString("username");
				registerName = data.getString("registerName");
				admin = data.getString("admin");
				teamName = data.getString("teamName");
				tributes = data.getString("tributes");
				UserEntity userToEdit = dataService.getUser(toEdit);
				RegisterEntity register;
				if (registerName.equals("-----")) {
					register = null;
				} else {
					register = dataService.getRegister
							(registerName, teamName);
				}
				UserEntity userAdmin = dataService.getUser(admin);
				if (userToEdit != null && userAdmin != null) {
					if (userAdmin.getRole().equals(UserRole.ADMINISTRATOR)
							&& userAdmin.getTeam().getName().equals(teamName)) {
						userToEdit.setRegister(register);
						userToEdit.setTributes(tributes);
						dataService.saveUser(userToEdit);
						result = new JSONObject();
						result.put("success", "true");
					} else {
						result = returnNoRightsError();
					}
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/team/members")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTeamMembers(JSONObject data) {
		JSONObject result;
		String token;
		String teamName;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				teamName = data.getString("teamName");
				TeamEntity team = dataService.getTeam(teamName);
				if (team != null) {
					List<UserEntity> teamsMembers = team.getUsers();
					ArrayList<JSONObject> members = new ArrayList<>();
					for (UserEntity user : teamsMembers) {
						JSONObject userInfo;
						if (user.getRegister() != null) {
							userInfo = new JSONObject(
									"{\"username\": \""
											+ user.getUsername()
											+ "\", \"register\": " +
											"\"" + user.getRegister()
											.getName() + "\", \"color\": \"" +
											user.getRegister().getColor()
											+ "\"}");
							members.add(userInfo);
						} else {
							userInfo = new JSONObject("{\"username\": " +
									"\"" + user.getUsername() + "\", " +
									"\"register\": \"null\", \"color\":" +
									" \"-1\"}");
							members.add(userInfo);
						}
					}
					result = new JSONObject();
					result.put("success", "true");
					result.put("members", members);
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/create/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject createRegister(JSONObject data) {
		JSONObject result;
		String token;
		String teamName;
		String registerName;
		String username;
		String color;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				teamName = data.getString("teamName");
				registerName = data.getString("registerName");
				username = data.getString("username");
				color = data.getString("color");
				TeamEntity team = dataService.getTeam(teamName);
				RegisterEntity register
						= dataService.getRegister(registerName, teamName);
				if (team != null && register == null) {
					if (team.getAdmin().getUsername().equals(username)) {
						if (dataService.createNewRegister(registerName,
								teamName, color)) {
							result = new JSONObject("{\"success\": \"true\"}");
						} else {
							result = returnServerError();
						}
					} else {
						result = returnNoRightsError();
					}
				} else {
					result = returnExistingError();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/team/registers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getRegisters(JSONObject data) {
		JSONObject result;
		String token;
		String teamName;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				teamName = data.getString("teamName");
				TeamEntity team = dataService.getTeam(teamName);
				if (team != null) {
					List<JSONObject> registerNames = new ArrayList<>();
					List<RegisterEntity> registerEntities = team.getRegisters();
					if (registerEntities.size() != 0) {
						for (RegisterEntity register : registerEntities) {
							JSONObject registerData = new JSONObject();
							registerData.put("registerName",
									register.getName());
							registerData.put("color", register.getColor());
							registerNames.add(registerData);
						}
						result = new JSONObject();
						result.put("success", "true");
						result.put("registers", registerNames);
					} else {
						result = new JSONObject();
						result.put("success", "true");
						result.put("registers", "null");
					}
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	public JSONObject getTeamMemebers(JSONObject data) {
		JSONObject result;
		String token;
		String teamName;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				teamName = data.getString("teamName");
				TeamEntity team = dataService.getTeam(teamName);
				if (team != null) {
					// TODO
					result = null;
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
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
			if (validateToken(token)) {
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
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
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
						result = returnClientError();
					}
				} else {
					try {
						result = new JSONObject("{\"success\":" +
								"\"true\", \"type\": \"" + INVITATION + "\"}");
					} catch (JSONException e) {
						result = returnClientError();
					}
				}
			} else {
				result = returnEmptyResult();
			}
		} else {
			result = returnEmptyResult();
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
						result = returnClientError();
					}
				} else {
					try {
						result = new JSONObject("{\"success\":" +
								"\"false\", \"reason\": \"Der User scheint " +
								"schon in einem Team Mitglied zu sein oder " +
								"die Anfrage existiert nicht mehr!\"}");
					} catch (JSONException e) {
						result = returnClientError();
					}
				}
			} else {
				result = returnEmptyResult();
			}
		} else {
			result = returnEmptyResult();
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

	@POST
	@Path("/request")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject addRequestToTeam(JSONObject data) {
		JSONObject result;
		String token;
		String username;
		String teamName;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				username = data.getString("username");
				teamName = data.getString("teamName");
				UserEntity user = dataService.getUser(username);
				TeamEntity team = dataService.getTeam(teamName);
				if (user != null && team != null) {
					if (dataService.addRequestToTeam(team, user)) {
						result = new JSONObject("{\"success\": \"true\"}");
					} else {
						result = returnServerError();
					}
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	// TODO test
	@POST
	@Path("/requests")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getRequestsOfTeam(JSONObject data) {
		JSONObject result;
		String token;
		String teamName;
		String username;
		try {
			token = data.getString("token");
			teamName = data.getString("teamName");
			username = data.getString("username");
			if (validateToken(token)) {
				TeamEntity teamEntity = dataService.getTeam(teamName);
				if (teamEntity != null) {
					if (teamEntity.getAdmin().getUsername().equals(username)) {
						List<String> requests = new ArrayList<>();
						requests = teamEntity.getRequestsOfUsers();
						if (requests.size() != 0) {
							StringBuilder stringBuilder = new StringBuilder();
							for (int i = 0; i < requests.size(); i++) {
								if (i != (requests.size() - 1)) {
									stringBuilder.append(requests.get(i) + ",");
								} else {
									stringBuilder.append(requests.get(i));
								}
							}
							result = new JSONObject("{\"success\": " +
									"\"true\", \"requests\": \""
									+ stringBuilder.toString() + "\"}");
						} else {
							result = new JSONObject();
							result.put("success", "true");
							result.put("requests", "null");
						}
					} else {
						result = returnNoRightsError();
					}
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/invite")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject inviteUserToTeam(JSONObject data) {
		JSONObject result;
		String token;
		String usernameToInvite;
		String teamName;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				usernameToInvite = data.getString("usernameToInvite");
				teamName = data.getString("teamName");
				UserEntity toInvite = dataService.getUser(usernameToInvite);
				TeamEntity team = dataService.getTeam(teamName);
				if (toInvite.getTeam() == null) {
					if (toInvite != null && team != null) {
						if (isAdminOfTeam(token, teamName)) {
							if (dataService.addInvitationToUser(toInvite, team)) {
								result = new JSONObject("{\"success\": " +
										"\"true\"}");
							} else {
								result = returnServerError();
							}
						} else {
							result = returnNoRightsError();
						}
					} else {
						result = returnEmptyResult();
					}
				} else {
					result = returnNoRightsError();
				}
			} else {
				result = returnUserAlreadyInTeam();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	// TODO test
	@POST
	@Path("/invitations")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getInvitationsOfUser(JSONObject data) {
		String token;
		String username;
		JSONObject result;
		try {
			token = data.getString("token");
			username = data.getString("username");
			UserEntity user = dataService.getUser(username);
			if (validateToken(token)) {
				if (user != null) {
					List<String> invitations = new ArrayList<>();
					invitations = user.getInvitationsOfTeams();
					if (invitations.size() != 0) {
						StringBuilder stringBuilder = new StringBuilder();
						for (int i = 0; i < invitations.size(); i++) {
							if (i != (invitations.size() - 1)) {
								stringBuilder.append(invitations.get(i) + ",");
							} else {
								stringBuilder.append(invitations.get(i));
							}
						}
						result = new JSONObject("{\"success\": " +
								"\"true\", \"invitations\": \""
								+ stringBuilder.toString() + "\"}");
					} else {
						result = new JSONObject();
						result.put("success", "true");
						result.put("invitations", "null");
					}
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/edit/user")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject editUser(JSONObject userData) {
		JSONObject result;
		String token;
		String username;
		String firstName;
		String surname;
		String address;
		String phoneNr;
		String email;
		String birthday;
		try {
			token = userData.getString("token");
			if (validateToken(token)) {
				username = userData.getString("username");
				UserEntity fetchedUser = dataService.getUser(username);
				if (fetchedUser != null) {
					firstName = userData.getString("firstName");
					surname = userData.getString("surname");
					address = userData.getString("address");
					phoneNr = userData.getString("phoneNr");
					email = userData.getString("email");
					birthday = userData.getString("birthday");
					if (dataService.editUser(fetchedUser, firstName, surname,
							address, phoneNr, email, birthday)) {
						result
								= new JSONObject("{\"success\": \"true\"}");
					} else {
						result = returnServerError();
					}
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/users")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getAllUsers(JSONObject data) {
		String token;
		JSONObject result;
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				List<UserEntity> allUsers
						= (List<UserEntity>) dataService.getAllUsers();
				if (allUsers != null) {
					StringBuilder stringBuilder = new StringBuilder();
					stringBuilder.append("{");
					for (int i = 0; i < allUsers.size(); i++) {
						UserEntity user = allUsers.get(i);
						stringBuilder.append(user.getUsername() + ",");
					}
					stringBuilder.append("}");
					result = new JSONObject();
					result.put("success", "true");
					result.put("users", stringBuilder.toString());
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/edit/team")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject editTeam(JSONObject data) {
		JSONObject result;
		String token;
		String admin;
		String teamName;
		String teamDescription;
		try {
			token = data.getString("token");
			teamName = data.getString("teamName");
			if (validateToken(token)) {
				if (isAdminOfTeam(token, teamName)) {
					admin = data.getString("admin");
					if (admin != null) {
						teamDescription = data.getString("teamDescription");
						TeamEntity fetchedTeam = dataService.getTeam
								(teamName);
						if (fetchedTeam != null) {
							UserEntity teamsAdmin
									= dataService.getUser(admin);
							if (fetchedTeam.getAdmin().getUsername()
									.equals(admin)) {
								if (dataService.editTeam(fetchedTeam, teamName,
										teamDescription)) {
									result = new JSONObject("{\"success\" : " +
											"\"true\"}");
								} else {
									result = returnServerError();
								}
							} else {
								result = returnNoRightsError();
							}
						} else {
							result = returnEmptyResult();
						}

					} else {
						result = returnClientError();
					}
				} else {
					result = returnNoRightsError();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/team")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTeam(JSONObject team) {
		String token = null;
		String teamName = null;
		JSONObject result = null;
		try {
			token = team.getString("token");
			teamName = team.getString("team");
			if (token != null && teamName != null && !(token.equals(""))
					&& !(teamName.equals(""))) {
				if (validateToken(token)) {
					TeamEntity fetchedTeam = dataService.getTeam(teamName);
					if (fetchedTeam != null) {
						try {
							result = new JSONObject("{\"success\": " +
									"\"true\", " + "\"team\":"
									+ fetchedTeam.toString() + "}");
						} catch (JSONException e) {
							result = returnServerError();
						}
					} else {
						result = returnEmptyResult();
					}
				} else {
					result = returnNoRightsError();
				}
			} else {
				result = returnEmptyResult();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}


	@POST
	@Path("/teams")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTeams(JSONObject data) {
		JSONObject result;
		try {
			String token = data.getString("token");
			if (validateToken(token)) {
				List<TeamEntity> teams = (List<TeamEntity>) dataService.getAllTeams();
				if (teams != null) {
					StringBuilder stringBuilder = new StringBuilder();
					stringBuilder.append("{");
					for (int i = 0; i < teams.size(); i++) {
						stringBuilder.append(teams.get(i).getName() + ",");
					}
					stringBuilder.append("}");
					result = new JSONObject();
					try {
						result.put("success", "true");
						result.put("teams", stringBuilder.toString());
					} catch (JSONException e) {
						result = returnServerError();
					}
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/create/team")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONObject createTeam(JSONObject data) {
		JSONObject result;
		try {
			String token = data.getString("token");
			if (validateToken(token)) {
				String teamName = data.getString("teamName");
				TeamEntity fetchedTeam = dataService.getTeam(teamName);
				if (fetchedTeam == null) {
					String description = data.getString("teamDescription");
					String admin = data.getString("admin");
					if (dataService.createNewTeam(teamName, description,
							admin)) {
						UserEntity user = dataService.getUser(admin);
						String newToken = createUserToken("" + user.getId(),
								user.getUsername(), "" + user.getRole(),
								user.getTeam().getName());
						result = new JSONObject("{\"success\": \"true\", "
								+ "\"teamName\": \"" + teamName + "\", " +
								"\"token\": \"" + newToken + "\"}");
					} else {
						result = returnServerError();
					}
				} else {
					result = returnExistingError();
				}
			} else {
				result = returnNoRightsError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/user")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject getUser(JSONObject userData) {
		JSONObject result;
		String token = null;
		String username = null;
		try {
			token = userData.getString("token");
			username = userData.getString("username");
			if (token != null && !(token.equals("")) && username != null
					&& !(username.equals(""))) {
				if (validateToken(token)) {
					UserEntity fetchedUser = dataService.getUser(username);
					if (fetchedUser != null) {
						result = new JSONObject("{\"success\": \"true\", "
								+ " \"user\": " + fetchedUser.toSring() + "}");
					} else {
						result = returnEmptyResult();
					}
				} else {
					result = returnNoRightsError();
				}
			} else {
				result = returnClientError();
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	@POST
	@Path("/register/user")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject registerUser(JSONObject userInformation) {
		JSONObject result = null;
		String username = null;
		String password = null;
		String firstName = null;
		String surname = null;
		String email = null;
		String phoneNr = null;
		String address = null;
		String birthday = null;
		try {
			// Get values of the incoming json object.
			username = userInformation.getString("username");
			password = userInformation.getString("password");
			firstName = userInformation.getString("firstName");
			surname = userInformation.getString("surname");
			email = userInformation.getString("email");
			phoneNr = userInformation.getString("phoneNr");
			address = userInformation.getString("address");
			birthday = userInformation.getString("birthday");
		} catch (JSONException e) {
			result = returnClientError();
		}
		if (dataService.getUser(username) == null) {
			dataService.registerUser(username, password, firstName, surname,
					email, phoneNr, address, birthday);
			String jsoInfo = "{\"success\": \"true\"}";
			try {
				result = new JSONObject(jsoInfo);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			String jsonInfo = "{\"success\": \"false\"}";
			try {
				result = new JSONObject(jsonInfo);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject loginUser(JSONObject userInformation) {
		String username;
		String password;
		JSONObject result;
		String jsonInfo;
		String token;
		try {
			username = userInformation.getString("username");
			password = userInformation.getString("password");
			if (dataService.login(username, password)) {
				UserEntity user = dataService.getUser(username);
				if (user.getTeam() != null) {
					token = createUserToken("" + user.getId(), username,
							"" + user.getRole(),
							user.getTeam().getName());
				} else {
					token = createUserToken("" + user.getId(), username,
							"" + user.getRole(), "null");
				}
				// Append all needed data to response body
				jsonInfo = "{\"success\": \"true\", \"token\": \"" + token +
						"\", " + "\"user\": " + user.toSring() + "}";
				result = new JSONObject(jsonInfo);

			} else {
				jsonInfo = "{\"success\": \"false\"}";
				result = new JSONObject(jsonInfo);
			}
		} catch (JSONException e) {
			result = returnClientError();
		}
		return result;
	}

	private JSONObject returnExistingError() {
		try {
			return new JSONObject("{\"success\": \"false\", \"reason\": " +
					"\"Die von Ihnen angeforderte Aktion konnte nicht " +
					"ausgeführt werden, da die Daten schon existieren. " +
					"Versuchen Sie einen anderen Bezeichner!\"}");
		} catch (JSONException exc) {
			return null;
		}
	}

	private JSONObject returnEmptyResult() {
		try {
			JSONObject result = new JSONObject("{\"success\": \"false\"," +
					" \"reason\": \"Die angefragten Daten existieren " +
					"nicht!\"}");
			return result;
		} catch (JSONException e) {
			return null;
		}
	}

	private JSONObject returnNoRightsError() {
		try {
			JSONObject result = new JSONObject("{\"success\": \"false\", " +
					"\"reason\": \"Die Berechtigung für diese Aktion ist " +
					"nicht gewährleistet!\n" +
					"Entweder Ihre Session ist abgelaufen oder Sie haben " +
					"nicht die nötigen Rechte für die Aktion!\n" +
					"Bitte loggen Sie sich erneut ein und versuchen Sie es" +
					".\"}");
			return result;
		} catch (JSONException e) {
			return null;
		}
	}

	private JSONObject returnClientError() {
		try {
			return new JSONObject("{\"success\": \"false\", " +
					"\"reason\": \"Falsche Angaben im Request! Client " +
					"zeigt falsches Verhalten!\"}");
		} catch (JSONException e) {
			return null;
		}
	}

	private JSONObject returnInternalError() {
		try {
			return new JSONObject("{\"success\": \"false\", " +
					"\"reason\": \"Interner Fehler! Die Aktion konnte nicht " +
					"beendet werden!\"}");
		} catch (JSONException e) {
			return null;
		}
	}

	private JSONObject returnServerError() {
		try {
			return new JSONObject("{\"success\": \"false\", " +
					"\"reason\": \"Der Server zeigt falsches Verhalten! " +
					"Bitte melden Sie dies an den Administrator unter " +
					"grum02@gw.uni-passau.de.\"}");
		} catch (JSONException e) {
			return null;
		}
	}

	private JSONObject returnUserAlreadyInTeam() {
		try {
			return new JSONObject("{\"success\": \"false\", " +
					"\"reason\": \"Der eingeladene Nutzer ist bereits in " +
					"einem Team.\"}");
		} catch (JSONException e) {
			return null;
		}
	}

	private JSONObject returnUpdatedMessage() {
		try {
			return new JSONObject("{\"success\": \"false\", \"reason\":" +
					" \"Keine Änderungen vorhanden!\"}");
		} catch (JSONException e) {
			return null;
		}
	}

	private String createUserToken(String userid, String username,
								   String userRole, String teamName) {
		long currentMilliseconds = System.currentTimeMillis();
		Date creationTime = new Date(currentMilliseconds);
		Date expireTime = new Date(currentMilliseconds + EXPIRE_TIME);
		String token = null;
		try {
			// Create HMAC signer
			JWSSigner signer = new MACSigner(SHARED_SECRET);

			// Prepare JWT
			JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
					.audience("users")
					.subject("authentication") // Defines for what the token is used
					.jwtID(userid)    // The user id is the id for the token
					.issueTime(creationTime)    // The creation time of the token
					.expirationTime(expireTime)    // The time the token expires
					.claim("name", username) // Username
					.claim("role", userRole) // The role of the user
					.claim("team", teamName) // The team name of the user
					.build();

			// Combine header and claims
			SignedJWT signedJWT
					= new SignedJWT(new JWSHeader(JWSAlgorithm.HS256),
					claimsSet);

			// Sign the token
			signedJWT.sign(signer);

			// Finish token
			token = signedJWT.serialize();
		} catch (KeyLengthException e) {
			token = null;
		} catch (JOSEException e) {
			token = null;
		}
		return token;
	}

	private byte[] generateSharedSecret() {
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

	private boolean validateToken(String token) {
		boolean result = false;
		if (token != null) {
			try {
				JWSVerifier verifier = new MACVerifier(SHARED_SECRET);
				SignedJWT jwt = SignedJWT.parse(token);
				// Verify token
				result = jwt.verify(verifier);
			} catch (JOSEException e) {
				// Do nothing
			} catch (ParseException e) {
				// Do nothing
			}
		}
		return result;
	}

	private boolean isAdminOfTeam(String token, String teamNameSent) {
		boolean result = false;
		if (token != null) {
			try {
				JWSVerifier verifier = new MACVerifier(SHARED_SECRET);
				SignedJWT jwt = SignedJWT.parse(token);
				JWTClaimsSet claims = jwt.getJWTClaimsSet();
				String username = (String) claims.getClaim("name");
				String userRole = (String) claims.getClaim("role");
				String teamName = (String) claims.getClaim("team");
				UserEntity user = dataService.getUser(username);
				if (user != null && userRole.equals("" + user.getRole()) &&
						teamName.equals(teamNameSent)) {
					result = true;
				}
			} catch (JOSEException e) {
				// Do nothing
			} catch (ParseException e) {
				// Do nothing
			}
		}
		return result;
	}

}