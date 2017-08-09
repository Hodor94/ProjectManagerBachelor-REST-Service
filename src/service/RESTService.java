package service;

/**
 * Created by Raphael on 15.06.2017.
 */

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import entity.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jettison.json.JSONException;
import org.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import io.jsonwebtoken.*;
import java.util.Date;

// TODO watch UserEntity and make with:
// TODO: MessageEntity - alles - Überlegung: Wie identifizieren
// TODO: StatisticEntity - alles - Überlegung: Wie identifizieren
// Todo: POST, UPDATE UND DELETE Methoden

@JsonSerialize
@Path("/pmservice")
public class RESTService {

	private DataService dataService = new DataService();
	private SecretKey secretKey;

	// TODO delete
	public SecretKey getSecretKey() {
		return secretKey;
	}

	// TEST
	private Calendar testDate = Calendar.getInstance();
	private SimpleDateFormat formatter;

	@Path("/setUp/")
	public String setUp() {
		DataService service = new DataService();
		service.setUpDataForRESTService();
		return "Success";
	}

	@GET
	@Path("/chat/{chatname}/{teamname}/{creator}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getChat(@PathParam("chatname") String chatName,
							  @PathParam("teamname") String teamName,
							  @PathParam("creator") String creatorName) {
		ChatEntity chat = dataService.getChat(chatName, teamName, creatorName);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
		JSONObject result;
		if (chat != null) {
			result = mapper.convertValue(chat.toString(), JSONObject.class);
		} else {
			result = mapper.convertValue(null, JSONObject.class);
		}
		return result;
	}

	// TODO Orientier dich HIER
	@GET
	@Path("/task/{taskName}/{teamName}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTask(@PathParam("taskName") String taskName,
							  @PathParam("teamName") String teamName) {
		TaskEntity task = dataService.getTask(taskName, teamName);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
		JSONObject result;
		if (task != null) {
			result = mapper.convertValue(task.toString(), JSONObject.class);
		} else {
			result = mapper.convertValue(null, JSONObject.class);
		}
		return result;
	}

	@GET
	@Path("/task/{taskName}/{teamName}/worker")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTasksWorker(@PathParam("taskName") String taskName,
								 @PathParam("teamName") String teamName) {
		TaskEntity task = dataService.getTask(taskName, teamName);
		String result;
		if (task != null && task.getWorker() != null) {
			result = task.getWorker().toSring();
		} else {
			result = "null";
		}
		return result;
	}

	@GET
	@Path("/task/{taskName}/{teamName}/team")
	public String getTasksTeam(@PathParam("taskName") String taskName,
							   @PathParam("teamName") String teamName) {
		TaskEntity task = dataService.getTask(taskName, teamName);
		String result;
		if (task != null && task.getTeam() != null) {
			result = task.getTeam().toString();
		} else {
			result = "null";
		}
		return result;
	}

