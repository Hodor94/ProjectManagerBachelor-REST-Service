import com.sun.jersey.core.util.Base64;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import service.DataService;
import service.RESTService;
import sun.net.www.http.HttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Raphael on 06.08.2017.
 */
public class MainTest {

	public static void main(String[] args) {
		/*cz.msebera.android.httpclient.client.HttpClient client
				= HttpClientBuilder.create().build();
		JSONObject userInfo = null;
		JSONObject content = null;
		HttpResponse response = null;
		BufferedReader reader;
		StringEntity body;
		InputStream input = null;
		String temp = null;
		String token = null;
		// Register user
		HttpPost registerUser = new HttpPost
				("http://localhost:8080/ProjectManager-0.0.1-SNAPSHOT" +
						"/pmservice/register");
		try {
			userInfo = new JSONObject("{\"firstName\": " +
					"\"Raphael\", \"surname\": \"Grum\", \"birthday\": " +
					"\"03.01.1994 00:00:00\", \"address\": \"adresse\"," +
					"\"email\": \"raphael-grum@web.de\", \"phoneNr\": " +
					"\"1234567890\", \"username\": \"EatMyStardust94\"," +
					"\"password\": \"admin\"}");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		body = new StringEntity(userInfo.toString(), "UTF-8");
		body.setContentType("application/json");
		registerUser.setEntity(body);
		try {
			response = client.execute(registerUser);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (response != null) {
			try {
				input = response.getEntity().getContent();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (input != null) {
			reader = new BufferedReader(new InputStreamReader(input));
			try {
				temp = reader.readLine();
				System.out.println("Register answer: " + temp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//---------------------------------------------------------------

		// Login
		HttpPost login = new HttpPost("http://localhost:8080/ProjectManager-0.0.1-SNAPSHOT" +
				"/pmservice/login");
		try {
			userInfo = new JSONObject("{\"username\": \"EatMyStardust94\"," +
					" \"password\": \"admin\"}");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		body = new StringEntity(userInfo.toString(), "UTF-8");
		body.setContentType("application/json");
		login.setEntity(body);
		try {
			response = client.execute(login);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			input = response.getEntity().getContent();
		} catch (IOException e) {
			e.printStackTrace();
		}
		reader = new BufferedReader(new InputStreamReader(input));
		try {
			temp = reader.readLine();
			System.out.println(temp);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (temp != null) {
			try {
				content = new JSONObject(temp);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (content != null) {
			try {
				token = content.getString("token");
				System.out.println(token);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		HttpPost validateToken
				= new HttpPost("http://localhost:8080/ProjectManager-0.0.1-SNAPSHOT" +
		"/pmservice/test");
		try {
			userInfo = new JSONObject("{\"token\":  \"" + token + "\"}");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		body = new StringEntity(userInfo.toString(), "UTF-8");
		body.setContentType("application/json");

		validateToken.setEntity(body);

		try {
			response = client.execute(validateToken);
			input = response.getEntity().getContent();
			reader = new BufferedReader(new InputStreamReader(input));
			temp = reader.readLine();
			System.out.println(temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		RESTService service = new RESTService();
		JSONObject login = null;
		JSONObject register = null;
		JSONObject tokenObject = null;
		try {
			register = new JSONObject("{\"firstName\": \"Raphael\", " +
					"\"surname\": \"Grum\", \"birthday\": \"03.01.1994 " +
					"00:00:00\", \"address\": \"Gärtnerweg 1\", " +
					"\"email\": \"r-g@web.de\", \"phoneNr\": \"1234567890\"," +
					" \"username\": \"EatMyStardust94\", \"password\": " +
					"\"admin\"}");
			login = new JSONObject("{\"username\": " +
					"\"EatMyStardust94\", \"password\": \"admin\"}");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		JSONObject responseRegister = service.registerUser(register);
		System.out.println(responseRegister);
		JSONObject responseLogin = service.loginUser(login);
		System.out.println(responseLogin.toString());
		String token = null;
		try {
			token = responseLogin.getString("token");
			tokenObject = new JSONObject("{\"token\": " + "\"" +  token +
					"\"}");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println(token);
		JSONObject responseToken = service.testvalidateToken(tokenObject);
		System.out.println(responseToken.toString());

	}
}
