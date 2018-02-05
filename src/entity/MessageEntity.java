package entity;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This class is used by the framework Hibernate to work with a database and
 * represents a message of a chat.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "message")
public class MessageEntity extends GenericEntity {

	// Attributes without relation to other entities.
	@Column(name = "message")
	private String message;

	@Column(name = "date")
	private String date;

	// Attributes related to other entities
	@OneToOne(fetch = FetchType.LAZY)
	private UserEntity author;

	@ManyToOne(fetch = FetchType.LAZY)
	private ChatEntity chat;

	/**
	 * Creates a MessageEntity object with the attributes set by default values.
	 */
	public MessageEntity() {
		super();
	}

	/**
	 * Creates a MessageEntity object with the attributes set by the values
	 * given by the parameters.
	 *
	 * @param message The text of the message.
	 * @param date The point of time the message was written.
	 * @param author The author of the text.
	 * @param chat The chat the message belongs to.
	 */
	public MessageEntity(String message, String date, UserEntity author,
						 ChatEntity chat) {
		super();
		this.message = message;
		this.date = date;
		this.author = author;
		this.chat = chat;
	}

	/**
	 * Returns the text of the message.
	 *
	 * @return The text of the message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the text of the message.
	 *
	 * @param message The new text of the message.
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Returns the timestamp of the message.
	 *
	 * @return The timestamp of the message.
	 */
	public String getDate() {
		return date;
	}

	/**
	 * Sets the timestamp of the message.
	 *
	 * @param date The new timestamp of the message.
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * Returns the author of the message.
	 *
	 * @return The author of the message.
	 */
	public UserEntity getAuthor() {
		return author;
	}

	/**
	 * Sets the author of this message.
	 *
	 * @param author The new author of the message.
	 */
	public void setAuthor(UserEntity author) {
		this.author = author;
	}

	/**
	 * Returns the chat this register belongs to.
	 *
	 * @return The chat this message belongs to.
	 */
	public ChatEntity getChat() {
		return chat;
	}

	/**
	 * Sets the chat this message belongs to.
	 *
	 * @param chat The new chat this message belongs to.
	 */
	public void setChat(ChatEntity chat) {
		this.chat = chat;
	}

	/*
	Transforms a Calendar object into a String attribute.
	 */
	private String calendarToString(Calendar calendar) {
		String result;
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		result = formatter.format(calendar.getTime());
		return result;
	}

	/**
	 * Transforms this RegisterEntity object into a JSON format String.
	 *
	 * @return This object as a JSON format String
	 */
	public  String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		String result;
		stringBuilder.append("{");
		stringBuilder.append("\"id\": " + "\"" + this.getId() + "\",");
		stringBuilder.append(appendJSONAuthor(author));
		stringBuilder.append(appendJSONDate(date));
		stringBuilder.append(appendJSONMessage(message));
		stringBuilder.append(appendJSONChat(chat));
		stringBuilder.append("}");
		result = stringBuilder.toString();
		return result;
	}

	/*
	Returns the text of the message as a JSON attribute.
	 */
	private String appendJSONMessage(String message) {
		if (message != null) {
			return "\"message\": \"" + message + "\", ";
		} else {
			return "\"message\": " + null + ", ";
		}
	}

	/*
	Returns the username of the author of this message as a JSON attribute.
	 */
	private String appendJSONAuthor(UserEntity author) {
		if (author != null) {
			return "\"author\": " + author.getUsername() + ", ";
		} else {
			return "\"author\": " + null + ", ";
		}
	}

	/*
	Returns the timestamp of this message as a JSON attribute.
	 */
	private String appendJSONDate(String date) {
		if (date != null && !(date.equals(""))) {
			return "\"date\": " + "\"" + date + "\", ";
		} else {
			return "\"date\": " + null + ", ";
		}
	}

	/*
	Returns the chat of this message as a JSON attribute.
	 */
	private String appendJSONChat(ChatEntity chat) {
		if (chat != null) {
			return "\"chat\": " + this.getChat().toString();
		} else {
			return "\"chat\": " + null;
		}
	}

}

