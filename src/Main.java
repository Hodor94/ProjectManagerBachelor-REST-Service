import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.*;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class Main {

	public static void main(String[] args) {
		ArrayList<String> arrayList = new ArrayList<>();
		arrayList.add("FIRST");
		arrayList.add("SECOND");
		JSONObject data = new JSONObject();
		try {
			data.put("array", arrayList);
			System.out.println(data.toString());
			JSONArray jsonArray = data.getJSONArray("array");
			for (int i = 0; i < jsonArray.length(); i++) {
				String temp = jsonArray.getString(i);
				System.out.println(temp);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}