import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.hibernate.boot.jaxb.SourceType;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

public class Main {

	public static void main(String[] args) {
		SecureRandom random = new SecureRandom();
		byte[] sharedSecret = new byte[32];
		random.nextBytes(sharedSecret);
		JWSSigner signer = null;
		SignedJWT verify = null;
		try {
			signer = new MACSigner(sharedSecret);
		} catch (KeyLengthException e) {
			e.printStackTrace();
		}
		JWTClaimsSet claims = new JWTClaimsSet.Builder()
				.subject("alice")
				.issuer("https://c2id.com")
				.expirationTime(new Date(new Date().getTime() + 60 * 1000))
				.build();

		SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256),
				claims);
		try {
			signedJWT.sign(signer);
		} catch (JOSEException e) {
			e.printStackTrace();
		}
		String token = signedJWT.serialize();
		System.out.println(token);

		try {
			verify = SignedJWT.parse(token);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		try {
			JWSVerifier verifier = new MACVerifier(sharedSecret);
			System.out.println(verify.verify(verifier));
		} catch (JOSEException e) {
			e.printStackTrace();
		}

	}
}