package service.helper;

import entity.AppointmentEntity;
import entity.ProjectEntity;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import service.DataService;

/**
 * Created by Raphael on 19.12.2017.
 */
public class AppointmentHelper {

	public static JSONObject getAppointment(JSONObject data,
											DataService dataService) {
		JSONObject result;
		String token;
		String appointmentId;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
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

	public static JSONObject deleteAppointment(JSONObject data,
											   DataService dataService) {
		JSONObject result;
		String token;
		String projectName;
		String teamName;
		String username;
		String appointmentId;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
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

	public static JSONObject editAppointment(JSONObject data,
											 DataService dataService) {
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
			if (JWTHelper.validateToken(token)) {
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

	public static JSONObject createAppointment(JSONObject data,
											   DataService dataService) {
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
			if (JWTHelper.validateToken(token)) {
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
