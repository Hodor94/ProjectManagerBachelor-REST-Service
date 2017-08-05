package entity;

import dao.RegisterDAO;
import dao.TeamDAO;
import dao.UserDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.DataService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Raphael on 20.06.2017.
 */
class RegisterEntityTest {

	private static final String REGISTER_NAME = "test-register";
	private static final String USER_NAME = "test-user";
	private static final String TEAM_NAME = "test-team";
	private static final String DESCRIPTION = "Just for testing";
	private static final String BIRTHDAY = "03.01.1994 00:00:00";
	private static SimpleDateFormat formatter;
	private static TeamEntity testTeam;
	private static RegisterEntity testRegister;
	private static UserEntity testUser;
	private static Calendar birthday;

	@BeforeAll
	private static void setUp() {
		formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		birthday = Calendar.getInstance();
		try {
			birthday.setTime(formatter.parse(BIRTHDAY));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		testUser = new UserEntity(USER_NAME, "", "",
				"", "", "", "", BIRTHDAY);
		testTeam = new TeamEntity(TEAM_NAME, DESCRIPTION, testUser);
		testRegister = new RegisterEntity(REGISTER_NAME, testTeam);
	}

	@Test
	public void testCreateRegister() {
		UserDAO userDAO = new UserDAO();
		userDAO.saveOrUpdate(testUser);
		TeamDAO teamDAO = new TeamDAO();
		teamDAO.saveOrUpdate(testTeam);
		DataService service = new DataService();
		service.createNewRegister(REGISTER_NAME, TEAM_NAME);
		RegisterDAO registerDAO = new RegisterDAO();
		RegisterEntity register = service.getRegister(REGISTER_NAME, TEAM_NAME);
		assertEquals(testRegister.getName(), register.getName());
		assertEquals(testRegister.getTeam().getName()
				, register.getTeam().getName());
	}

	@Test
	public void testRemoveRegister() {
		DataService service = new DataService();
		service.registerUser(USER_NAME, "", "", "",
				"", "", "", BIRTHDAY);
		service.createNewTeam(TEAM_NAME, DESCRIPTION, USER_NAME);
		service.createNewRegister(REGISTER_NAME, TEAM_NAME);
		service.setUsersRegister(USER_NAME, REGISTER_NAME, TEAM_NAME);
		UserEntity user = service.getUser(USER_NAME);
		assertNotEquals(null, user.getRegister());
		service.removeRegister(REGISTER_NAME, TEAM_NAME);
		user = service.getUser(USER_NAME);
		assertNotEquals(null, user);
		assertEquals(null, user.getRegister());
		TeamEntity team = service.getTeam(TEAM_NAME);
		assertNotEquals(null, team);
		assertEquals(0, team.getRegisters().size());
		assertEquals(null,
				service.getRegister(REGISTER_NAME, TEAM_NAME));
	}

}