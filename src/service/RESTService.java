package service;

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

/**
 * This is the communication interface between the clients and the server.
 * It uses the construct of a REST service which means it listens to HTTP
 * requests with the specific identifier of it's resources and call the logic
 * before it answers with the specific JSON data.
 */
@Path("/pmservice")
public class RESTService {

	// Needed for proofing if a client sent a request to a team to join or a
	// team administrator invites a new user to join a team.
	private final String REQUEST = "request";
	private final String INVITATION = "invitation";
	private DataService dataService = new DataService();
	// A random secret created when this instance gets created and used for
	// creating the user tokens to authenticate at server side.
	public static final byte[] SHARED_SECRET = generateSharedSecret();
	// Expiration time of the tokens.
	public static final long EXPIRE_TIME = 900000; // Within a 15 minutes
								                   // period a token is valid.

	//--------------------------------------------------------------------------
	// Team methods

	/**
	 * Listens for a client's request to create a new team.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with information about the success or failure of
	 * the operation.
	 */
	@POST
	@Path("/create/team")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject createTeam(JSONObject data) {
		return TeamHelper.createTeam(data, dataService);
	}

	/**
	 * Listens for a client's request to get the news of a team.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with the news of a team.
	 */
	@POST
	@Path("/team/news")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTeamsNews(JSONObject data) {
		return TeamHelper.getTeamsNews(data, dataService);
	}

	/**
	 * Listens for a client's request to get all tasks of a team.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with all the task data of a team.
	 */
	@POST
	@Path("/team/tasks")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTeamsTasks(JSONObject data) {
		return TeamHelper.getTeamsTasks(data, dataService);
	}

	/**
	 * Listens for a client's request to delete a specific team.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSON object with information about the success or failure of
	 * the operation.
	 */
	@POST
	@Path("/delete/team")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject deleteTeam(JSONObject data) {
		return TeamHelper.deleteTeam(data, dataService);
	}

	/**
	 * Listens for a client's request to edit the membership of a specific team.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with the information about the success or failure
	 * of this operation.
	 */
	@POST
	@Path("member/edit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject editTeamMember(JSONObject data) {
		return TeamHelper.editTeamMember(data, dataService);
	}

	/**
	 * Listens for a client's request to get all members of a team.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with all members of a specific team.
	 */
	@POST
	@Path("/team/members")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTeamMembers(JSONObject data) {
		return TeamHelper.getTeamMembers(data, dataService);
	}

	/**
	 * Listens to a client's request to edit a specific team.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with information about the success or failure of
	 * this operation.
	 */
	@POST
	@Path("/edit/team")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject editTeam(JSONObject data) {
		return TeamHelper.editTeam(data, dataService);
	}

	/**
	 * Listens to a client's request to get the data of a specific team.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with the team's data.
	 */
	@POST
	@Path("/team")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTeam(JSONObject data) {
		return TeamHelper.getTeam(data, dataService);
	}

	/**
	 * Listens to a client's request to get all team names in the system.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with all the names of the teams in the system.
	 */
	@POST
	@Path("/teams")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTeams(JSONObject data) {
		return TeamHelper.getTeams(data, dataService);
	}

	/**
	 * Listens to a client's request of a user to join a specific team and
	 * adds this request to the team.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with the information about the success or failure
	 * of this operation.
	 */
	@POST
	@Path("/request")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject addRequestToTeam(JSONObject data) {
		return TeamHelper.addRequestToTeam(data, dataService);
	}

	/**
	 * Listens to a client's request to get all the users' requests to join a
	 * team of this specific team.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with all the requests of users of a specific team.
	 */
	@POST
	@Path("/requests")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getRequestsOfTeam(JSONObject data) {
		return TeamHelper.getRequestsOfTeam(data, dataService);
	}

	//--------------------------------------------------------------------------
	// User methods

	/**
	 * Listens to a client's login request.
	 *
	 * @param data The JSON data a client sent.
	 *
	 * @return A JSONObject. If the login was successful, it contains a JWT
	 * for the authentication at the server side and if the login wasn't
	 * successful it just informs the client about that.
	 */
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject loginUser(JSONObject data) {
		return UserHelper.loginUser(data, dataService);
	}

	/**
	 * Listens to a client's request to get all the chats of a specific user.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with all the chats of a user.
	 */
	@POST
	@Path("user/chats")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject getUsersChats(JSONObject data) {
		return UserHelper.getUsersChats(data, dataService);
	}

