package service.helper;

import entity.AppointmentEntity;
import entity.ProjectEntity;
import entity.TeamEntity;
import entity.UserEntity;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import service.DataService;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to receive data sent by  the client.
 * It extracts needed data and calls the specific methods to work with
 * ProjectEntity data in the database, manipulates it and returns it to the
 * RESTService object.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since version 1.0
 */
public class ProjectHelper {

	/**
	 * Gets all appointments of a project out of the database.
	 *
	 * @param data The data received from the client.
	 * @param dataService An instance of DataService to get and manipulate data.
	 *
	 * @return A JSONObject with all appointments of a project saved in it or
	 * an error message if there was a failure.
	 */
	public static JSONObject getProjectsAppointment(JSONObject data,
													DataService dataService) {
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
	 * Adds and removes users from a project in the database.
	 *
	 * @param data The data received from the client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return Returns a JSONObject with information about the result of this
	 * operation. An error message, if there was a failure and a success
	 * message if it worked fine.
	 */
	public static JSONObject editProjectMemebership(JSONObject data,
													DataService dataService) {
		JSONObject result;
		String token;
		String projectName;
		String teamName;
		String username;
		JSONArray usernames;
		ArrayList<String> usersToEdit = new ArrayList<>();
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
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
							result = ErrorCreator.returnNoRightsError();
						}
					} else {
						result = ErrorCreator.returnEmptyResult();
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
				result = ErrorCreator.returnNoRightsError();
			}
		} catch (JSONException e) {
			result = ErrorCreator.returnClientError();
		}
		return result;
	}

	/**
	 * Gets all users belonging to a specific project in the system.
	 *
	 * @param data The data received from a client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return A JSONObject with all members of a project or an error message
	 * if there was a failure.
	 */
	public static JSONObject getProjectMembers(JSONObject data,
											   DataService dataService) {
		JSONObject result;
		String token;
		String projectName;
		String teamName;
		String username;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
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
	 * Deletes a project from the database.
	 *
	 * @param data The data received from a client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return A JSONObject with information about the result of this
	 * operation. A success message if it worked fine or an error message if
	 * there was a failure.
	 */
	public  static JSONObject deleteProject(JSONObject data,
											DataService dataService) {
		JSONObject result;
		String token;
		String projectName;
		String teamName;
		String username;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
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
						result = ErrorCreator.returnNoRightsError();
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

	/**
	 * Gets a project entry out of the database, edits the data and saves it
	 * again in the database.
	 *
	 * @param data The data received from a client.
	 * @param dataService An instance of DataService to get and manipulate
	 *                       data from the database.
	 *
	 * @return A JSONObject with information about the success of this
	 * operation.
	 */
	public static JSONObject editProject(JSONObject data,
										 DataService dataService) {
		JSONObject result = new JSONObject();
		String token;
		String username;
		String projectName;
		String teamName;
		String description;
		String deadline;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
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
						result = ErrorCreator.returnNoRightsError();
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

	/**
	 * Gets data of a specific project out of the database.
	 *
	 * @param data The data received from a client.
	 * @param dataService An instance of DataService to get and edit the data
	 *                      from the database.
	 *
	 * @return A JSONObject with the project data saved in it if the
	 * operation was a success. If it was not, there will be an error message.
	 */
	public static JSONObject getProject(JSONObject data,
								 DataService dataService) {
		JSONObject result;
		String token;
		String teamName;
		String projectName;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
				teamName = data.getString("teamName");
				projectName = data.getString("projectName");
				ProjectEntity project = dataService.getProject(projectName,
						teamName);
				if (project != null) {
					result = new JSONObject();
					result.put("success", "true");
					result.put("project", project.toString());
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
	 * Gets all project entries belonging to a specific team out of the
	 * database.
	 *
	 * @param data The data received from a client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return A JSONObject with all projects of a specific team saved in it
	 * or an error message if there was a failure.
	 */
	public static JSONObject getAllProjects(JSONObject data,
											DataService dataService) {
		JSONObject result;
		String token;
		String teamName;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
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
	 * Creates a new project data set and savees it in the database.
	 *
	 * @param data The data received from a client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return A JSONObject with information about the result of this operation.
	 */
	public static JSONObject createProject(JSONObject data,
										   DataService dataService) {
		JSONObject result;
		String token;
		String teamName;
		String projectName;
		String projectDescription;
		String projectManager;
		String deadline;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
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
							token = JWTHelper.createUserToken("" + manager
											.getId(),
									manager.getUsername(), ""
											+ manager.getRole(),
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
					result = ErrorCreator.returnExistingError();
				}
			} else {
				result = ErrorCreator.returnNoRightsError();
			}
		} catch (JSONException e) {
			result = ErrorCreator.returnClientError();
		}
		return result;
	}

}
