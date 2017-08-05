package entity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.DataService;

import javax.xml.crypto.Data;
import java.lang.ref.PhantomReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Raphael on 15.06.2017.
 */
class UserEntityTest {
	private static UserEntity testUserOne;
	private static TeamEntity testTeam;
	private static final String DESCRIPTION = "testDescription";
	private static final String USER_NAME = "testuser";
	private static final String PASSWORD = "testpassword";
	private static final String FIRST_NAME = "firstName";
	private static final String SURNAME = "surname";
	private static final String EMAIL = "test@testmail.com";
	private static final String PHONE_NR = "1234/56789";
	private static final String ADDRESS = "testaddress 1";
	private static final String BIRTHDAY = "03.01.1994 00:00:00";
	private static final String NAME_OF_REGISTER = "registerTest";
	private static final String TEAM_NAME = "testTeam";
	private static final String REGISTER_NAME = "test-register";
	private static final String PROJECT_NAME = "test-project";
	private static Calendar birthday;
	private static SimpleDateFormat formatter;


	@BeforeAll
	private static void setUp() {
		formatter = new SimpleDateFormat("dd.MM.yyyy");
		try {
			birthday = Calendar.getInstance();
			birthday.setTime(formatter.parse
					("03.01.1994"));
		} catch (ParseException e) {
			System.out.println("ERROR! COULD NOT PARSE TO DATE!");
			e.printStackTrace();
		}
		testUserOne = new UserEntity(USER_NAME, PASSWORD, FIRST_NAME, SURNAME,
				EMAIL, PHONE_NR, ADDRESS, BIRTHDAY);
		testTeam = new TeamEntity(TEAM_NAME, DESCRIPTION, testUserOne, new
				ArrayList<UserEntity>(), new ArrayList<ProjectEntity>(), new
				ArrayList<TaskEntity>(), new ArrayList<RegisterEntity>(), new
				ArrayList<ChatEntity>());
	}

	@Test
	public void testRegisterUser() {
		DataService dataService = new DataService();
		dataService.registerUser(USER_NAME, PASSWORD, FIRST_NAME, SURNAME,
				EMAIL, PHONE_NR, ADDRESS, BIRTHDAY);
		UserEntity fetchedUser = dataService.getUser(USER_NAME);
		assertEquals(testUserOne.getAddress(), fetchedUser.getAddress());
		assertEquals(testUserOne.getAppointmentsTakingPart(),
					 fetchedUser.getAppointmentsTakingPart());
		assertEquals(testUserOne.getBirthday(),
				fetchedUser.getBirthday());
		assertEquals(testUserOne.getEmail(), fetchedUser.getEmail());
		assertEquals(testUserOne.getFirstName(), fetchedUser.getFirstName());
		assertEquals(testUserOne.getPassword(), fetchedUser.getPassword());
		assertEquals(testUserOne.getPhoneNr(), fetchedUser.getPhoneNr());
		assertEquals(testUserOne.getRole().toString(), fetchedUser.getRole()
				.toString());
		assertEquals(testUserOne.getSurname(), fetchedUser.getSurname());
		assertEquals(testUserOne.getUsername(), fetchedUser.getUsername());
		boolean registerTwice = dataService.registerUser(USER_NAME, PASSWORD,
				FIRST_NAME, SURNAME,
				EMAIL, PHONE_NR, ADDRESS, BIRTHDAY);
		assertFalse(registerTwice);
	}

	@Test
	public void testLogin() {
		DataService dataService = new DataService();
		dataService.registerUser(USER_NAME, PASSWORD, FIRST_NAME, SURNAME,
				EMAIL, PHONE_NR, ADDRESS, BIRTHDAY);
		boolean validLogin = dataService.login(USER_NAME, PASSWORD);
		assertTrue(validLogin);
	}

