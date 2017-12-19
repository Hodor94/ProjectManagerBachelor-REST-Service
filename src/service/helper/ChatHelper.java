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
 * Created by Raphael on 19.12.2017.
 */
public class ChatHelper {

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
