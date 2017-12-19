package service.helper;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import entity.*;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import service.DataService;
import service.RESTService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raphael on 19.12.2017.
 */
public class UserHelper {

	private static final String MAYBE = "MAYBE";
	private static final String YES = "YES";
	private static final String NO = "NO";

	public static JSONObject loginUser(JSONObject data,
									   DataService dataService) {
		JSONObject result;
		String username;
		String password;
		String jsonInfo;
		String token;
		try {
			username = data.getString("username");
			password = data.getString("password");
			if (dataService.login(username, password)) {
				UserEntity user = dataService.getUser(username);
				if (user.getTeam() != null) {
					token = JWTHelper.createUserToken("" + user.getId(),
							username,
							"" + user.getRole(),
							user.getTeam().getName());
				} else {
					token = JWTHelper.createUserToken("" + user.getId(),
							username,
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
			result = ErrorCreator.returnClientError();
		}
		return result;
	}

	public static JSONObject getUsersChats(JSONObject data,
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
				TeamEntity team = dataService.getTeam(teamName);
				UserEntity user = dataService.getUser(username);
				if (team != null && user != null) {
					List<ChatEntity> chats = new ArrayList<>();
					chats = user.getChats();
					JSONArray usersChats = new JSONArray();
					for (int i = 0; i < chats.size(); i++) {
						JSONObject chat
								= new JSONObject(chats.get(i).toString());
						List<UserEntity> users = chats.get(i).getUsers();
						ArrayList<String> usernames = getUsernames(users);
						chat.put("users", usernames);
						usersChats.put(chat);
					}
					result = new JSONObject();
					result.put("success", "true");
					result.put("chats", usersChats);
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

	private static ArrayList<String> getUsernames(List<UserEntity> users) {
		ArrayList<String> usernames = new ArrayList<>();
		for (UserEntity user : users) {
			String name = user.getUsername();
			usernames.add(name);
		}
		return usernames;
	}

	public static JSONObject getUsersProjects(JSONObject data,
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

	public static JSONObject getUsersTasks(JSONObject data,
										   DataService dataService) {
		JSONObject result;
		String token;
		String username;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
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

	public static  JSONObject inviteUserToTeam(JSONObject data,
											   DataService dataService) {
		JSONObject result;
		String token;
		String usernameToInvite;
		String teamName;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
				usernameToInvite = data.getString("usernameToInvite");
				teamName = data.getString("teamName");
				UserEntity toInvite = dataService.getUser(usernameToInvite);
				TeamEntity team = dataService.getTeam(teamName);
				if (toInvite.getTeam() == null) {
					if (toInvite != null && team != null) {
						if (isAdminOfTeam(token, teamName, dataService)) {
							if (dataService.addInvitationToUser(toInvite, team)) {
								result = new JSONObject("{\"success\": " +
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
					result = ErrorCreator.returnNoRightsError();
				}
			} else {
				result = ErrorCreator.returnUserAlreadyInTeam();
			}
		} catch (JSONException e) {
			result = ErrorCreator.returnClientError();
		}
		return result;
	}

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

	public static JSONObject getInvitationsOfUser(JSONObject data,
												  DataService dataService) {
		JSONObject result;
		String token;
		String username;
		try {
			token = data.getString("token");
			username = data.getString("username");
			UserEntity user = dataService.getUser(username);
			if (JWTHelper.validateToken(token)) {
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

	public static JSONObject getUsersTribute(JSONObject data,
											 DataService dataService) {
		JSONObject result;
		String token;
		String username;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
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

	public static JSONObject saveUsersParticipation(JSONObject data,
													DataService dataService) {
		JSONObject result;
		String token;
		String username;
		String appointmentId;
		String answer;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
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
							result = ErrorCreator.returnInternalError();
						}
					} else {
						result = new JSONObject();
						result.put("success", "false");
						result.put("reason", "Der User ist nicht Teil des " +
								"entsprechenden Projekts und kann deshalb " +
								"nicht an dem Meeting teilnehmen!");
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

	private static StatisticParticipationAnswer getUsersAnswer(String answer) {
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

	public static JSONObject leaveTeam(JSONObject data,
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
				if (team != null && user != null) {
					if (dataService.leaveTeam(user, team)) {
						UserEntity updatedUser = dataService.getUser(username);
						token = JWTHelper.createUserToken(""
										+ updatedUser.getId(), username,
								"" + updatedUser.getRole(),
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

	public static JSONObject editUser(JSONObject data,
									  DataService dataService) {
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
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
				username = data.getString("username");
				UserEntity fetchedUser = dataService.getUser(username);
				if (fetchedUser != null) {
					firstName = data.getString("firstName");
					surname = data.getString("surname");
					address = data.getString("address");
					phoneNr = data.getString("phoneNr");
					email = data.getString("email");
					birthday = data.getString("birthday");
					if (dataService.editUser(fetchedUser, firstName, surname,
							address, phoneNr, email, birthday)) {
						result
								= new JSONObject("{\"success\": \"true\"}");
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

	public static JSONObject getAllUsers(JSONObject data,
										 DataService dataService) {
		JSONObject result;
		String token;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
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

	public static JSONObject getUser(JSONObject data,
									 DataService dataService) {
		JSONObject result;
		String token;
		String username;
		try {
			token = data.getString("token");
			username = data.getString("username");
			if (token != null && !(token.equals("")) && username != null
					&& !(username.equals(""))) {
				if (JWTHelper.validateToken(token)) {
					UserEntity fetchedUser = dataService.getUser(username);
					if (fetchedUser != null) {
						result = new JSONObject("{\"success\": \"true\", "
								+ " \"user\": " + fetchedUser.toSring() + "}");
					} else {
						result = ErrorCreator.returnEmptyResult();
					}
				} else {
					result = ErrorCreator.returnNoRightsError();
				}
			} else {
				result = ErrorCreator.returnClientError();
			}
		} catch (JSONException e) {
			result = ErrorCreator.returnClientError();
		}
		return result;
	}

	public static JSONObject registerUser(JSONObject data,
										  DataService dataService) {
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
			username = data.getString("username");
			password = data.getString("password");
			firstName = data.getString("firstName");
			surname = data.getString("surname");
			email = data.getString("email");
			phoneNr = data.getString("phoneNr");
			address = data.getString("address");
			birthday = data.getString("birthday");
		} catch (JSONException e) {
			result = ErrorCreator.returnClientError();
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
}
