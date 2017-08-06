import cz.msebera.android.httpclient.client.ClientProtocolException;
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
/**
 * Created by Raphael on 14.06.2017.
 */
public class Main {

	public static void main(String[] args) {
		String url ="http://localhost:8080/ProjectManager-0.0.1-SNAPSHOT/pmservice/login";
		String username = "yolo";
		String password = "swag";
		String[] params = {url, username, password};
		JSONObject result = null;
		StringBuilder stringBuilder = new StringBuilder();
		HttpClient client = HttpClientBuilder.create().build();
		cz.msebera.android.httpclient.client.methods.HttpPost loginRequest = new cz.msebera.android.httpclient.client.methods.HttpPost(params[0]);
		JSONObject userInfo = createUserInfo(params[1], params[2]);
		if (userInfo != null) {
			cz.msebera.android.httpclient.entity.StringEntity stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(userInfo.toString(), "UTF-8");
			stringEntity.setContentType("application/json");
			if (stringEntity != null) {
				loginRequest.setEntity(stringEntity);
				try {
					cz.msebera.android.httpclient.HttpResponse response = client.execute(loginRequest);
					InputStream input = response.getEntity().getContent();
					String tempJson;
					if (input != null) {
						BufferedReader reader
								= new BufferedReader(new InputStreamReader(input));
						while ((tempJson = reader.readLine()) != null) {
							stringBuilder.append(tempJson);
						}
						result = new JSONObject(stringBuilder.toString());
						System.out.println(result.toString());
						//return result;
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}
	}

		private static JSONObject createUserInfo(String username, String password) {
			JSONObject result = null;
			try {
				result = new JSONObject("{\"username\": \"" + username + "\", " + "\"password\": "
						+ "\"" + password + "\"}");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return result;
		}
	}