	/**
	 * Listens to a client's request to get all the projects a specific user
	 * is involved in.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with all the projects of a specific user.
	 */
	@POST
	@Path("/user/projects")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getUsersProjects(JSONObject data) {
		return UserHelper.getUsersProjects(data, dataService);
	}

	/**
	 * Listens to a client's request to get all the tasks of a specific user.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with all the tasks a specific user is working on.
	 */
	@POST
	@Path("/user/tasks")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getUsersTasks(JSONObject data) {
		return UserHelper.getUsersTasks(data, dataService);
	}

	/**
	 * Listens to a team administrators request to invite a specific user to
	 * join his or her team.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with information about the success or failure of
	 * this operation.
	 */
	@POST
	@Path("/invite")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject inviteUserToTeam(JSONObject data) {
		return UserHelper.inviteUserToTeam(data, dataService);
	}

	/**
	 * Listens to the request of a client to get all teams' invitations a user
	 * got to this point of time.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with all invitations a specific user has received
	 * yet.
	 */
	@POST
	@Path("/invitations")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getInvitationsOfUser(JSONObject data) {
		return UserHelper.getInvitationsOfUser(data, dataService);
	}

	/**
	 * Listens t a client's request to get all the tributes a specific user has
	 * received yet.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with all the tributes of a specific user.
	 */
	@POST
	@Path("/user/tributes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getUsersTribute(JSONObject data) {
		return UserHelper.getUsersTribute(data, dataService);
	}

	/**
	 * Listens to a client's request to set the answer if a user participates
	 * in a specific appointment.
	 *
	 * @param data THe JSON data sent by a client.
	 *
	 * @return A JSONObject with the information about the success or failure
	 * of this operation.
	 */
	@POST
	@Path("appointment/answer/participation")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject saveUsersParticipation(JSONObject data) {
		return UserHelper.saveUsersParticipation(data,
				dataService);
	}

	/**
	 * Listens to a client's request to remove a user from a team.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with the information about the success or failure
	 * of this operation.
	 */
	@POST
	@Path("/leave/team")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject leaveTeam(JSONObject data) {
		return UserHelper.leaveTeam(data, dataService);
	}

	/**
	 * Listens to the request of a client to edit a specific user's data.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with information about the succes or failure of
	 * this operation.
	 */
	@POST
	@Path("/edit/user")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject editUser(JSONObject data) {
		return UserHelper.editUser(data, dataService);
	}

	/**
	 * Listens to a client's request to get all usernames existing in the
	 * system.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with all usernames of this system.
	 */
	@POST
	@Path("/users")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getAllUsers(JSONObject data) {
		return UserHelper.getAllUsers(data, dataService);
	}

	/**
	 * Listens to a request of a client to get the data of a specific user.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with all the data of a specific user
	 * (except password).
	 */
	@POST
	@Path("/user")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject getUser(JSONObject data) {
		return UserHelper.getUser(data, dataService);
	}

	/**
	 * Listens to a client's request to register a new user in the system.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with information about success or failure of this
	 * operation.
	 */
	@POST
	@Path("/register/user")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject registerUser(JSONObject data) {
		return UserHelper.registerUser(data, dataService);
	}

	//--------------------------------------------------------------------------
	// Project methods

	/**
	 * Listens to a client's request to get all appointments of a specific
	 * project.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with all appointments' data of a specific project.
	 */
	@POST
	@Path("/project/appointments")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getProjectsAppointments(JSONObject data) {
		return ProjectHelper.getProjectsAppointment(data,
				dataService);
	}

	/**
	 * Listens to a client's request to edit a bunch of users' membership of
	 * a specific project.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with information about the success or failure of
	 * this operation.
	 */
	@POST
	@Path("/edit/project/membership")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject editProjectMembership(JSONObject data) {
		return ProjectHelper.editProjectMemebership(data,
				dataService);
	}

	/**
	 * Listens to a client's request to get all usernames of a specific
	 * project's members.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with all usernames of the members of a specific
	 * project.
	 */
	@POST
	@Path("/project/members")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getProjectMembers(JSONObject data) {
		return ProjectHelper.getProjectMembers(data,
				dataService);
	}

	/**
	 * Listens to a client's request to delete a specific project.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with information about the success or failure of
	 * this operation.
	 */
	@POST
	@Path("/delete/project")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject deleteProject(JSONObject data) {
		return ProjectHelper.deleteProject(data, dataService);
	}

	/**
	 * Listens to a client's request
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with the information of success or failure of
	 * this operation.
	 */
	@POST
	@Path("/edit/project")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject editProject(JSONObject data) {
		return ProjectHelper.editProject(data, dataService);
	}

