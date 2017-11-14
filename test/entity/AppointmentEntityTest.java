package entity;

import dao.ProjectDAO;
import dao.UserDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.DataService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Created by Raphael on 19.06.2017.
 */
class AppointmentEntityTest {

	private static final String PROJECT_NAME = "test-project";
	private static final String TEAM_NAME = "test-team";
	private static final String USER_NAME = "test-user";
	private static final String BIRTHDAY = "03.01.1994 00:00:00";
	private static final String DESCRIPTION = "Just for testing";
	private static final int LUCKY_NUMBER = 31;
	private static final String APPOINTMENT_NAME = "test-appointment";

	private static ProjectEntity testProject;
	private static TeamEntity testTeam;
	private static UserEntity testUser;
	private static AppointmentEntity testAppointment;
	private static Calendar birthday;
	private static SimpleDateFormat formatter;

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
		testProject = new ProjectEntity(PROJECT_NAME, DESCRIPTION, BIRTHDAY,
				testUser, testTeam);
		testAppointment = new AppointmentEntity(APPOINTMENT_NAME, DESCRIPTION,
				BIRTHDAY, testProject, new ArrayList<UserEntity>());
	}

	@Test
	public void testCreateNewAppointment() {
		DataService service = new DataService();
		service.registerUser(USER_NAME, "", "", "",
				"", "", "", BIRTHDAY);
		service.createNewTeam(TEAM_NAME, DESCRIPTION, USER_NAME);
		service.createNewProject(TEAM_NAME, PROJECT_NAME, DESCRIPTION,
				USER_NAME, BIRTHDAY);
		service.createNewAppointment(APPOINTMENT_NAME, DESCRIPTION, BIRTHDAY,
				PROJECT_NAME, TEAM_NAME);
		AppointmentEntity appointmentEntity = service.getAppointment(1);
		ProjectEntity project = service.getProject(PROJECT_NAME, TEAM_NAME);
		assertEquals(testAppointment.getName(), appointmentEntity.getName());
		assertEquals(testAppointment.getDeadline(),
				appointmentEntity.getDeadline());
		assertEquals(testAppointment.getDescription(),
				appointmentEntity.getDescription());
		assertEquals(testAppointment.getProject().getName(),
				appointmentEntity.getProject().getName());
		assertEquals(false, project.getAppointments().isEmpty());
		assertEquals(1, project.getNumberOfAppointments());
	}

	@Test
	public void testRemoveAppointment() {
		DataService service = new DataService();
		service.registerUser(USER_NAME, "", "", "",
				"", "", "", BIRTHDAY);
		service.createNewTeam(TEAM_NAME, DESCRIPTION, USER_NAME);
		service.createNewProject(TEAM_NAME, PROJECT_NAME, DESCRIPTION,
				USER_NAME, BIRTHDAY);
		service.createNewAppointment(APPOINTMENT_NAME, DESCRIPTION, BIRTHDAY,
				PROJECT_NAME, TEAM_NAME);
		service.addUserToAppointment(USER_NAME, PROJECT_NAME, TEAM_NAME, 1);
		service.removeAppointment(PROJECT_NAME, TEAM_NAME, 1);
		ProjectEntity project = service.getProject(PROJECT_NAME, TEAM_NAME);
		TeamEntity team = service.getTeam(TEAM_NAME);
		UserEntity user = service.getUser(USER_NAME);
		assertEquals(0, project.getNumberOfAppointments());
		assertEquals(0, project.getAppointments().size());
		assertEquals(0, user.getAppointmentsTakingPart().size());
		service.addUserToAppointment(USER_NAME, PROJECT_NAME, TEAM_NAME,
				1);
		AppointmentEntity appointment = service.getAppointment(1);
		assertEquals(null, appointment);
		assertNotEquals(null, project);
		assertNotEquals(null, team);
		assertTrue(project.getAppointments().isEmpty());
	}

	@Test
	public void testTakePartAtAppointment() {
		DataService service = new DataService();
		service.registerUser(USER_NAME, "", "", "",
				"", "", "", BIRTHDAY);
		service.createNewTeam(TEAM_NAME, DESCRIPTION, USER_NAME);
		service.createNewProject(TEAM_NAME, PROJECT_NAME, DESCRIPTION,
				USER_NAME, BIRTHDAY);
		service.createNewAppointment(APPOINTMENT_NAME, DESCRIPTION, BIRTHDAY,
				PROJECT_NAME, TEAM_NAME);
		ProjectEntity project = service.getProject(PROJECT_NAME, TEAM_NAME);
		assertEquals(1, project.getAppointments().size());
		assertEquals(1, project.getNumberOfAppointments());
		UserEntity user = service.getUser(USER_NAME);
		assertEquals(0, user.getAppointmentsTakingPart().size());
		StatisticEntity statisticOfUser
				= service.getStatisticOfUser(USER_NAME, 0);
		assertNotEquals(null, statisticOfUser);
		assertEquals(1, statisticOfUser.getNumberOfAllAppointments());
		assertEquals(0, statisticOfUser.getNumberOfParticipiation());
		assertEquals(0, statisticOfUser.getPercentage());
		service.takePartAtAppointment(1, USER_NAME);
		user = service.getUser(USER_NAME);
		project = service.getProject(PROJECT_NAME, TEAM_NAME);
		statisticOfUser = service.getStatisticOfUser(USER_NAME, 1);
		assertEquals(1, user.getAppointmentsTakingPart().size());
		assertEquals(1, user.getAppointmentsTakingPart().get(0).getId());
		assertEquals(1, project.getNumberOfAppointments());
		assertEquals(1, statisticOfUser.getNumberOfParticipiation());
		assertEquals(1, statisticOfUser.getNumberOfAllAppointments());
		assertEquals(1, statisticOfUser.getPercentage());
		service.createNewAppointment("test-Appointment-2", DESCRIPTION,
				BIRTHDAY, PROJECT_NAME, TEAM_NAME);
		statisticOfUser = service.getStatisticOfUser(USER_NAME, 2);
		assertEquals(2, statisticOfUser.getNumberOfAllAppointments());
		assertEquals(1, statisticOfUser.getNumberOfParticipiation());
		assertEquals(0.5, statisticOfUser.getPercentage());
	}
}