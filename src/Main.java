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

import javax.xml.crypto.Data;

/**
 * Created by Raphael on 14.06.2017.
 */
public class Main {
	private static RESTService restService = new RESTService();

	public static void main(String[] args) {
		String username = "testUser";
		String password = "troll";
		String resultOfRegistration;
		DataService dataService = new DataService();
		if (dataService.getUser(username) == null) {
			try {
				JSONObject registerUserInfo = new JSONObject("{\"username\": "
						+ "\"" + username + "\", " + "\"password\": " + "\""
						+ password + "\", \"firstName\": " + null + ", " +
						"\"surname\": " + null + ", \"email\": " + null +
						", \"phoneNr\": " + null + ", \"address\": " + null +
						", \"birthday\": " + null +
						"}");
				resultOfRegistration = restService.registerUser
						(registerUserInfo).toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		System.out.println(restService.getSecretKey().getEncoded());
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
		if (loginInfo != null) {
			response = restService.loginUser(loginInfo);
		}
		System.out.println(response.toString());
	}
}
