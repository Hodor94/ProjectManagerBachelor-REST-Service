import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

public class Main {

	public static void main(String[] args) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		String random = UUID.randomUUID().toString();
		System.out.println("random string: " + random);

		Key signingKeyRandom = new SecretKeySpec(DatatypeConverter
				.parseBase64Binary(random),
				signatureAlgorithm
				.getJcaName());

		//We will sign our JWT with our ApiKey secret
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary
				("secret");
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		//Let's set the JWT Claims
		JwtBuilder builderOne = Jwts.builder().setId("1")
				.setIssuedAt(now)
				.setSubject("users")
				.setIssuer("authentcation")
				.signWith(signatureAlgorithm, signingKey);

		JwtBuilder builderTwo = Jwts.builder().setId("2")
				.setIssuedAt(now)
				.setSubject("users")
				.setIssuer("authentcation")
				.signWith(signatureAlgorithm, signingKeyRandom);

		//if it has been specified, let's add the expiration
		long expTime = now.getTime() + 900000;
		builderOne.setExpiration(new Date(expTime));

		//Builds the JWT and serializes it to a compact, URL-safe string
		String tokenOne = builderOne.compact();
		System.out.println("TokenOne: " + tokenOne);
		System.out.println("Key encoded One: " + signingKey.getEncoded());
		System.out.println("DatatypeConverter One: " + DatatypeConverter
				.parseBase64Binary("secret"));

		String tokenTwo = builderTwo.compact();
		System.out.println("Token Two: " + tokenOne);
		System.out.println("Key encoded Two: " + signingKeyRandom.getEncoded());
		System.out.println("DatatypeConverter Two: " + DatatypeConverter
				.parseBase64Binary(random));

		Claims claimsOne = Jwts.parser()
				.setSigningKey(DatatypeConverter
						.parseBase64Binary("secret"))
				.parseClaimsJws(tokenOne)
				.getBody();
		System.out.println("ID: " + claimsOne.getId());
		System.out.println("Subject: " + claimsOne.getSubject());
		System.out.println("Issuer: " + claimsOne.getIssuer());
		System.out.println("exp time: " + claimsOne.getExpiration().toString());

		Claims claimsTwo = Jwts.parser()
				.setSigningKey(DatatypeConverter.parseBase64Binary(random))
				.parseClaimsJws(tokenTwo)
				.getBody();
		System.out.println("ID: " + claimsTwo.getId());
		System.out.println("Subject: " + claimsTwo.getSubject());
		System.out.println("Issuer: " + claimsTwo.getIssuer());
		System.out.println("exp time: " + claimsTwo.getExpiration().toString());
	}
	// TODO WATCH!!!
}