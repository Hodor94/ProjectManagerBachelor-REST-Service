package entity;

import com.sun.jersey.json.impl.provider.entity.JSONArrayProvider;
import dao.AppointmentDAO;
import dao.ProjectDAO;
import dao.TeamDAO;
import dao.UserDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.DataService;

import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Raphael on 18.06.2017.
 */
class ProjectEntityTest {

	private static final String TEST_NAME = "test-project";
	private static final String DESCRIPTION = "Just for testing";
	private static final String USER_NAME = "test-user";
	private static final String USER_NAME_TWO = "test-user-two";
	private static final String TEAM_NAME = "test-team";
	private static final String BIRTHDAY = "03.01.1994 00:00:00";
	private static final String APPOINTMEN_NAME = "test-appointment";
	private static final String USER_NAME_THREE = "test-user-three";

	private static Calendar deadline;
	private static UserEntity testUser;
	private static TeamEntity testTeam;
	private static ProjectEntity testProject;
	private static SimpleDateFormat formatter;


	@BeforeAll
	private static void setUp() {
		formatter = new SimpleDateFormat("dd.MM.yyyy 00:00:00");
		deadline = Calendar.getInstance();
		testUser = new UserEntity(USER_NAME, "", "",
				"", "", "", "", BIRTHDAY);
		testTeam = new TeamEntity(TEAM_NAME, DESCRIPTION, testUser);
		testProject = new ProjectEntity(TEST_NAME, DESCRIPTION, BIRTHDAY,
				 testUser, testTeam);
	}

	@Test
	public void testCreateNewProject() {
		DataService service = new DataService();
		service.registerUser(USER_NAME, "", "", "",
				"", "", "", BIRTHDAY);
		service.createNewTeam(TEAM_NAME, DESCRIPTION, USER_NAME);
		service.createNewProject(TEAM_NAME, TEST_NAME, DESCRIPTION, USER_NAME,
				BIRTHDAY);
		UserEntity user = service.getUser(USER_NAME);
		assertEquals(TEST_NAME, user.getProjectsTakingPart().get(0).getName());
		TeamEntity team = service.getTeam(TEAM_NAME);
		assertEquals(TEST_NAME, team.getProjects().get(0).getName());
		assertEquals(false, user.getStatistics().isEmpty());
		ProjectEntity project = service.getProject(TEST_NAME, TEAM_NAME);
		assertEquals(false, project.getStatistics().isEmpty());
	}

	@Test
	public void testRemoveProject() {
		DataService service = new DataService();
		service.registerUser(USER_NAME, "", "", "",
				"", "", "", BIRTHDAY);
		service.registerUser(USER_NAME_TWO, "", "", "",
				"", "", "", BIRTHDAY);
		service.createNewTeam(TEAM_NAME, DESCRIPTION, USER_NAME);
		service.addUserToTeam(TEAM_NAME, USER_NAME_TWO);
		service.createNewProject(TEAM_NAME, TEST_NAME, DESCRIPTION, USER_NAME,
				BIRTHDAY);
		service.addUserToProject(USER_NAME_TWO, TEST_NAME, TEAM_NAME);
		UserEntity userTwo = service.getUser(USER_NAME_TWO);
		ProjectEntity project = service.getProject(TEST_NAME, TEAM_NAME);
		assertEquals(false, userTwo.getProjectsTakingPart().isEmpty());
		assertEquals(false, userTwo.getStatistics().isEmpty());
		assertEquals(USER_NAME_TWO, project.getStatistics().get(1)
				.getUser().getUsername());
		assertEquals(USER_NAME_TWO, project.getUsers().get(1).getUsername());
		service.createNewAppointment(APPOINTMEN_NAME, DESCRIPTION, BIRTHDAY,
				TEST_NAME, TEAM_NAME);
		project = service.getProject(TEST_NAME, TEAM_NAME);
		assertEquals(false, project.getAppointments().isEmpty());
		AppointmentEntity appointment = service.getAppointment(TEST_NAME, 1,
				TEAM_NAME);
		assertEquals(project.getName(), appointment.getProject().getName());
		service.removeProject(TEST_NAME, TEAM_NAME);
		UserEntity userOne = service.getUser(USER_NAME);
		userTwo = service.getUser(USER_NAME_TWO);
		assertEquals(true, userOne.getProjectsTakingPart().isEmpty());
		assertEquals(true, userOne.getStatistics().isEmpty());
		assertEquals(true, userTwo.getProjectsTakingPart().isEmpty());
		assertEquals(true, userTwo.getStatistics().isEmpty());
		assertEquals(null, service.getAppointment(TEST_NAME,
				1, TEAM_NAME));
		assertEquals(null, service.getProject(TEST_NAME, TEAM_NAME));
	}

	@Test
	public void testAddUserToProject() {
		DataService service = new DataService();
		service.registerUser(USER_NAME, "", "", "",
				"", "", "", BIRTHDAY);
		service.registerUser(USER_NAME_TWO, "", "", "",
				"", "", "", BIRTHDAY);
		service.createNewTeam(TEAM_NAME, DESCRIPTION, USER_NAME);
		service.createNewProject(TEAM_NAME, TEST_NAME, DESCRIPTION, USER_NAME,
				BIRTHDAY);
		assertEquals(1,
				service.getStatisticsOfProject(TEST_NAME, TEAM_NAME).size());
		service.addUserToProject(USER_NAME_TWO, TEST_NAME, TEAM_NAME);
		assertEquals(2,
				service.getStatisticsOfProject(TEST_NAME, TEAM_NAME).size());
		assertEquals(2,
				service.getProject(TEST_NAME, TEAM_NAME).getUsers().size());
		assertEquals(TEST_NAME, service.getUser(USER_NAME)
				.getProjectsTakingPart().get(0).getName());
		assertEquals(TEST_NAME, service.getUser(USER_NAME_TWO)
				.getProjectsTakingPart().get(0).getName());
		assertEquals(USER_NAME, service.getProject(TEST_NAME, TEAM_NAME)
				.getUsers().get(0).getUsername());
		assertEquals(USER_NAME_TWO, service.getProject(TEST_NAME, TEAM_NAME)
				.getUsers().get(1).getUsername());

	}

