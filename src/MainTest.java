import com.sun.jersey.core.util.Base64;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import service.DataService;
import service.RESTService;

/**
 * Created by Raphael on 06.08.2017.
 */
public class MainTest {

	public static void main(String[] args) {
		System.out.println("c2VjcmV0");
		System.out.println(Base64.encode("c2VjcmV0"));
	}
}
