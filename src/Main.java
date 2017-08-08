import com.sun.org.apache.regexp.internal.RE;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import entity.UserEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import service.DataService;
import service.RESTService;

/**
 * Created by Raphael on 14.06.2017.
 */
public class Main {

	public static void main(String[] args) {
		String username = "EatMyStardust";
		String password = "troll";
		DataService dataService = new DataService();
		System.out.println(dataService.registerUser(username, password,
				null, null,
				null, null, null, null));
		UserEntity user = dataService.getUser(username);
		System.out.println(user.toSring());
		JSONObject loginInfo = null;
		JSONObject response = null;
		try {
			 loginInfo = new JSONObject("{\"username\": \""
					+ username + "\", \"password\": \"" + password + "\"}");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		RESTService restService = new RESTService();
		if (loginInfo != null) {
			response = restService.loginUser(loginInfo);
		}
		System.out.println(response.toString());
	}
}