	/**
	 * Listens to a client's request to get the data of a specific project.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with a specific project's data.
	 */
	@POST
	@Path("/project")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getProject(JSONObject data) {
		return ProjectHelper.getProject(data, dataService);
	}

	/**
	 * Listens to a client's request to get all project names of a specific
	 * team.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with all project names of a specific team.
	 */
	@POST
	@Path("/team/projects")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getAllProjects(JSONObject data) {
		return ProjectHelper.getAllProjects(data, dataService);
	}

	/**
	 * Listens to a client's request to create a new project in a specific team.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with information about success or failure of this
	 * operation.
	 */
	@POST
	@Path("/create/project")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject createProject(JSONObject data) {
		return ProjectHelper.createProject(data, dataService);
	}

	//--------------------------------------------------------------------------
	// Appointment methods

	/**
	 * Listens to a client's request to get the data of a specific appointment.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with the data of a specific appointment.
	 */
	@POST
	@Path("/appointment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getAppointment(JSONObject data) {
		return AppointmentHelper.getAppointment(data, dataService);
	}

	/**
	 * Listens to a client's request to delete a specific appointment.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with information about success or failure of this
	 * operation.
	 */
	@POST
	@Path("/delete/appointment")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject deleteAppointment(JSONObject data) {
		return AppointmentHelper.deleteAppointment(data,
				dataService);
	}

	/**
	 * Listens to a client's request to edit the data of a specific appointment.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with information about the success or failure of
	 * this operation.
	 */
	@POST
	@Path("/edit/appointment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject editAppointment(JSONObject data) {
		return AppointmentHelper.editAppointment(data,
				dataService);
	}

	/**
	 * Listens to a client's request to create a new appointment in the system.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with information about success or failure of this
	 * operation.
	 */
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

	/**
	 * Listens to a client's request to delete a specific task.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with information about the success or failure of
	 * this operation.
	 */
	@POST
	@Path("/delete/task")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject deleteTask(JSONObject data) {
		return TaskHelper.deleteTask(data, dataService);
	}

	/**
	 * Listens to a client's request to edit the data of a specific task.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with information about the success or failure of
	 * this operation.
	 */
	@POST
	@Path("/edit/task")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject editTask(JSONObject data) {
		return TaskHelper.editTask(data, dataService);
	}

	/**
	 * Listens to a client's request to get the data of a specific taks.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with the data of a specific task.
	 */
	@POST
	@Path("/task")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTask(JSONObject data) {
		return TaskHelper.getTask(data, dataService);
	}

	/**
	 * Listens to a client's request to create a new task.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with the information of success or failure of
	 * this operation.
	 */
	@POST
	@Path("/create/task")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject createTask(JSONObject data) {
		return TaskHelper.createTask(data, dataService);
	}

	//--------------------------------------------------------------------------
	// Chat methods

	/**
	 * Listens to a client's request to delete a specific chat.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with information about success or failure of this
	 * operation.
	 */
	@POST
	@Path("/delete/chat")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject deleteChat(JSONObject data) {
		return ChatHelper.deleteChat(data, dataService);
	}

	/**
	 * Listens to a client's request to get all messages of a specific chat.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with the data of a chat's messages.
	 */
	@POST
	@Path("/chat/messages")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject getChatsMessages(JSONObject data) {
		return ChatHelper.getChatsMessages(data, dataService);
	}

	/**
	 * Listens to a client's request to create a new chat.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with information about success or failure of this
	 * operation.
	 */
	@POST
	@Path("/create/chat")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject createChat(JSONObject data) {
		return ChatHelper.createChat(data, dataService);
	}

	//--------------------------------------------------------------------------
	// Message methods

	/**
	 * Listens to a client's request to get the new messages of a user's chats.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with all the new messages' data a specific user
	 * received.
	 */
	@POST
	@Path("/messages/new")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getNewMessagesForUser(JSONObject data) {
		return MessageHelper.getNewMessagesForUser(data,
				dataService);
	}

	/**
	 * Listens to a client's request to save a new message in a specific chat.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with information about the success or failure of
	 * this operation.
	 */
	@POST
	@Path("/receive/message")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject receiveMessage(JSONObject data) {
		return MessageHelper.receiveMessage(data, dataService);
	}

	//--------------------------------------------------------------------------
    // Register methods

	/**
	 * Listens to a client's request to delete a specific register.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with information about the success or failure of
	 * this operation.
	 */
	@POST
	@Path("/delete/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject delteRegister(JSONObject data) {
		return RegisterHelper.deleteRegister(data, dataService);
	}

