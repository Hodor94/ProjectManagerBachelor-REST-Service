package service.helper;

import entity.RegisterEntity;
import entity.TeamEntity;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import service.DataService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raphael on 19.12.2017.
 */
public class RegisterHelper {

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