	@GET
	@Path("/chat/{chatname}/{teamname}/{creator}/messages")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getChatsMessages(@PathParam("chatname") String chatName,
									   @PathParam("teamname") String teamName,
									   @PathParam("creator") String creatorName) {
		StringBuilder stringBuilder;
		ObjectMapper mapper;
		JSONObject result;
		ChatEntity chat = dataService.getChat(chatName, teamName, creatorName);
		if (chat != null && chat.getMessages().size() != 0) {
			stringBuilder = new StringBuilder();
			stringBuilder.append("{\"messages\": [");
			mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,
					false);
			ArrayList<MessageEntity> messages
					= new ArrayList<MessageEntity>(chat.getMessages());
			for (int i = 0; i < messages.size(); i++) {
				if (i != (messages.size() - 1)) {
					stringBuilder.append(messages.get(i).toString() + ", ");
				} else {
					stringBuilder.append(messages.get(i).toString() + "]}");
				}
			}
			result = mapper.convertValue(stringBuilder.toString(),
					JSONObject.class);
		} else {
			result = null;
		}
		return result;
	}

	@GET
	@Path("//chat/{chatname}/{teamname}/{creator}/users")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getChatsUsers(@PathParam("chatname") String chatName,
									@PathParam("teamname") String teamName,
									@PathParam("creator") String creatorName) {
		StringBuilder stringBuilder;
		ObjectMapper mapper;
		JSONObject result;
		ChatEntity chat = dataService.getChat(chatName, teamName, creatorName);
		if (chat != null && chat.getUsers().size() != 0) {
			stringBuilder = new StringBuilder();
			stringBuilder.append("{\"users\": [");
			mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,
					false);
			ArrayList<UserEntity> users
					= new ArrayList<UserEntity>(chat.getUsers());
			for (int i = 0; i < users.size(); i++) {
				if (i != (users.size() - 1)) {
					stringBuilder.append(users.get(i).toSring() + ", ");
				} else {
					stringBuilder.append(users.get(i).toSring() + "]}");
				}
			}
			result = mapper.convertValue(stringBuilder.toString(),
					JSONObject.class);
		} else {
			result = null;
		}
		return result;
	}

	// TODO: rework
	@GET
	@Path("/users/")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray getUsers() {
		dataService.getAllUsers();
		return null;
	}

	@GET
	@Path("/user/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getUser(@PathParam("username") String username) {
		UserEntity user = dataService.getUser(username);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
		JSONObject result;
		if (user != null) {
			result = mapper.convertValue(user.toSring(), JSONObject.class);
		} else {
			result = mapper.convertValue(null, JSONObject.class);
		}
		return result;
	}

	@GET
	@Path("/user/{username}/tasks")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getUsersTasks(@PathParam("username") String username) {
		DataService service = new DataService();
		StringBuilder stringBuilder;
		ObjectMapper mapper;
		JSONObject result;
		UserEntity user = service.getUser(username);
		if (user != null && user.getTasks() != null) {
			stringBuilder = new StringBuilder();
			mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,
					false);
			stringBuilder.append("{\"tasks\": [");
			ArrayList<TaskEntity> tasks
					= new ArrayList<TaskEntity>(user.getTasks());
			for (int i = 0; i < tasks.size(); i++) {
				if (i != (tasks.size() - 1)) {
					stringBuilder.append(tasks.get(i).toString() + ", ");
				} else {
					stringBuilder.append(tasks.get(i).toString() + "]}");
				}
			}
			result = mapper.convertValue(stringBuilder.toString(),
					JSONObject.class);
		} else {
			result = null;

		}
		return result;
	}

	// TODO Orientier dich hier für arrays
	@GET
	@Path("/user/{username}/appointments")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getUsersAppointments(@PathParam("username") String username) {
		DataService service = new DataService();
		StringBuilder stringBuilder;
		ObjectMapper mapper;
		JSONObject result;
		UserEntity user = service.getUser(username);
		if (user != null && (user.getAppointmentsTakingPart().size() != 0)) {
			stringBuilder = new StringBuilder();
			mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,
					false);
			ArrayList<AppointmentEntity> appointments
					= new ArrayList<AppointmentEntity>(
					user.getAppointmentsTakingPart());
			stringBuilder.append("{\"appointments\": [");
			for (int i = 0; i < appointments.size(); i++) {
				if (i != (appointments.size() - 1)) {
					stringBuilder.append(appointments.get(i).toString() + ", ");
				} else {
					stringBuilder.append(appointments.get(i).toString() + "]}");
				}
			}
			result = mapper.convertValue(stringBuilder.toString(),
					JSONObject.class);
		} else {
			result = null;
		}
		return result;
	}

	@GET
	@Path("/user/{username}/projects")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getUsersProjects(@PathParam("username") String username) {
		DataService service = new DataService();
		StringBuilder stringBuilder;
		ObjectMapper mapper;
		JSONObject result;
		UserEntity user = service.getUser(username);
		if (user != null && (user.getProjectsTakingPart().size() != 0)) {
			stringBuilder = new StringBuilder();
			mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,
					false);
			stringBuilder.append("{\"projects\": [");
			ArrayList<ProjectEntity> projects
					= new ArrayList<ProjectEntity>
					(user.getProjectsTakingPart());
			for (int i = 0; i < projects.size(); i++) {
				if (i != (projects.size() - 1)) {
					stringBuilder.append(projects.get(i).toString() + ", ");
				} else {
					stringBuilder.append(projects.get(i) + "]}");
				}
			}
			result = mapper.convertValue(stringBuilder.toString(),
					JSONObject.class);
		} else {
			result = null;
		}
		return result;
	}

	@GET
	@Path("/user/{username}/statistics")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getUsersStatistics(
			@PathParam("username") String username) {
		DataService service = new DataService();
		StringBuilder stringBuilder;
		ObjectMapper mapper;
		JSONObject result;
		UserEntity user = service.getUser(username);
		if (user != null && (user.getStatistics().size() != 0)) {
			stringBuilder = new StringBuilder();
			mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,
					false);
			ArrayList<StatisticEntity> statistics
					= new ArrayList<StatisticEntity>(user.getStatistics());
			stringBuilder.append("{\"statistics\": [");
			for (int i = 0; i < statistics.size(); i++) {
				if (i != (statistics.size() - 1)) {
					stringBuilder.append(statistics.get(i).toString() + ", ");
				} else {
					stringBuilder.append(statistics.get(i).toString() + "]}");
				}
			}
			result = mapper.convertValue(stringBuilder.toString(),
					JSONObject.class);
		} else {
			result = null;
		}
		return result;
	}

	@GET
	@Path("/user/{username}/chats")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getUsersChats(@PathParam("username") String username) {
		DataService service = new DataService();
		StringBuilder stringBuilder;
		ObjectMapper mapper;
		JSONObject result;
		UserEntity user = service.getUser(username);
		if (user != null && user.getChats().size() != 0) {
			stringBuilder = new StringBuilder();
			mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,
					false);
			stringBuilder.append("{\"chats\": [");
			ArrayList<ChatEntity> chats
					= new ArrayList<ChatEntity>(user.getChats());
			for (int i = 0; i < chats.size(); i++) {
				if (i != (chats.size() - 1)) {
					stringBuilder.append(chats.get(i).toString() + ", ");
				} else {
					stringBuilder.append(chats.get(i).toString() + "]}");
				}
			}
			result = mapper.convertValue(stringBuilder.toString(),
					JSONObject.class);
		} else {
			result = null;
		}
		return result;
	}

	@GET
	@Path("/team/{teamName}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTeam(@PathParam("teamName") String teamName) {
		DataService service = new DataService();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
		JSONObject result;
		TeamEntity team = service.getTeam(teamName);
		if (team != null) {
			result = mapper.convertValue(team.toString(), JSONObject.class);
		} else {
			result = mapper.convertValue("null", JSONObject.class);
		}
		return result;
	}

	@GET
	@Path("/team/{teamName}/users")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTeamsUsers(@PathParam("teamName") String teamName) {
		DataService service = new DataService();
		StringBuilder stringBuilder;
		ObjectMapper mapper;
		JSONObject result;
		TeamEntity team = service.getTeam(teamName);
		if (team != null && team.getUsers().size() != 0) {
			stringBuilder = new StringBuilder();
			stringBuilder.append("{\"users\": [");
			mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,
					false);
			ArrayList<UserEntity> users =
					new ArrayList<UserEntity>(team.getUsers());
			for (int i = 0; i < users.size(); i++) {
				if (i != (users.size() - 1)) {
					stringBuilder.append(users.get(i).toSring() + ", ");
				} else {
					stringBuilder.append(users.get(i).toSring() + "]}");
				}
			}
			result = mapper.convertValue(stringBuilder.toString(),
					JSONObject.class);
		} else {
			result = null;
		}
		return result;
	}

	@GET
	@Path("/team/{teamName}/projects")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTeamsProjects(@PathParam("teamName") String teamName) {
		DataService service = new DataService();
		StringBuilder stringBuilder;
		ObjectMapper mapper;
		JSONObject result;
		TeamEntity team = service.getTeam(teamName);
		if (team != null && team.getProjects().size() != 0) {
			stringBuilder = new StringBuilder();
			stringBuilder.append("{\"projects\": [");
			mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,
					false);
			ArrayList<ProjectEntity> projects
					= new ArrayList<ProjectEntity>(team.getProjects());
			for (int i = 0; i < projects.size(); i++) {
				if (i != (projects.size() - 1)) {
					stringBuilder.append(projects.get(i).toString() + ", ");
				} else {
					stringBuilder.append(projects.get(i).toString() + "]}");
				}
			}
			result = mapper.convertValue(stringBuilder.toString(),
					JSONObject.class);
		} else {
			result = null;
		}
		return result;
	}

	@GET
	@Path("/team/{teamName}/tasks")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTeamsTasks(@PathParam("teamName") String teamName) {
		DataService service = new DataService();
		StringBuilder stringBuilder;
		ObjectMapper mapper;
		JSONObject result;
		TeamEntity team = service.getTeam(teamName);
		if (team != null && team.getTasks().size() != 0) {
			stringBuilder = new StringBuilder();
			stringBuilder.append("{\"tasks\": [");
			mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,
					false);
			ArrayList<TaskEntity> tasks
					= new ArrayList<TaskEntity>(team.getTasks());
			for (int i = 0; i < tasks.size(); i++) {
				if (i != (tasks.size() - 1)) {
					stringBuilder.append(tasks.get(i).toString() + ", ");
				} else {
					stringBuilder.append(tasks.get(i).toString() + "]}");
				}
			}
			result = mapper.convertValue(stringBuilder.toString(),
					JSONObject.class);
		} else {
			result = null;
		}
		return result;
	}

	@GET
	@Path("/team/{teamName}/registers")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTeamsRegisters(@PathParam("teamName") String teamName) {
		DataService service = new DataService();
		StringBuilder stringBuilder;
		ObjectMapper mapper;
		JSONObject result;
		TeamEntity team = service.getTeam(teamName);
		if (team != null && team.getRegisters().size() != 0) {
			stringBuilder = new StringBuilder();
			stringBuilder.append("{\"registers\": [");
			mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,
					false);
			ArrayList<RegisterEntity> registers
					= new ArrayList<RegisterEntity>(team.getRegisters());
			for (int i = 0; i < registers.size(); i++) {
				if (i != (registers.size() - 1)) {
					stringBuilder.append(registers.get(i).toString() + ", ");
				} else {
					stringBuilder.append(registers.get(i).toString() + "]}");
				}
			}
			result = mapper.convertValue(stringBuilder.toString(),
					JSONObject.class);
		} else {
			result = null;
		}
		return result;
	}

	@GET
	@Path("/team/{teamName}/chats")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTeamsChats(@PathParam("teamName") String teamName) {
		DataService service = new DataService();
		StringBuilder stringBuilder;
		ObjectMapper mapper;
		JSONObject result;
		TeamEntity team = service.getTeam(teamName);
		if (team != null && team.getChats().size() != 0) {
			stringBuilder = new StringBuilder();
			stringBuilder.append("{\"chats\": [");
			mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,
					false);
			ArrayList<ChatEntity> chats
					= new ArrayList<ChatEntity>(team.getChats());
			for (int i = 0; i < chats.size(); i++) {
				if (i != (chats.size() - 1)) {
					stringBuilder.append(chats.get(i).toString() + ", ");
				} else {
					stringBuilder.append(chats.get(i).toString() + "]}");
				}
			}
			result = mapper.convertValue(stringBuilder.toString(),
					JSONObject.class);
		} else {
			result = null;
		}
		return result;
	}

	@GET
	@Path("/team/{teamName}/admin")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTeamsAdmin(@PathParam("teamName") String teamName) {
		DataService service = new DataService();
		ObjectMapper mapper;
		JSONObject result;
		TeamEntity team = service.getTeam(teamName);
		if (team != null && team.getAdmin() != null) {
			mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,
					false);
			result = mapper.convertValue(team.getAdmin().toSring(),
					JSONObject.class);
		} else {
			result = null;
		}
		return result;
	}

	@GET
	@Path("/appointment/{projectName}/{appointmentId}/{teamName}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getAppointment
			(@PathParam("projectName") String projectName,
			 @PathParam("appointmentId") long appointmentId,
			 @PathParam("teamName") String teamName) {
		DataService service = new DataService();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
		JSONObject result;
		AppointmentEntity appointment = service.getAppointment(projectName,
				appointmentId, teamName);
		if (appointment != null) {
			result = mapper.convertValue(appointment.toString(),
					JSONObject.class);
		} else {
			result = mapper.convertValue(null, JSONObject.class);
		}
		return result;
	}

	@GET
	@Path("/appointment/{projectName}/{appointmentId}/{teamName}/project")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getAppointmentsProject(
			@PathParam("projectName") String projectName,
			@PathParam("appointmentId") long appointmentId,
			@PathParam("teamName") String teamName) {
		DataService service = new DataService();
		ObjectMapper mapper;
		JSONObject result;
		AppointmentEntity appointment
				= service.getAppointment(projectName, appointmentId, teamName);
		if (appointment != null && appointment.getProject() != null) {
			mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,
					false);
			result = mapper.convertValue(appointment.getProject().toString(),
					JSONObject.class);
		} else {
			result = null;
		}
		return result;
	}

	@GET
	@Path("/appointment/{projectName}/{appointmentId}/{teamName}/users")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getAppoinmentsUsers(
			@PathParam("projectName") String projectName,
			@PathParam("appointmentId") long appointmentId,
			@PathParam("teamName") String teamName
	) {
		DataService service = new DataService();
		StringBuilder stringBuilder;
		ObjectMapper mapper;
		JSONObject result;
		AppointmentEntity appointment =
				service.getAppointment(projectName, appointmentId, teamName);
		if (appointment != null
				&& appointment.getUserTakinPart().size() != 0) {
			stringBuilder = new StringBuilder();
			stringBuilder.append("{\"users\": [");
			mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,
					false);
			ArrayList<UserEntity> users
					= new ArrayList<UserEntity>(appointment.getUserTakinPart());
			for (int i = 0; i < users.size(); i++) {
				if (i != (users.size() - 1)) {
					stringBuilder.append(users.get(i).toSring() + ", ");
				} else {
					stringBuilder.append(users.get(i).toSring() + "]}");
				}
			}
			result = mapper.convertValue(stringBuilder.toString(),
					JSONObject.class);
		} else {
			result = null;
		}
		return result;
	}

	@GET
	@Path("/register/{registerName}/{teamName}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getRegister(
			@PathParam("registerName") String registerName,
			@PathParam("teamName") String teamName) {
		DataService service = new DataService();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
		JSONObject result;
		RegisterEntity register = service.getRegister(registerName, teamName);
		if (register != null) {
			result = mapper.convertValue(register.toString(), JSONObject.class);
		} else {
			result = mapper.convertValue(null, JSONObject.class);
		}
		return result;
	}

	@GET
	@Path("/register/{registerName}/{teamName}/users")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getRegistersUsers(
			@PathParam("registerName") String registerName,
			@PathParam("teamName") String teamName) {
		DataService service = new DataService();
		StringBuilder stringBuilder;
		ObjectMapper mapper;
		JSONObject result;
		RegisterEntity register = service.getRegister(registerName, teamName);
		if (register != null && register.getUsers().size() != 0) {
			stringBuilder = new StringBuilder();
			stringBuilder.append("{\"users\": [");
			mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,
					false);
			ArrayList<UserEntity> users
					= new ArrayList<UserEntity>(register.getUsers());
			for (int i = 0; i < users.size(); i++) {
				if (i != (users.size() - 1)) {
					stringBuilder.append(users.get(i).toSring() + ", ");
				} else {
					stringBuilder.append(users.get(i).toSring() + "]}");
				}
			}
			result = mapper.convertValue(stringBuilder.toString(),
					JSONObject.class);
		} else {
			result = null;
		}
		return result;
	}

	@GET
	@Path("/register/{registerName}/{teamName}/team")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRegistersTeam(
			@PathParam("registerName") String registerName,
			@PathParam("teamName") String teamName) {
		DataService service = new DataService();
		String result;
		RegisterEntity register = service.getRegister(registerName, teamName);
		if (register != null && register.getTeam() != null) {
			result = register.getTeam().toString();
		} else {
			result = "null";
		}
		return result;
	}

	@GET
	@Path("/project/{projectname}/{teamname}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getProject(@PathParam("projectname") String projectName,
								 @PathParam("teamname") String teamName) {
		DataService service = new DataService();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
		JSONObject result;
		ProjectEntity project = service.getProject(projectName, teamName);
		if (project != null) {
			result = mapper.convertValue(project.toString(), JSONObject.class);
		} else {
			result = mapper.convertValue(null, JSONObject.class);
		}
		return result;
	}

	@GET
	@Path("/project/{projectName}/{teamName}/appointments")
	public JSONObject getProjectsAppointments(
			@PathParam("projectName") String projectName,
			@PathParam("teamName") String teamName
	) {
		DataService service = new DataService();
		StringBuilder stringBuilder;
		ObjectMapper mapper;
		JSONObject result;
		ProjectEntity project = service.getProject(projectName, teamName);
		if (project != null && project.getAppointments().size() != 0) {
			stringBuilder = new StringBuilder();
			stringBuilder.append("{\"appointments\": [");
			mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,
					false);
			ArrayList<AppointmentEntity> appointments
					= new ArrayList<AppointmentEntity>
					(project.getAppointments());
			for (int i = 0; i < appointments.size(); i++) {
				if (i != (appointments.size() - 1)) {
					stringBuilder.append(appointments.get(i).toString() + ", ");
				} else {
					stringBuilder.append(appointments.get(i).toString() + "]}");
				}
			}
			result = mapper.convertValue(stringBuilder.toString(),
					JSONObject.class);
		} else {
			result = null;
		}
		return result;
	}

	@GET
	@Path("/project/{projectName}/{teamName}/users")
	public JSONObject getProjectsUsers(
			@PathParam("projectName") String projectName,
			@PathParam("teamName") String teamName
	) {
		DataService service = new DataService();
		StringBuilder stringBuilder;
		ObjectMapper mapper;
		JSONObject result;
		ProjectEntity project = service.getProject(projectName, teamName);
		if (project != null && project.getUsers().size() != 0) {
			stringBuilder = new StringBuilder();
			stringBuilder.append("{\"users\": [");
			mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,
					false);
			ArrayList<UserEntity> users
					= new ArrayList<UserEntity>(project.getUsers());
			for (int i = 0; i < users.size(); i++) {
				if (i != (users.size() - 1)) {
					stringBuilder.append(users.get(i).toString() + ", ");
				} else {
					stringBuilder.append(users.get(i).toString() + "]}");
				}
			}
			result = mapper.convertValue(stringBuilder.toString(),
					JSONObject.class);
		} else {
			result = null;
		}
		return result;
	}

	@GET
	@Path("/project/{projectName}/{teamName}/statistics")
	public JSONObject getProjectsStatistics(
			@PathParam("projectName") String projectName,
			@PathParam("teamName") String teamName
	) {
		DataService service = new DataService();
		StringBuilder stringBuilder;
		ObjectMapper mapper;
		JSONObject result;
		ProjectEntity project = service.getProject(projectName, teamName);
		if (project != null && project.getStatistics().size() != 0) {
			stringBuilder = new StringBuilder();
			stringBuilder.append("{\"statistics\": [");
			mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,
					false);
			ArrayList<StatisticEntity> statistics
					= new ArrayList<StatisticEntity>(project.getStatistics());
			for (int i = 0; i < statistics.size(); i++) {
				if (i != (statistics.size() - 1)) {
					stringBuilder.append(statistics.get(i).toString() + ", ");
				} else {
					stringBuilder.append(statistics.get(i).toString() + "]}");
				}
			}
			result = mapper.convertValue(stringBuilder.toString(),
					JSONObject.class);
		} else {
			result = null;
		}
		return result;
	}

	@GET
	@Path("/statistic/{teamName}/{projectName}/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getStatistic(
			@PathParam("projectName") String projectName,
			@PathParam("teamName") String teamName,
			@PathParam("username") String username) {
		DataService service = new DataService();
		JSONObject result;
		ObjectMapper mapper;
		StatisticEntity statistic = service.getStatisticOfUser(username,
				projectName, teamName);
		if (statistic != null) {
			mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,
					false);
			result = mapper.convertValue(statistic.toString(),
					JSONObject.class);
		} else {
			result = null;
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
		if (secretKey == null) {
			secretKey = dataService.generateSecretKey();
		}
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

	private JSONObject returnClientError() {
		try {
			return new JSONObject("{\"success\": \"false\", " +
					"\"error\": \"Falsche Angaben im Request! Client " +
					"zeigt falsches Verhalten!\"}");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String createUserToken(String userid, String username,
								   String userRole, String teamName) {
		long currentMilliseconds = System.currentTimeMillis();
		Date creationTime = new Date(currentMilliseconds);
		Date expireTime = new Date(currentMilliseconds + 900000);
		if (secretKey == null) {
			return "CREATION FAILED";
		} else {
			String jwt = Jwts.builder()
					.setAudience("users")  // Defines the audience the token is created for
					.setSubject("authentication") // Defines for what the token is used
					.setId(userid) // The user id is the id for the token
					.setIssuedAt(creationTime) // The creation time of the token
					.setExpiration(expireTime) // The time the token expires
					.claim("name", username) // username
					.claim("role", userRole) // The role of the user
					.claim("team", teamName) // the team name of the user, null
					// if he/she has none
					.signWith(                   // Signature of the token
							SignatureAlgorithm.HS256,
							Base64.getEncoder()
									.encodeToString(secretKey.getEncoded()))
					.compact(); // Finishes the creation of the token
			return jwt;
		}
	}


}

