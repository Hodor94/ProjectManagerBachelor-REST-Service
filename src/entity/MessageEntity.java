package entity;

/**
 * Created by Raphael on 14.06.2017.
 */

import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.json.JSONObject;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity
@Table(name = "message")
public class MessageEntity extends GenericEntity {

	// Attributes without relation to other entities.
	@ColumnTransformer(read = "AES_DECRYPT(message, 'DataService.secretKey')",
					   write = "AES_ENCRYPT(?, 'DataService.secretKey')")
	@Column(name = "message")
	private String message;

	@ColumnTransformer(read = "AES_DECRYPT(date, 'DataService.secretKey')",
					   write = "AES_ENCRYPT(?, 'DataService.secretKey')")
	@Column(name = "date")
	private String date;

	// Attributes related to other entities
	@OneToOne(fetch = FetchType.LAZY)
	private UserEntity author;

	@ManyToOne(fetch = FetchType.LAZY)
	private ChatEntity chat;

	public MessageEntity() {
		super();
	}

	public MessageEntity(String message, String date, UserEntity author,
						 ChatEntity chat) {
		super();
		this.message = message;
		this.date = date;
		this.author = author;
		this.chat = chat;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public UserEntity getAuthor() {
		return author;
	}

	public void setAuthor(UserEntity author) {
		this.author = author;
	}

	public ChatEntity getChat() {
		return chat;
	}

	public void setChat(ChatEntity chat) {
		this.chat = chat;
	}

	private String calendarToString(Calendar calendar) {
		String result;
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		result = formatter.format(calendar.getTime());
		return result;
	}

	public  String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		String result;
		stringBuilder.append("{");
		stringBuilder.append("\"id\": " + "\"" + this.getId() + "\",");
		stringBuilder.append(appendJSONAuthor(author));
		stringBuilder.append(appendJSONDate(date));
		stringBuilder.append(appendJSONChat(chat));
		stringBuilder.append("}");
		result = stringBuilder.toString();
		return result;
	}

	private String appendJSONAuthor(UserEntity author) {
		if (author != null) {
			return "\"author\": " + author.toSring() + ", ";
		} else {
			return "\"author\": " + null + ", ";
		}
	}

	private String appendJSONDate(String date) {
		if (date != null && !(date.equals(""))) {
			return "\"date\": " + "\"" + encodeToUTF8(date) + "\", ";
		} else {
			return "\"date\": " + null + ", ";
		}
	}
	private String appendJSONChat(ChatEntity chat) {
		if (chat != null) {
			return "\"chat\": " + this.getChat().toString();
		} else {
			return "\"chat\": " + null;
		}
	}

}

