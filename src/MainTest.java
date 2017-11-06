import com.sun.jersey.core.util.Base64;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import service.DataService;
import service.RESTService;
import sun.net.www.http.HttpClient;

import java.io.*;

/**
 * Created by Raphael on 06.08.2017.
 */
public class MainTest {

	public static void main(String[] args) {
		RESTService service = new RESTService();
		JSONObject register = null;
		JSONObject login = null;
		JSONObject getUser = null;
		String token = null;
		try {
			register = new JSONObject("{\"firstName\": \"Raphael\", " +
					"\"surname\": \"ÄÖÜ\", \"birthday\": \"03.01.1994\", " +
					"\"email\": \"r-g@web.de\", \"address\": \"Gärtnerwög " +
					"1\", \"phoneNr\": \"1234567890\", " +
					"\"username\": \"admin\", \"password\": \"admin\"}");
			login = new JSONObject("{\"username\": \"admin\", " +
					"\"password\": \"admin\"}");
		} catch (JSONException exc) {
			exc.printStackTrace();
		}
		JSONObject responseRegister = service.registerUser(register);
		System.out.println(responseRegister.toString());
		JSONObject responseLogin = service.loginUser(login);
		try {
			token = responseLogin.getString("token");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (token != null) {
			try {
				JSONObject getUserData = new JSONObject("{\"token\":" +
						" \"" + token + "\", \"username\": \"admin\"}");
				JSONObject getUserResult = service.getUser(getUserData);
				System.out.println(getUserResult.toString());
				JSONObject createTeam
						= new JSONObject("{\"token\": \"" + token
						+ "\", \"teamName\": \"testTeam\", " +
						"\"teamDescription\": \"Just for testing\", " +
						"\"admin\": \"admin\"}");
				JSONObject responseCreateTeam = service.createTeam(createTeam);
				System.out.println(responseCreateTeam.toString());
				JSONObject getAllTeams = new JSONObject("{\"token\": " +
						"\"" + token + "\"}");
				JSONObject responseGetTeams = service.getTeams(getAllTeams);
				System.out.println(responseGetTeams.toString());
				String fetchedTeams = (String) responseGetTeams.get("teams");
				System.out.println(fetchedTeams);
				JSONObject fetchTeam
						= new JSONObject("{\"token\": \"" + token + "\", " +
						"\"teamName\": \"" + "testTeam" + "\"}");
				JSONObject fetchedTeam = new JSONObject(service.getTeam
						(fetchTeam).toString());
				System.out.println(fetchedTeam.toString());
				JSONObject editTeam = new JSONObject("{\"token\": \"" +
						token + "\", \"teamName\": \"" + "CHANGES" + "\", " +
						"\"teamDescription\": \"" + "CHANGES" + "\", " +
						"\"admin\": \"" + "admin" + "\"}");
				JSONObject editResult = service.editTeam(editTeam);
				System.out.println(editResult.toString());
				JSONObject requestTeamRequests
						= new JSONObject("{\"token\": \"" + token + "\", " +
						"\"teamName\": \"" + "testTeam" + "\"}");
				JSONObject resultOfRequestsRequest
						= service.getRequestsOfTeam(requestTeamRequests);
				System.out.println(resultOfRequestsRequest.toString());
				JSONObject getTeamMembers = new JSONObject();
				getTeamMembers.put("token", token);
				getTeamMembers.put("teamName", "testTeam");
				JSONObject responseGetTeamMembers = service.getTeamMembers
						(getTeamMembers);
				JSONObject deleteUser = new JSONObject("{\"token\": \"" +
						token + "\", \"username\": \"admin\"}");
				JSONObject resultDeleteUser = service.leaveApp(deleteUser);
				System.out.println(resultDeleteUser.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}


	}
}