	@Test
	public void testGetStatisticsOfProject() {
		DataService service = new DataService();
		service.registerUser(USER_NAME, "", "", "",
				"", "", "", BIRTHDAY);
		service.registerUser(USER_NAME_TWO, "", "", "",
				"", "", "", BIRTHDAY);
		service.createNewTeam(TEAM_NAME, DESCRIPTION, USER_NAME);
		service.addUserToTeam(TEAM_NAME, USER_NAME_TWO);
		service.createNewProject(TEAM_NAME, TEST_NAME, DESCRIPTION,
				USER_NAME_TWO, BIRTHDAY);
		service.addUserToProject(USER_NAME, TEST_NAME, TEAM_NAME);
		ArrayList<StatisticEntity> fetchedStatistics
				= new ArrayList<StatisticEntity>
				(service.getStatisticsOfProject(TEST_NAME, TEAM_NAME));
		assertNotEquals(null, fetchedStatistics);
		assertEquals(2, fetchedStatistics.size());
		assertEquals(USER_NAME, fetchedStatistics.get(1).getUser()
				.getUsername());
		assertEquals(USER_NAME_TWO, fetchedStatistics.get(0).getUser()
				.getUsername());
	}

	@Test
	public void testGetStatisticOfUser() {
		DataService service = new DataService();
		service.registerUser(USER_NAME, "", "", "",
				"", "", "", BIRTHDAY);
		service.createNewTeam(TEAM_NAME, DESCRIPTION, USER_NAME);
		service.createNewProject(TEAM_NAME, TEST_NAME, DESCRIPTION, USER_NAME,
				BIRTHDAY);
		UserEntity user = service.getUser(USER_NAME);
		assertFalse(user.getStatistics().isEmpty());
		StatisticEntity statistic = service.getStatisticOfUser(USER_NAME,
				TEST_NAME, TEAM_NAME);
		assertNotEquals(null, statistic);
		assertEquals(USER_NAME, statistic.getUser().getUsername());
	}

	@Test
	public void testRemoveUserFromProject() {
		DataService service = new DataService();
		service.registerUser(USER_NAME, "", "", "",
				"", "", "", BIRTHDAY);
		service.registerUser(USER_NAME_TWO, "", "", "",
				"", "", "", BIRTHDAY);
		service.registerUser(USER_NAME_THREE, "", "",
				"", "", "", "", BIRTHDAY);
		service.createNewTeam(TEAM_NAME, DESCRIPTION, USER_NAME);
		service.addUserToTeam(TEAM_NAME, USER_NAME_TWO);
		service.addUserToTeam(TEAM_NAME, USER_NAME_THREE);
		service.createNewProject(TEAM_NAME, TEST_NAME, DESCRIPTION,
				USER_NAME_TWO, BIRTHDAY);
				service.addUserToProject(USER_NAME, TEST_NAME, TEAM_NAME);
		service.addUserToProject(USER_NAME_THREE, TEST_NAME, TEAM_NAME);
		ProjectEntity project = service.getProject(TEST_NAME, TEAM_NAME);
		UserEntity userOne = service.getUser(USER_NAME);
		UserEntity userTwo = service.getUser(USER_NAME_TWO);
		UserEntity userThree = service.getUser(USER_NAME_THREE);
		assertEquals(3, project.getUsers().size());
		assertEquals(project.getId(),
				userOne.getProjectsTakingPart().get(0).getId());
		assertEquals(project.getId(),
				userTwo.getProjectsTakingPart().get(0).getId());
		assertEquals(project.getId(),
				userThree.getProjectsTakingPart().get(0).getId());
		// Remove normal user
		service.removeUserFromProject(USER_NAME_THREE, TEST_NAME, TEAM_NAME);
		userThree = service.getUser(USER_NAME_THREE);
		project = service.getProject(TEST_NAME, TEAM_NAME);
		assertEquals(0, userThree.getProjectsTakingPart().size());
		assertEquals(2, project.getUsers().size());
		// Remove the team admin
		service.removeUserFromProject(USER_NAME, TEST_NAME, TEAM_NAME);
		userOne = service.getUser(USER_NAME);
		project = service.getProject(TEST_NAME, TEAM_NAME);
		assertEquals(0, userOne.getProjectsTakingPart().size());
		assertEquals(1, project.getUsers().size());
		// Add user one again to the project
		service.addUserToProject(USER_NAME, TEST_NAME, TEAM_NAME);
		project = service.getProject(TEST_NAME, TEAM_NAME);
		assertEquals(2, project.getUsers().size());
		// Remove project owner
		service.removeUserFromProject(USER_NAME_TWO, TEST_NAME, TEAM_NAME);
		project = service.getProject(TEST_NAME, TEAM_NAME);
		userOne = service.getUser(USER_NAME);
		userTwo = service.getUser(USER_NAME_TWO);
		assertEquals(null, project);
		assertEquals(0, userOne.getProjectsTakingPart().size());
		assertEquals(0, userTwo.getProjectsTakingPart().size());
		assertEquals(UserRole.ADMINISTRATOR, userOne.getRole());
		assertEquals(UserRole.USER, userTwo.getRole());
	}

}