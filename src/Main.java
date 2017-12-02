import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import entity.TeamEntity;
import entity.UserEntity;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import service.DataService;
import service.PasswordService;
import service.RESTService;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

public class Main {
	public static void main(String[] args) {
		ArrayList<String> users = new ArrayList<String>();
		users.add("admin");
		users.add("jeri");
		try {
			JSONObject result = new JSONObject();
			result.put("teamName", "Flying Unicorns");
			JSONArray arrayUsers = new JSONArray(users);
			result.put("users", arrayUsers);
			result.put("name", "");
			result.put("isSoloChat", "true");
		} catch (JSONException e) {

		}
	}
}