	/**
	 * Listens to a client's request to get the data of a specific register.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with the data of a specific register.
	 */
	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getRegister(JSONObject data) {
		return RegisterHelper.getRegister(data, dataService);
	}

	/**
	 * Listens to a client's request to edit the data of a specific register.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with the information about success or failure of
	 * this operation.
	 */
	@POST
	@Path("/edit/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject editRegister(JSONObject data) {
		return RegisterHelper.editRegister(data, dataService);
	}

	/**
	 * Listens to a client's request to create a new register.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with information about success or failure of this
	 * operation.
	 */
	@POST
	@Path("/create/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject createRegister(JSONObject data) {
		return RegisterHelper.createRegister(data, dataService);
	}

	/**
	 * Listens to a client's request to get the data of all registers of a
	 * specific team.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with the data of all registers of a specific team.
	 */
	@POST
	@Path("/team/registers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getRegisters(JSONObject data) {
		return RegisterHelper.getRegisters(data, dataService);
	}

	//--------------------------------------------------------------------------
	// General needed methods for services etc.

	/**
	 * Listens to a client's request with which a user changes his or her
	 * password.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with information about the success or failure of
	 * this operation.
	 */
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

	/**
	 * Listens to a client's request with which the system will generate a
	 * random secure PIN for a specific user due to the fact that the current
	 * password has been forgotten. The PIN is set as the new password and
	 * sent to the user's email address.
	 *
	 * @param data The JSON data sent by a client.
	 */
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

	/**
	 * Listens to a request to get all the actions belonging to a
	 * specific team for the current day. No user token is needed cause this
	 * resource is only called by a background thread on the client side.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with all the specific team's data relevant for the
	 * current week.
	 */
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

	/**
	 * Listens to a client's request to get all news of a team for the
	 * current week but using a token to authenticate and validate the
	 * request due to the active call of the client.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with all the specific team's data relevant for
	 * the current week.
	 */
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

	/*
	Extracts all the dates of the current week on which there will happen
	actions of a specific team and returns them as a JSONObject.
	 */
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

	/*
	Extracts all the birthdays of a team's members which will be in the
	current week and returns them as a JSONObject.
	 */
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

	/*
	Extracts all appointments which deadlines are set for the current week
	and returns them as a JSONObject.
	 */
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

	/*
	Extracts the tasks -- of which the deadlines are set for the current week --
	of a specific team and returns them as a JSONObject.
	 */
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

	/*
	Extracts all the projects of a team with their deadlines set during the
	current week and returns them as a JSONObject.
	 */
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

	/*
	Checks if a point of time is happening during the current week. Returns
	true if this is the case and false if not.
	 */
	private boolean deadlineIsCurrently(Calendar deadline, Calendar currentDate) {
		boolean result = false;
		if (deadline.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)
				&& deadline.get(Calendar.DAY_OF_YEAR)
				== currentDate.get(Calendar.DAY_OF_YEAR)) {
			result = true;
		}
		return result;
	}

	/**
	 * Listens to a client's request to get all participation statistics
	 * belonging to a specific project.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with all participation statistics of the
	 * project's members saved in it.
	 */
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

	/*
	Combines the user and his/her participation answers for every appointment in a
	JSONObject to prepare the numbers for the statistics on the client side.
	It returns a List of JSONObjects
	*/
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

	/**
	 * Listens to the request of a client to remove a user from the system.
	 *
	 * @param data The JSON data sent by a client.
	 *
	 * @return A JSONObject with information about the success or failure of
	 * this operation.
	 */
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

	/**
	 * Listens to a client's request to answer a specific invitation or
	 * request for joining a team. If the answer is to let the user join, the
	 * user will be added to the team and if the answer is a rejection the
	 * request/invitation will be deleted.
	 *
	 * @param data The data sent by a client.
	 *
	 * @return A JSONObject with the information about the success or failure
	 * of this operation.
	 */
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

	/*
	Decides whether the system has to deal with a request or an invitation to
	 join a team.
	 */
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

	/*
	Answers an invitation of a team. If the user wants to join it he/she will
	 be added to it. If not the invitation is deleted.
	 */
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

	/*
	Answers a request of a user. It can only be done by the administrator of
	the specific team. If he/she accepts the request the user will be added
	to the team and if not the request is deleted here.
	 */
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

	/*
	Generates a random secret and returns it as a byte array. the secret is
	saved in a file and will be just created if the file is empty.
	 */
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