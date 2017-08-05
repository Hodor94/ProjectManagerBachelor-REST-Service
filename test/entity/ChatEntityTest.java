package entity;

import dao.ChatDAO;
import dao.TeamDAO;
import dao.UserDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.DataService;

import javax.xml.crypto.Data;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Raphael on 19.06.2017.
 */
class ChatEntityTest {

	private static final String CHAT_NAME = "test-chat";
	private static final String USER_NAME_ONE = "test-user-one";
	private static final String USER_NAME_TWO = "test-user-two";
	private static final String BIRTHDAY = "03.01.1994 00:00:00";
	private static final String TEAM_NAME = "test-team";
	private static final String DESCRIPTION = "Just for testing";

	private static UserEntity userOne;
	private static UserEntity userTwo;
	private static TeamEntity testTeam;
	private static Calendar birthday;
	private static SimpleDateFormat formatter;
	private static ChatEntity testChat;


	@BeforeAll
	private static void setUp() {
		formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		birthday = Calendar.getInstance();
		try {
			birthday.setTime(formatter.parse(BIRTHDAY));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		userOne = new UserEntity(USER_NAME_ONE, "", "",
				"", "", "", "", BIRTHDAY);
		userTwo = new UserEntity(USER_NAME_TWO, "", "",
				"", "", "", "", BIRTHDAY);
		testTeam = new TeamEntity(TEAM_NAME, DESCRIPTION, userOne);
		testChat = new ChatEntity(CHAT_NAME, new ArrayList<UserEntity>(), new
				ArrayList<MessageEntity>(), testTeam, userOne);
		testChat.getUsers().add(userOne);
		testChat.getUsers().add(userTwo);
		testChat.setTeam(testTeam);
	}

	@Test
	public void testCreateNewChat() {
		DataService service = new DataService();
		service.registerUser(USER_NAME_ONE, "", "", "",
				"", "", "", BIRTHDAY);
		service.registerUser(USER_NAME_TWO, "", "", "",
				"", "", "", BIRTHDAY);
		service.createNewTeam(TEAM_NAME, DESCRIPTION, USER_NAME_ONE);
		service.addUserToTeam(TEAM_NAME, USER_NAME_TWO);
		ArrayList<String> users
				= new ArrayList<String>();
		users.add(USER_NAME_ONE);
		users.add(USER_NAME_TWO);
		service.createNewChat(CHAT_NAME, users, TEAM_NAME, USER_NAME_ONE);
		TeamEntity team = service.getTeam(TEAM_NAME);
		ArrayList<ChatEntity> chats
				= new ArrayList<ChatEntity>(team.getChats());
		assertEquals(1, team.getChats().size());
		assertEquals(CHAT_NAME, chats.get(0).getName());
		UserEntity userOne = service.getUser(USER_NAME_ONE);
		UserEntity userTwo = service.getUser(USER_NAME_TWO);
		assertEquals(1, userOne.getChats().size());
		assertEquals(1, userTwo.getChats().size());
		assertEquals(CHAT_NAME, userOne.getChats().get(0).getName());
		assertEquals(CHAT_NAME, userTwo.getChats().get(0).getName());
		assertEquals(USER_NAME_ONE, chats.get(0).getCreator().getUsername());
	}

	@Test
	public void testGetChat() {
		DataService service = new DataService();
		service.registerUser(USER_NAME_ONE, "", "", "",
				"", "", "", BIRTHDAY);
		service.registerUser(USER_NAME_TWO, "", "", "",
				"", "", "", BIRTHDAY);
		service.createNewTeam(TEAM_NAME, DESCRIPTION, USER_NAME_ONE);
		service.addUserToTeam(TEAM_NAME, USER_NAME_TWO);
		ArrayList<String> users = new ArrayList<String>();
		users.add(USER_NAME_ONE);
		users.add(USER_NAME_TWO);
		// Create two chats with similar attributes except the creator
		service.createNewChat(CHAT_NAME, users, TEAM_NAME, USER_NAME_ONE);
		service.createNewChat(CHAT_NAME, users, TEAM_NAME, USER_NAME_TWO);
		ChatEntity chatOne
				= service.getChat(CHAT_NAME, TEAM_NAME, USER_NAME_ONE);
		ChatEntity chatTwo
				= service.getChat(CHAT_NAME, TEAM_NAME, USER_NAME_TWO);
		assertNotEquals(null, chatOne);
		assertNotEquals(null, chatTwo);
		assertEquals(USER_NAME_ONE, chatOne.getCreator().getUsername());
		assertEquals(USER_NAME_TWO, chatTwo.getCreator().getUsername());
	}

	@Test
	public void testRemoveChat() {
		DataService service = new DataService();
		service.registerUser(USER_NAME_ONE, "", "", "",
				"", "", "", BIRTHDAY);
		service.registerUser(USER_NAME_TWO, "", "", "",
				"", "", "", BIRTHDAY);
		service.createNewTeam(TEAM_NAME, DESCRIPTION, USER_NAME_ONE);
		service.addUserToTeam(TEAM_NAME, USER_NAME_TWO);
		ArrayList<String> users = new ArrayList<String>();
		users.add(USER_NAME_ONE);
		users.add(USER_NAME_TWO);
		service.createNewChat(CHAT_NAME, users, TEAM_NAME, USER_NAME_ONE);
		service.createNewChat(CHAT_NAME, users, TEAM_NAME, USER_NAME_TWO);
		service.removeChat(CHAT_NAME, TEAM_NAME, USER_NAME_ONE);
		UserEntity userOne = service.getUser(USER_NAME_ONE);
		UserEntity userTwo = service.getUser(USER_NAME_TWO);
		ChatEntity chatOne = service.getChat(CHAT_NAME, TEAM_NAME,
				USER_NAME_ONE);
		ChatEntity chatTwo
				= service.getChat(CHAT_NAME, TEAM_NAME, USER_NAME_ONE);
		assertEquals(1, userOne.getChats().size());
		assertEquals(1, userTwo.getChats().size());
		assertEquals(null, chatOne);
		assertNotEquals(null, chatTwo);
	}

}