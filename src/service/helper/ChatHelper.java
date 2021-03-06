package service.helper;

import entity.ChatEntity;
import entity.MessageEntity;
import entity.TeamEntity;
import entity.UserEntity;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import service.DataService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This class is used to receive data sent by  the client.
 * It extracts needed data and calls the specific methods to work with
 * ChatEntity data in the database, manipulates it and returns it to the
 * RESTService object.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since version 1.0
 */
public class ChatHelper {

	/**
	 * Deletes a chat entry in the database.
	 *
	 * @param data Data needed for the operation sent by a client.
	 * @param dataService An instance of DataService to work with the database.
	 *
	 * @return A JSONObject holding information about failure or success of
	 * this operation.
	 */
	public static JSONObject deleteChat(JSONObject data,
										DataService dataService) {
		JSONObject result;
		String token;
		long id;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
				id = data.getLong("chatId");
				ChatEntity chat = dataService.getChat(id);
				if (chat != null) {
					dataService.deleteChat(chat);
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
	 * Gets all messages of a chat entry in the database.
	 *
	 * @param data Data needed fr the operation and sent by a client.
	 * @param dataService An instance of DataService to work with the database.
	 *
	 * @return A JSONObject holding all message data of the specific chat or
	 * giving information about failure of the operation if there was an error.
	 */
	public static JSONObject getChatsMessages(JSONObject data,
											  DataService dataService) {
		JSONObject result;
		String token;
		String username;
		long chatId;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
				chatId = Long.parseLong(data.getString("chatId"));
				username = data.getString("username");
				UserEntity user = dataService.getUser(username);
				ChatEntity chat = dataService.getChat(chatId);
				if (chat != null && user != null) {
					List<MessageEntity> chatsMessages = chat.getMessages();
					JSONArray messages = new JSONArray();
					for (int i = 0; i < chatsMessages.size(); i++) {
						JSONObject message = new JSONObject(
								chatsMessages.get(i).toString());
						messages.put(message);
					}
					user.setLastCheckedMessages(Calendar.getInstance());
					dataService.saveUser(user);
					result = new JSONObject();
					result.put("success", "true");
					result.put("messages", messages);
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
	 * Creates a new chat entry in the database.
	 *
	 * @param data Data needed for this operation and sent by a client.
	 * @param dataService An instance of DataService to work with the database.
	 *
	 * @return A JSONObject with information about success or failure of this
	 * operation.
	 */
	public static JSONObject createChat(JSONObject data,
										DataService dataService) {
		JSONObject result;
		String token;
		String teamName;
		String chatName;
		String isSoloChat = null;
		JSONArray usersOfChat;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
				teamName = data.getString("teamName");
				chatName = data.getString("name");
				isSoloChat = data.getString("isSoloChat");
				usersOfChat = data.getJSONArray("users");
				TeamEntity team = dataService.getTeam(teamName);
				if (team != null) {
					if (isSoloChat.equals("false")) {
						if (dataService.getChatByName(team, chatName) == null) {
							ArrayList<UserEntity> users = new ArrayList<>();
							for (int i = 0; i < usersOfChat.length(); i++) {
								UserEntity user
										= dataService
										.getUser(usersOfChat.getString(i));
								if (user != null) {
									users.add(user);
								}
							}
							dataService.createNewChat(team, users, chatName);
							result = new JSONObject();
							result.put("success", "true");
						} else {
							result = ErrorCreator.returnExistingError();
						}
					} else if (isSoloChat.equals("true")) {
						ArrayList<UserEntity> users = new ArrayList<>();
						for (int i = 0; i < usersOfChat.length(); i++) {
							UserEntity user = dataService.getUser(usersOfChat
									.getString(i));
							if (user != null) {
								users.add(user);
							}
						}
						dataService.createNewChat(team, users, chatName);
						result = new JSONObject();
						result.put("success", "true");
					} else {
						result = ErrorCreator.returnClientError();
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
