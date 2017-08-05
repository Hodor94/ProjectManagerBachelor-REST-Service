package entity;

import dao.TaskDAO;
import dao.TeamDAO;
import dao.UserDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.DataService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Raphael on 18.06.2017.
 */
class TaskEntityTest {

	private static final String TASK_NAME = "test-task";
	private static final String DESCRIPTION = "test-description";
	private static final String TEAM_NAME = "test-team";
	private static final String USER_NAME = "test-user";
	private static final String DATE = "03.01.1994 00:00:00";
	private static UserEntity testUser;
	private static TeamEntity testTeam;
	private static TaskEntity testTask;
	private static Calendar testDate;
	private static SimpleDateFormat formatter;


	@BeforeAll
	private static void setUp() {
		formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		testDate = Calendar.getInstance();
		try {
			testDate.setTime(formatter.parse(DATE));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		testUser = new UserEntity(USER_NAME, "", "",
				"", "", "", "", DATE);
		testTeam = new TeamEntity(TEAM_NAME, DESCRIPTION, testUser);
		testTask = new TaskEntity(TASK_NAME, DESCRIPTION, DATE, testTeam);
	}

	@Test
	public void testCreateNewTask() {
		DataService service = new DataService();
		service.registerUser(USER_NAME, "", "", "",
				"", "", "", DATE);
		service.createNewTeam(TEAM_NAME, DESCRIPTION, USER_NAME);
		service.createNewTask(TASK_NAME, DESCRIPTION, DATE, TEAM_NAME);
		TeamEntity team = service.getTeam(TEAM_NAME);
		TaskEntity fetchedTask = service.getTask(TASK_NAME, TEAM_NAME);
		assertEquals(testTask.getName(), fetchedTask.getName());
		assertEquals(testTask.getDeadline(), fetchedTask.getDeadline());
		assertEquals(testTask.getDescription(), fetchedTask.getDescription());
		assertEquals(testTask.getTeam().getName(),
				fetchedTask.getTeam().getName());
		assertEquals(null, fetchedTask.getWorker());
	}

	@Test
	public void testSetWorkerToTask() {
		DataService service = new DataService();
		UserDAO userDAO = new UserDAO();
		TeamDAO teamDAO = new TeamDAO();
		TaskDAO taskDAO = new TaskDAO();
		userDAO.saveOrUpdate(testUser);
		teamDAO.saveOrUpdate(testTeam);
		service.createNewTask(TASK_NAME, DESCRIPTION, DATE, TEAM_NAME);
		service.setWorkerToTask(TASK_NAME, USER_NAME, TEAM_NAME);
		TeamEntity team = teamDAO.getTeamByTeamName(TEAM_NAME);
		TaskEntity fetchedTask = taskDAO.getTaskByTaskName(TASK_NAME, team);
		assertEquals(testUser.getUsername(), fetchedTask.getWorker()
				.getUsername());

	}

	@Test
	public void testGetTask() {
		UserDAO userDAO = new UserDAO();
		userDAO.saveOrUpdate(testUser);
		TeamDAO teamDAO = new TeamDAO();
		teamDAO.saveOrUpdate(testTeam);
		TaskDAO taskDAO = new TaskDAO();
		taskDAO.saveOrUpdate(testTask);
		DataService service = new DataService();
		TaskEntity fetchedTask = service.getTask(TASK_NAME, TEAM_NAME);
		assertEquals(testTask.getTeam().getName(),
				fetchedTask.getTeam().getName());
		assertEquals(testTask.getDescription(), fetchedTask.getDescription());
		assertEquals(testTask.getDeadline(),
				fetchedTask.getDeadline());
		assertEquals(null, fetchedTask.getWorker());
	}

	@Test
	public void testRemoveTask() {
		DataService service = new DataService();
		service.registerUser(USER_NAME, "", "", "",
				"", "", "", DATE);
		service.createNewTeam(TEAM_NAME, DESCRIPTION, USER_NAME);
		service.createNewTask(TASK_NAME, DESCRIPTION, DATE, TEAM_NAME);
		TaskEntity task = service.getTask(TASK_NAME, TEAM_NAME);
		assertNotEquals(null, task);
		TeamEntity team = service.getTeam(TEAM_NAME);
		assertNotEquals(null, team);
		assertFalse(team.getTasks().isEmpty());
		assertEquals(task.getName(), team.getTasks().get(0).getName());
		// remove the task without a worker
		service.removeTask(TASK_NAME, TEAM_NAME);
		task = service.getTask(TASK_NAME, TEAM_NAME);
		assertEquals(null, task);
		team = service.getTeam(TEAM_NAME);
		assertTrue(team.getTasks().isEmpty());
		// create a task again and set a worker
		service.createNewTask(TASK_NAME, DESCRIPTION, DATE, TEAM_NAME);
		service.setWorkerToTask(TASK_NAME, USER_NAME, TEAM_NAME);
		UserEntity user = service.getUser(USER_NAME);
		task = service.getTask(TASK_NAME, TEAM_NAME);
		assertFalse(user.getTasks().isEmpty());
		assertEquals(task.getName(), user.getTasks().get(0).getName());
		// remove again and proove, if it'S deleted and the user is not the
		// worker anymore
		service.removeTask(TASK_NAME, TEAM_NAME);
		user = service.getUser(USER_NAME);
		task = service.getTask(TASK_NAME, TEAM_NAME);
		assertNotEquals(null, user);
		assertEquals(null, task);
		assertEquals(0, user.getTasks().size());
	}

}