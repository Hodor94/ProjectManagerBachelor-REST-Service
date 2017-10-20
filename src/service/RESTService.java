package service;

/**
 * Created by Raphael on 15.06.2017.
 */

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import entity.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.*;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.*;

@JsonSerialize
@Path("/pmservice")
public class RESTService {

	private final String REQUEST = "request";
	private final String INVITATION = "invitation";
	private DataService dataService = new DataService();
	private final byte[] SHARED_SECRET = generateSharedSecret();
	private final long EXPIRE_TIME = 900000; // Within a 15 minutes period a
	// token is valid

	//--------------------------------------------------------------------------

	// Todo: Ask for updates -> InitialService at client side
	// TODO: test with ping

	@GET
	@Path("/ping")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject ping() {
		try {
			return new JSONObject("{\"success\": \"true\"}");
		} catch (JSONException e) {
			return null;
		}
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
		try {
			token = data.getString("token");
			if (validateToken(token)) {
				toEdit = data.getString("username");
				registerName = data.getString("registerName");
				admin = data.getString("admin");
				teamName = data.getString("teamName");
				UserEntity userToEdit = dataService.getUser(toEdit);
				RegisterEntity register;
				if (registerName.equals("-----")) {
					register = null;
				} else {
					register = dataService.getRegister
							(registerName, teamName);
				}
					UserEntity userAdmin = dataService.getUser(admin);
					if (userToEdit != null && register != null
							&& userAdmin != null) {
						if (userAdmin.getRole().equals(UserRole.ADMINISTRATOR)
								&& userAdmin.getTeam().getName().equals(teamName)) {
							userToEdit.setRegister(register);
							dataService.saveUser(userToEdit);
							result = new JSONObject();
							result.put("success", "true");
						} else {
							result = returnTokenError();
						}
					} else {
						result = new JSONObject();
						result.put("success", "false");
						result.put("reason", "USER: " + userToEdit +
								" REGISTER: " + register +
								" ADMIN: " + userAdmin);
					}
				} else{
					result = returnTokenError();
				}
			} catch(JSONException e){
				result = returnClientError();
			}
			return result;
		}

		@POST
		@Path("/team/members")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject getTeamMembers (JSONObject data){
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
					result = returnTokenError();
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
		public JSONObject createRegister (JSONObject data){
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
							result = returnTokenError();
						}
					} else {
						result = returnExistingError();
					}
				} else {
					result = returnTokenError();
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
		public JSONObject getRegisters (JSONObject data){
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
					result = returnTokenError();
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
				result = returnTokenError();
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
				result = returnTokenError();
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
								"\"true\", \"type\": \"" + REQUEST + "\"}");
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
			user.setTeam(team);
			team.setRequestsOfUsers(new ArrayList<>());
			dataService.saveUser(user);
			dataService.saveTeam(team);
			result = true;
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
			user.setTeam(team);
			user.setInvitationsOfTeams(new ArrayList<String>());
			dataService.saveUser(user);
			result = true;
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
				result = returnTokenError();
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
						result = returnTokenError();
					}
				} else {
					result = returnEmptyResult();
				}
			} else {
				result = returnTokenError();
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
							result = returnTokenError();
						}
					} else {
						result = returnEmptyResult();
					}
				} else {
					result = returnTokenError();
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
				result = returnTokenError();
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
				result = returnTokenError();
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
				result = returnTokenError();
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
								result = returnTokenError();
							}
						} else {
							result = returnEmptyResult();
						}

					} else {
						result = returnClientError();
					}
				} else {
					result = returnTokenError();
				}
			} else {
				result = returnTokenError();
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
					result = returnTokenError();
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
				result = returnTokenError();
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
				result = returnTokenError();
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
					result = returnTokenError();
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

	private JSONObject returnTokenError() {
		try {
			JSONObject result = new JSONObject("{\"success\": \"false\", " +
					"\"reason\": \"Die Berechtigung für diese Aktion ist " +
					"nicht gewährleistet!\"}");
			return result;
		} catch (JSONException e) {
			return null;
		}
	}

	private JSONObject returnClientError() {
		try {
			return new JSONObject("{\"success\": \"false\", " +
					"\"error\": \"Falsche Angaben im Request! Client " +
					"zeigt falsches Verhalten!\"}");
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

