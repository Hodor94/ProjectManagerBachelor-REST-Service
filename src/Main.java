import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.hibernate.boot.jaxb.SourceType;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

public class Main {

	public static void main(String[] args) {
		String test = "äöü@ÄÖÜ";
		String ä = "ä";
		byte[] byteÄ = ä.getBytes();
		System.out.println(ä);
		System.out.println(byteÄ);
		byte[] bytes = null;
		try {
			bytes = "AÄÖÜäöüßA".getBytes("ISO-8859-1");
			System.out.println("Bytes: " + bytes);
			String bachToString = new String(bytes, "ISO-8859-1");
			System.out.println("Wieder text: " + bachToString);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String unreadable = null;
		try {
			 unreadable = new String(test.getBytes("UTF-8"), "ISO-8859-15");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println(test);
		System.out.println(unreadable);
		String readable = null;
		try {
			readable = new String(unreadable.getBytes("ISO-8859-15"),
					"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println(readable);
	}
}