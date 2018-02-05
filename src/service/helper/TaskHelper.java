package service.helper;

import entity.TaskEntity;
import entity.TeamEntity;
import entity.UserEntity;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import service.DataService;

/**
 * This class is used to receive data sent by  the client.
 * It extracts needed data and calls the specific methods to work with
 * TaskEntity data in the database, manipulates it and returns it to the
 * RESTService object.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since version 1.0
 */
public class TaskHelper {

	/**
	 * Deletes a task from the database.
	 *
	 * @param data The data received from a client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return A JSONObject with information about success or failure of this
	 * operation saved in it.
	 */
	public static JSONObject deleteTask(JSONObject data,
										DataService dataService) {
		JSONObject result;
		String token;
		String taskName;
		String teamName;
		String username;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
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
	 * Edits a task data entry in the database.
	 *
	 * @param data The data sent by the client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return A JSONObject with information about success or failure of this
	 * operation saved in it.
	 */
	public static JSONObject editTask(JSONObject data,
									  DataService dataService) {
		JSONObject result;
		String token;
		String taskName;
		String description;
		String deadline;
		String teamName;
		String username;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
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
	 * Gets a task out of the database.
	 *
	 * @param data The data sent by a client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return A JSONObject with the task data or an error message due to a
	 * failure saved in it.
	 */
	public static JSONObject getTask(JSONObject data, DataService dataService) {
		JSONObject result;
		String token;
		String taskName;
		String teamName;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
				taskName = data.getString("taskName");
				teamName = data.getString("teamName");
				TaskEntity task = dataService.getTask(taskName, teamName);
				if (task != null) {
					result = new JSONObject();
					result.put("success", "true");
					result.put("task", new JSONObject(task.toString()));
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
	 * Creates a new task data entry in the database.
	 *
	 * @param data The data sent by a client.
	 * @param dataService An instance of DataService to get and edit data
	 *                       from the database.
	 *
	 * @return A JSONObject with information about success or failure of this
	 * operation saved in it.
	 */
	public static JSONObject createTask(JSONObject data,
										DataService dataService) {
		JSONObject result;
		String token;
		String taskName;
		String taskDescription;
		String worker;
		String deadline;
		String teamName;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
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
						result = ErrorCreator.returnExistingError();
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

}
