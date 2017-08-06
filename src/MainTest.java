import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import service.RESTService;

/**
 * Created by Raphael on 06.08.2017.
 */
public class MainTest {

	public static void main(String[] args) {
		RESTService service = new RESTService();
		JSONObject testUser = null;
		try {
			testUser = new JSONObject("{\"username\": \"yolo\"," +
					"\"password\": \"swag\"}");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
