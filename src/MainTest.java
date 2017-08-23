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

import java.io.*;

/**
 * Created by Raphael on 06.08.2017.
 */
public class MainTest {

	public static void main(String[] args) {
		RESTService service = new RESTService();
		JSONObject register = null;
		JSONObject login = null;
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
		JSONObject userLogin = null;
		try {
			userLogin = responseLogin.getJSONObject("user");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			String address = userLogin.getString("address");
			System.out.println("Address: " + address);
			System.out.println("Address-bytes: " + address.getBytes());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println(responseLogin.toString());
	}
}
