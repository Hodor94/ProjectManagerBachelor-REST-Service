package entity;

import com.sun.org.apache.regexp.internal.RE;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.DataService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by Raphael on 25.06.2017.
 */
class TeamEntityTest {

	private static final String USER_NAME_ONE = "test-user-one";
	private static final String USER_NAME_TWO = "test-user-two";
	private static final String BIRTHDAY = "03.01.1994 00:00:00";
	private static final String TEAM_NAME = "test-team";
	private static final String DESCRIPTION = "Just for testing!";
	private static final String TASK_NAME = "test-task";
	private static final String REGISTER_NAME = "test-register";
	private static final String PROJECT_NAME = "test-project";
	private static final int NUMBER_OF_APPOINTMENTS = 31; // Lucky number
	private static final String APPOINMENT_NAME = "test-appointment";
	private static final String CHAT_NAME = "test-chat";
	private static final String MESSAGE = "test-message";
	private static Calendar birthday;
	private static SimpleDateFormat formatter;
	private static Collection<UserEntity> users;

	@BeforeAll
	private static void setUp() {
		formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		birthday = Calendar.getInstance();
		users = new ArrayList<UserEntity>();
		try {
			birthday.setTime(formatter.parse(BIRTHDAY));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetUsersOfTeam() {
		DataService service = new DataService();
		service.registerUser(USER_NAME_ONE, "", "", "",
				"", "", "", BIRTHDAY);
		service.registerUser(USER_NAME_TWO, "", "", "",
				"", "", "", BIRTHDAY);
		service.createNewTeam(TEAM_NAME, DESCRIPTION, USER_NAME_ONE);
		service.addUserToTeam(TEAM_NAME, USER_NAME_TWO);
		ArrayList<UserEntity> fetchedUsers
				= new ArrayList<UserEntity>(service.getUsersOfTeam(TEAM_NAME));
		assertNotEquals(null, fetchedUsers);
		assertEquals(2, fetchedUsers.size());
		UserEntity userOne = fetchedUsers.get(0);
		UserEntity userTwo = fetchedUsers.get(1);
		assertEquals(USER_NAME_ONE, userOne.getUsername());
		assertEquals(USER_NAME_TWO, userTwo.getUsername());
	}

	@Test
	public void testRemoveTeam() {
		DataService service = new DataService();
		service.registerUser(USER_NAME_ONE, "", "", "",
				"", "", "", BIRTHDAY);
		service.registerUser(USER_NAME_TWO, "", "", "",
				"", "", "", BIRTHDAY);
		service.createNewTeam(TEAM_NAME, DESCRIPTION, USER_NAME_ONE);
		service.addUserToTeam(TEAM_NAME, USER_NAME_TWO);
		service.createNewTask(TASK_NAME, DESCRIPTION, BIRTHDAY, TEAM_NAME,
				USER_NAME_ONE);
		service.setWorkerToTask(TASK_NAME, USER_NAME_ONE, TEAM_NAME);
		service.createNewRegister(REGISTER_NAME, TEAM_NAME, "#ffffff");
		service.setUsersRegister(USER_NAME_TWO, REGISTER_NAME, TEAM_NAME);
		service.createNewProject(TEAM_NAME, PROJECT_NAME, DESCRIPTION,
				USER_NAME_ONE, BIRTHDAY);
		service.addUserToProject(USER_NAME_TWO, PROJECT_NAME, TEAM_NAME);
		service.createNewAppointment(APPOINMENT_NAME, DESCRIPTION, BIRTHDAY,
				PROJECT_NAME, TEAM_NAME);
		service.addUserToAppointment(USER_NAME_TWO, PROJECT_NAME, TEAM_NAME,
				0);
		UserEntity userOne = service.getUser(USER_NAME_ONE);
		UserEntity userTwo = service.getUser(USER_NAME_TWO);
		service.removeTeam(TEAM_NAME);
		userOne = service.getUser(USER_NAME_ONE);
		userTwo = service.getUser(USER_NAME_TWO);
		assertNotEquals(null, userOne);
		assertNotEquals(null, userTwo);
		assertEquals(null, userOne.getTeam());
		assertEquals(null, userTwo.getTeam());
		assertEquals(null, userOne.getRegister());
		TeamEntity team = service.getTeam(TEAM_NAME);
		assertEquals(null, team);
		TaskEntity task = service.getTask(TASK_NAME, TEAM_NAME);
		assertEquals(null, task);
		RegisterEntity register = service.getRegister(REGISTER_NAME, TEAM_NAME);
		assertEquals(null, register);
		ProjectEntity project = service.getProject(PROJECT_NAME, TEAM_NAME);
		assertEquals(null, project);
		AppointmentEntity appointment = service.getAppointment(PROJECT_NAME,
				0, TEAM_NAME);
		assertEquals(null, appointment);
	}

	@Test
	public void testCreateNewTeam() {
		DataService service = new DataService();
		service.registerUser(USER_NAME_ONE, "", "", "",
				"", "", "", BIRTHDAY);
		service.createNewTeam(TEAM_NAME, DESCRIPTION, USER_NAME_ONE);
		TeamEntity team = service.getTeam(TEAM_NAME);
		assertNotEquals(null, team);
		assertEquals(USER_NAME_ONE, team.getAdmin().getUsername());
		assertEquals(TEAM_NAME, team.getName());
		assertEquals(DESCRIPTION, team.getDescription());
		assertEquals(1, team.getUsers().size());
	}

	@Test
	public void testRequests() {
		DataService service = new DataService();
		// Register two users
		service.registerUser(USER_NAME_ONE, "", "", "",
				"", "", "", BIRTHDAY);
		service.registerUser(USER_NAME_TWO, "", "", "",
				"", "", "", BIRTHDAY);
		UserEntity userOne = service.getUser(USER_NAME_ONE);
		UserEntity userTwo = service.getUser(USER_NAME_TWO);
		assertNotNull(userOne);
		assertNotNull(userTwo);
		// Create new team
		service.createNewTeam(TEAM_NAME, DESCRIPTION, USER_NAME_ONE);
		TeamEntity team = service.getTeam(TEAM_NAME);
		assertNotNull(team);
		assertEquals(0, team.getRequestsOfUsers().size());
		// User two sends request to team for joining it
		// User two tries it twice. It is expected to be stored once.
		service.addRequestToTeam(team, userTwo);
		team = service.getTeam(TEAM_NAME);
		assertEquals(1, team.getRequestsOfUsers().size());
		service.addRequestToTeam(team, userTwo);
		team = service.getTeam(TEAM_NAME);
		assertEquals(1, team.getRequestsOfUsers().size());
	}

}