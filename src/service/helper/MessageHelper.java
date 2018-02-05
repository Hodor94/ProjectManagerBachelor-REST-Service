package service.helper;

import entity.ChatEntity;
import entity.MessageEntity;
import entity.UserEntity;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import service.DataService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This class is used to receive data sent by  the client.
 * It extracts needed data and calls the specific methods to work with
 * MessageEntity data in the database, manipulates it and returns it to the
 * RESTService object.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since version 1.0
 */
public class MessageHelper {

	/**
	 * Gets all the new messages of a user out of the database.
	 *
	 * @param data The data the client sent.
	 * @param dataService An instance of DataService for manipulating and
	 *                       getting the data from the database.
	 *
	 * @return A JSONObject with all new messages of all chats of a user
	 * saved in it.
	 */
	public static JSONObject getNewMessagesForUser(JSONObject data,
												   DataService dataService) {
		JSONObject result;
		try {
			String username = data.getString("username");
			String timestamp = data.getString("timestamp");
			UserEntity user = dataService.getUser(username);
			ArrayList<String> chatNames = new ArrayList<>();
			SimpleDateFormat formatter
					= new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
			if (user != null) {
				Calendar currentTime = Calendar.getInstance();
				currentTime.setTime(formatter.parse(timestamp));
				List<ChatEntity> chats = user.getChats();
				for (ChatEntity chat : chats) {
					if (chat.getMessages().size() != 0) {
						MessageEntity latestMessage = chat.getMessages().get(0);
						Calendar timestampLatestMessage
								= Calendar.getInstance();
						timestampLatestMessage
								.setTime(formatter.parse(latestMessage
										.getDate()));
						int comparedTimes = user.getLastCheckedMessages()
								.compareTo(timestampLatestMessage);
						if (comparedTimes < 0) {
							String name = "";
							if (chat.isSoloChat) {
								for (UserEntity userEntity : chat.getUsers()) {
									if (!userEntity.getUsername()
											.equals(username)) {
										name = userEntity.getUsername();
										break;
									}
								}
							} else {
								name = chat.getName();
							}
							if (name.length() != 0) {
								chatNames.add(name);
							}
						} else {
							result = new JSONObject();
							result.put("success", "false");
							result.put("compared", comparedTimes);
						}
					}
				}
				user.setLastCheckedMessages(currentTime);
				dataService.saveUser(user);
				result = new JSONObject();
				result.put("success", "true");
				result.put("chats", chatNames);
			} else {
				result = ErrorCreator.returnEmptyResult();
			}
		} catch (JSONException e) {
			result = ErrorCreator.returnClientError();
		} catch (ParseException e) {
			result = new JSONObject();
			try {
				result.put("success", "false");
				result.put("error", e.getMessage());
			} catch (JSONException e1) {
				// Do nothing cause try block will always work.
			}
		}
		return result;
	}

	/**
	 * Receives a message for a ChatEntity entry in the database and saves
	 * the message with all relations set.
	 *
	 * @param data The data sent by the client.
	 * @param dataService A DataService instance for getting data from the
	 *                       database and manipulate it.
	 *
	 * @return A JSONObject with information about the result of this operation.
	 */
	public static JSONObject receiveMessage(JSONObject data,
											DataService dataService) {
		JSONObject result;
		String token;
		long id;
		String message;
		String authorName;
		String timestamp;
		try {
			token = data.getString("token");
			if (JWTHelper.validateToken(token)) {
				id = Long.parseLong(data.getString("chatId"));
				message = data.getString("message");
				authorName = data.getString("author");
				timestamp = data.getString("timestamp");
				UserEntity author = dataService.getUser(authorName);
				ChatEntity chat = dataService.getChat(id);
				if (chat != null && author != null) {
					dataService.createNewMessage(message, author, chat,
							timestamp);
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
		} catch (ParseException e) {
			result = ErrorCreator.returnInternalError();
		}
		return result;
	}

}