	@Test
	public void testSetUsersRegister() {
		DataService service = new DataService();
		service.registerUser(USER_NAME, PASSWORD, FIRST_NAME, SURNAME, EMAIL,
				PHONE_NR, ADDRESS, BIRTHDAY);
		service.createNewTeam(TEAM_NAME, DESCRIPTION, USER_NAME);
		UserEntity fetchedUser = service.getUser(USER_NAME);
		assertEquals(null, fetchedUser.getRegister());
		service.createNewRegister(NAME_OF_REGISTER, TEAM_NAME);
		service.setUsersRegister(USER_NAME, NAME_OF_REGISTER, TEAM_NAME);
		fetchedUser = service.getUser(USER_NAME);
		assertEquals(NAME_OF_REGISTER, fetchedUser.getRegister().getName());
	}

	@Test
	public void testRemoveUserFromApp() {
		DataService service = new DataService();
		service.registerUser(USER_NAME, "", "", "",
				"", "", "", BIRTHDAY);
		service.registerUser("test-user-2", "", "",
				"", "", "", "", BIRTHDAY);
		service.createNewTeam(TEAM_NAME, DESCRIPTION, USER_NAME);
		service.addUserToTeam(TEAM_NAME, "test-user-2");
		// test remove an admin
		service.removeUserFromApp(USER_NAME);
		assertEquals(null, service.getTeam(TEAM_NAME));
		assertEquals(null, service.getUser(USER_NAME));
		// create team again and admin user
		service.registerUser(USER_NAME, "", "", "",
				"", "", "", BIRTHDAY);
		service.createNewTeam(TEAM_NAME, DESCRIPTION, USER_NAME);
		service.addUserToTeam(TEAM_NAME, "test-user-2");
		service.createNewRegister(REGISTER_NAME, TEAM_NAME);
		service.setUsersRegister("test-user-2", REGISTER_NAME, TEAM_NAME);
		// test remove a normal user
		service.removeUserFromApp("test-user-2");
		TeamEntity team = service.getTeam(TEAM_NAME);
		assertNotEquals(null, team);
		UserEntity user = service.getUser(USER_NAME);
		assertNotEquals(null, user);
		assertEquals(team.getAdmin().getUsername(), user.getUsername());
		assertEquals(null, service.getUser("test-user-2"));
		RegisterEntity register = service.getRegister(REGISTER_NAME, TEAM_NAME);
		assertNotEquals(null, register);
		assertEquals(0, register.getUsers().size());
	}

	@Test
	public void testRemoveUserFromTeam() {
		DataService service = new DataService();
		service.registerUser(USER_NAME, "", "", "",
				"", "", "", BIRTHDAY);
		service.registerUser("test-user-2", "", "",
				"", "", "", "", BIRTHDAY);
		service.createNewTeam(TEAM_NAME, DESCRIPTION, "test-user-2");
		service.createNewRegister(REGISTER_NAME, TEAM_NAME);
		service.setUsersRegister(USER_NAME, REGISTER_NAME, TEAM_NAME);
		// remove normal user
		service.removeUserFromTeam(USER_NAME, TEAM_NAME);
		UserEntity deletedUser = service.getUser(USER_NAME);
		assertNotEquals(null, deletedUser);
		assertEquals(null, deletedUser.getTeam());
		// remove project owner user
		service.addUserToTeam(TEAM_NAME, USER_NAME);
		service.createNewProject(TEAM_NAME, PROJECT_NAME, DESCRIPTION,
				USER_NAME, BIRTHDAY);
		service.removeUserFromTeam(USER_NAME, TEAM_NAME);
		deletedUser = service.getUser(USER_NAME);
		ProjectEntity project = service.getProject(PROJECT_NAME, TEAM_NAME);
		assertNotEquals(null, deletedUser);
		assertEquals(null, deletedUser.getTeam());
		assertEquals(null, project);
		// remove team admin
		service.removeUserFromTeam("test-user-2", TEAM_NAME);
		UserEntity userTwo = service.getUser("test-user-2");
		TeamEntity team = service.getTeam(TEAM_NAME);
		assertNotEquals(null, userTwo);
		assertEquals(null, userTwo.getTeam());
		assertEquals(null, team);
	}

}