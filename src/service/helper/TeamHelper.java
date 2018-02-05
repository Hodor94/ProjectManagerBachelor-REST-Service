package service.helper;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import entity.*;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import service.DataService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to receive data sent by  the client.
 * It extracts needed data and calls the specific methods to work with
 * TeamEntity data in the database, manipulates it and returns it to the
 * RESTService object.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since version 1.0
 */
public class TeamHelper {

	/**
	 * Creates a new team data entry in the database.
	 *
	 * @param data The data sent by a client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return A JSONObject with information about success or failure of this
	 * operation saved in it.
	 */
	public static JSONObject createTeam(JSONObject data,
										DataService dataService) {
		JSONObject result;
		try {
			String token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
				String teamName = data.getString("teamName");
				TeamEntity fetchedTeam = dataService.getTeam(teamName);
				if (fetchedTeam == null) {
					String description = data.getString("teamDescription");
					String admin = data.getString("admin");
					if (dataService.createNewTeam(teamName, description,
							admin)) {
						UserEntity user = dataService.getUser(admin);
						String newToken = JWTHelper
								.createUserToken("" + user.getId(),
								user.getUsername(), "" + user.getRole(),
								user.getTeam().getName());
						result = new JSONObject("{\"success\": \"true\", "
								+ "\"teamName\": \"" + teamName + "\", " +
								"\"token\": \"" + newToken + "\"}");
					} else {
						result = ErrorCreator.returnServerError();
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

	/**
	 * Gets the news of a team entry out of the database.
	 *
	 * @param data The data sent by a client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return The news of a team or in case of a failure the error and it's
	 * reason saved in a JSONObject.
	 */
	public static JSONObject getTeamsNews(JSONObject data,
										  DataService dataService) {
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
				result = ErrorCreator.returnEmptyResult();
			}
		} catch (JSONException e) {
			result = ErrorCreator.returnClientError();
		}
		return result;
	}

	/**
	 * Gets all tasks of a specific team out of the database.
	 *
	 * @param data The data sent by a client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return A JSONObject with all tasks of a specific team or in case of a
	 * failure the error and it'S reason saved in it.
	 */
	public static JSONObject getTeamsTasks(JSONObject data,
										   DataService dataService) {
		JSONObject result;
		String token;
		String teamName;
		String username;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
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
	 * Deletes a team from the database.
	 *
	 * @param data The data sent by a client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return A JSONOnject with information about success or failure of this
	 * operation.
	 */
	public static JSONObject deleteTeam(JSONObject data,
										DataService dataService) {
		JSONObject result;
		String token;
		String username;
		String teamName;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
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
	 * Adds or removes team members from a team.
	 *
	 * @param data The data sent by a client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return A JSONObject with the information about success or failure of
	 * this operation.
	 */
	public static JSONObject editTeamMember(JSONObject data,
											DataService dataService) {
		JSONObject result;
		String token;
		String toEdit;
		String registerName;
		String admin;
		String teamName;
		String tributes;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
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
	 * Gets all users who are members of a specific team.
	 *
	 * @param data The data sent by a client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return A JSONObject with all team members or an error with it's
	 * reason saved in it.
	 */
	public static JSONObject getTeamMembers(JSONObject data,
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
	 * Edits a specific data entry of a team.
	 *
	 * @param data The data sent by a client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return A JSONObject with information about success or failure of this
	 * operation saved in it.
	 */
	public static JSONObject editTeam(JSONObject data,
									  DataService dataService) {
		JSONObject result;
		String token;
		String admin;
		String teamName;
		String teamDescription;
		try {
			token = data.getString("token");
			teamName = data.getString("teamName");
			if (JWTHelper.validateToken(token)) {
				if (isAdminOfTeam(token, teamName, dataService)) {
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
									result = ErrorCreator.returnServerError();
								}
							} else {
								result = ErrorCreator.returnNoRightsError();
							}
						} else {
							result = ErrorCreator.returnEmptyResult();
						}

					} else {
						result = ErrorCreator.returnClientError();
					}
				} else {
					result = ErrorCreator.returnNoRightsError();
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
	 * Checks if a user is the admin of a team.
	 *
	 * @param token The token of the user to be checked.
	 * @param teamNameSent The name of the team loaded.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return A JSONObject with information whether the user is admin of the
	 * team or not.
	 *
	 */
	private static boolean isAdminOfTeam(String token, String teamNameSent,
										 DataService dataService) {
		boolean result = false;
		if (token != null) {
			try {
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
			} catch (ParseException e) {
				// Do nothing
			}
		}
		return result;
	}

	/**
	 * Gets the data of a specific team.
	 *
	 * @param data The data sent by a client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return A JSONObject with the team data or an error message saved in it.
	 */
	public static JSONObject getTeam(JSONObject data,
									 DataService dataService) {
		JSONObject result;
		String token;
		String teamName;
		try {
			token = data.getString("token");
			teamName = data.getString("team");
			if (token != null && teamName != null && !(token.equals(""))
					&& !(teamName.equals(""))) {
				if (JWTHelper.validateToken(token)) {
					TeamEntity fetchedTeam = dataService.getTeam(teamName);
					if (fetchedTeam != null) {
						try {
							result = new JSONObject("{\"success\": " +
									"\"true\", " + "\"team\":"
									+ fetchedTeam.toString() + "}");
						} catch (JSONException e) {
							result = ErrorCreator.returnServerError();
						}
					} else {
						result = ErrorCreator.returnEmptyResult();
					}
				} else {
					result = ErrorCreator.returnNoRightsError();
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
	 * Gets all team data of the system.
	 *
	 * @param data The data sent by a client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return A JSONObject with the teams in it or an error message due to a
	 * failure.
	 */
	public static JSONObject getTeams(JSONObject data,
										 DataService dataService) {
		JSONObject result;
		try {
			String token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
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
						result = ErrorCreator.returnServerError();
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
	 * Adds a user's request for joining a team to the team's requests.
	 *
	 * @param data The data sent by a client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return A JSONObject with information about success or failure of this
	 * operation.
	 */
	public static JSONObject addRequestToTeam(JSONObject data,
											  DataService dataService) {
		JSONObject result;
		String token;
		String username;
		String teamName;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
				username = data.getString("username");
				teamName = data.getString("teamName");
				UserEntity user = dataService.getUser(username);
				TeamEntity team = dataService.getTeam(teamName);
				if (user != null && team != null) {
					if (dataService.addRequestToTeam(team, user)) {
						result = new JSONObject("{\"success\": \"true\"}");
					} else {
						result = ErrorCreator.returnServerError();
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
	 * Gets the requests to join of a specific team.
	 *
	 * @param data The data sent by a client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return A JSONObject with all teh requests of a team or due to a
	 * failure during this operation an error message.
	 */
	public static JSONObject getRequestsOfTeam(JSONObject data,
											   DataService dataService) {
		JSONObject result;
		String token;
		String teamName;
		String username;
		try {
			token = data.getString("token");
			teamName = data.getString("teamName");
			username = data.getString("username");
			if (JWTHelper.validateToken(token)) {
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
}
