package service.helper;

import entity.RegisterEntity;
import entity.TeamEntity;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import service.DataService;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to receive data sent by the client.
 * It extracts needed data and calls the specific methods to work with
 * RegisterEntity data in the database, manipulates it and returns it to the
 * RESTService object.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since version 1.0
 */
public class RegisterHelper {

	/**
	 * Deletes a register in the database.
	 *
	 * @param data The data received from a client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return A JSONObject with information about success  or failure of the
	 * action.
	 */
	public static JSONObject deleteRegister(JSONObject data,
											DataService dataService) {
		JSONObject result;
		String token;
		String username;
		String registerName;
		String teamName;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
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
	 * Gets the data of a specific register out of the database.
	 *
	 * @param data The data sent by a client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return A JSONObject with the register data saved in it due to the
	 * operation was a success or a error message.
	 */
	public static JSONObject getRegister(JSONObject data,
										 DataService dataService) {
		JSONObject result;
		String token;
		String registerName;
		String teamName;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
				registerName = data.getString("registerName");
				teamName = data.getString("teamName");
				RegisterEntity register = dataService.getRegister(registerName,
						teamName);
				if (register != null) {
					result = new JSONObject();
					result.put("success", "true");
					result.put("register",
							new JSONObject(register.toString()));
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
	 * Gets the data of a specific register out of the database and updates
	 * it before saving it again.
	 *
	 * @param data The data received by a client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 * @return
	 */
	public static JSONObject editRegister(JSONObject data,
										  DataService dataService) {
		JSONObject result;
		String token;
		String registerName;
		String color;
		String teamName;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
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
	 * Creates a new register entry in the database.
	 *
	 * @param data The data received from a client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return A JSONObject with information about success or failure of this
	 * operation.
	 */
	public static JSONObject createRegister(JSONObject data,
											DataService dataService) {
		JSONObject result;
		String token;
		String teamName;
		String registerName;
		String username;
		String color;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
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
							result = ErrorCreator.returnServerError();
						}
					} else {
						result = ErrorCreator.returnNoRightsError();
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
	 * Gets all register data of a specific team out from the database.
	 *
	 * @param data The data received from a client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return A JSONObject with all registers of a specific team saved in it
	 * or information about the failure an it's reason saved in it.
	 */
	public static JSONObject getRegisters(JSONObject data,
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
