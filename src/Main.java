import entity.TeamEntity;
import entity.UserEntity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Raphael on 14.06.2017.
 */
public class Main {

	public static void main(String[] args) {
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			HttpPost postRequest = new HttpPost
					("http://localhost:8080/pmservice/register/user"); // 5500
			JSONObject jsonObject
					= new JSONObject("{\"firstName\": \"Raphael\", " +
					"\"surname\": \"Grum\", \"birthday\": \"03.01.1994 " +
					"00:00:00\", \"address\": \"Gärtnerweg 1\", " +
					"\"email\": \"raphael-grum@web.de\", \"phoneNr\": " +
					"\"0101010101001\", \"username\": \"star\", " +
					"\"password\": \"password\"}");
			StringEntity stringEntity = new StringEntity(jsonObject.toString(),
					"UTF-8");
			stringEntity.setContentType("application/json");
			postRequest.setEntity(stringEntity);
			HttpResponse response = client.execute(postRequest);
			if (response.getStatusLine().getStatusCode() != 200) {

			}
			InputStream resultStream = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader
					(resultStream));
			String output;
			System.out.println("Output from Server ... \n");
			while((output = reader.readLine()) != null) {
				System.out.println(output);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
