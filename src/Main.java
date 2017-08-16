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
import cz.msebera.android.httpclient.entity.*;
import service.DataService;
import service.RESTService;

import javax.xml.crypto.Data;

/**
 * Created by Raphael on 14.06.2017.
 */
public class Main {
	private static RESTService restService = new RESTService();
	public static String token;

	public static void main(String[] args) {
		JSONObject result = null;
		StringBuilder stringBuilder = new StringBuilder();
		HttpClient client = HttpClientBuilder.create().build();
		cz.msebera.android.httpclient.client.methods.HttpPost loginRequest =
				new cz.msebera.android.httpclient.client.methods.HttpPost
				("http://localhost:5500/ProjectManager-0.0.1-SNAPSHOT" +
						"/pmservice/login");
		JSONObject user = createUserInfo("EatMyStardust94", "admin");
		if (user != null) {
			cz.msebera.android.httpclient.entity.StringEntity stringEntity =
					new cz.msebera.android.httpclient.entity.StringEntity(user.toString(), "UTF-8");
			stringEntity.setContentType("application/json");
			if (stringEntity != null) {
				loginRequest.setEntity(stringEntity);
				try {
					cz.msebera.android.httpclient.HttpResponse response
							= client.execute(loginRequest);
					InputStream inputStream = response.getEntity().getContent();
					if (inputStream != null) {
						BufferedReader reader = new BufferedReader(new
								InputStreamReader(inputStream));
						String tempJson;
						while ((tempJson = reader.readLine()) != null) {
							stringBuilder.append(tempJson);
						}
						result = new JSONObject(stringBuilder.toString());
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		if (result != null) {
			setToken(result);
			System.out.println(restService.validateUserToken(token));
		}

	}

	private static JSONObject createUserInfo(String username, String password) {
		JSONObject result = null;
		try {
			result = new JSONObject("{\"username\": \"" + username + "\"" +
					", \"password\": \"" + password + "\"}");
		} catch (JSONException exc) {
			exc.printStackTrace();
		}
		return result;
	}

	private static void setToken(JSONObject userData) {
		try {
			token = userData.getString("token");
			System.out.println(token);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
