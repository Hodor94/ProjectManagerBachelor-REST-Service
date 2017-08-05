package entity;

import dao.ChatDAO;
import dao.TeamDAO;
import dao.UserDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.DataService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Raphael on 19.06.2017.
 */
class MessageEntityTest {

	private static final String TEAM_NAME = "test-team";
	private static final String USER_NAME = "test-user";
	private static final String USER_NAME_TWO = "test-user-two";
	private static final String DESCRIPTION = "Just for testing";
	private static final String CHAT_NAME = "test-chat";
	private static final String MESSAGE_ONE = "I am a test message!";
	private static final String MESSAGE_TWO = "I am a test reply!";
	private static final String BIRTHDAY_ONE = "03.01.1994 00:00:01";
	private static final String BIRTHDAY_TWO = "03.01.1994 00:00:02";

	private static TeamEntity testTeam;
	private static UserEntity testUserOne;
	private static UserEntity testUserTwo;
	private static ChatEntity testChat;
	private static MessageEntity testMessageOne;
	private static MessageEntity testMessageTwo;
	private static Calendar birthdayOne;
	private static Calendar birthdayTwo;
	private static SimpleDateFormat formatter;

	@BeforeAll
	private static void setUp() {
		formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		birthdayOne = Calendar.getInstance();
		birthdayTwo = Calendar.getInstance();
		try {
			birthdayOne.setTime(formatter.parse(BIRTHDAY_ONE));
			birthdayTwo.setTime(formatter.parse(BIRTHDAY_TWO));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		testUserOne = new UserEntity(USER_NAME, "", "",
				"", "", "", "", BIRTHDAY_ONE);
		testUserTwo = new UserEntity(USER_NAME_TWO, "", "",
				"", "", "", "", BIRTHDAY_TWO);
		testTeam = new TeamEntity(TEAM_NAME, DESCRIPTION, testUserOne);
		testChat = new ChatEntity(CHAT_NAME, new ArrayList<UserEntity>(), new
				ArrayList<MessageEntity>(), testTeam, testUserOne);
		testMessageOne = new MessageEntity(MESSAGE_ONE, BIRTHDAY_ONE, testUserOne,
				testChat);
		testMessageTwo = new MessageEntity(MESSAGE_TWO, BIRTHDAY_TWO, testUserTwo,
				testChat);
	}

	@Test
	public void testCreateNewMessage() {
		DataService service = new DataService();
		service.registerUser(USER_NAME, "", "", "",
				"", "", "", BIRTHDAY_ONE);
		service.registerUser(USER_NAME_TWO, "", "", "",
				"", "", "", BIRTHDAY_TWO);
		service.createNewTeam(TEAM_NAME, DESCRIPTION, USER_NAME);
		ArrayList<String> userNames = new ArrayList<String>();
		userNames.add(USER_NAME);
		userNames.add(USER_NAME_TWO);
		service.createNewChat(CHAT_NAME, userNames, TEAM_NAME, USER_NAME);
		service.createNewMessage(MESSAGE_ONE, BIRTHDAY_ONE, USER_NAME, CHAT_NAME,
				TEAM_NAME, USER_NAME);
		service.createNewMessage(MESSAGE_TWO, BIRTHDAY_TWO, USER_NAME_TWO,
				CHAT_NAME, TEAM_NAME, USER_NAME);
		List<MessageEntity> fetchedMessages
				= service.getMessagesOfChat(1);
		assertEquals(2, fetchedMessages.size());
		assertEquals(testMessageOne.getMessage(), fetchedMessages.get(0).getMessage());
		assertEquals(testMessageOne.getAuthor().getUsername(),
				fetchedMessages.get(0).getAuthor().getUsername());
		assertEquals(testMessageOne.getDate(),
				fetchedMessages.get(0).getDate());
		assertEquals(testMessageTwo.getMessage(),
				fetchedMessages.get(1).getMessage());
		assertEquals(testMessageTwo.getAuthor().getUsername(),
				fetchedMessages.get(1).getAuthor().getUsername());
		assertEquals(testMessageTwo.getDate(),
				fetchedMessages.get(1).getDate());
	}
}