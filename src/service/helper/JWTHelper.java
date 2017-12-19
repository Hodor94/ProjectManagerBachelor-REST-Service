package service.helper;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import service.RESTService;

import java.io.*;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Raphael on 19.12.2017.
 */
public class JWTHelper {

	public static String createUserToken(String userid, String username,
								   String userRole, String teamName) {
		long currentMilliseconds = System.currentTimeMillis();
		Date creationTime = new Date(currentMilliseconds);
		Date expireTime = new Date(currentMilliseconds
				+ RESTService.EXPIRE_TIME);
		String token = null;
		try {
			// Create HMAC signer
			JWSSigner signer = new MACSigner(RESTService.SHARED_SECRET);

			// Prepare JWT
			JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
					.audience("users")
					.subject("authentication") // Defines for what the token is used
					.jwtID(userid)    // The user id is the id for the token
					.issueTime(creationTime)    // The creation time of the token
					.expirationTime(expireTime)    // The time the token expires
					.claim("name", username) // Username
					.claim("role", userRole) // The role of the user
					.claim("team", teamName) // The team name of the user
					.build();

			// Combine header and claims
			SignedJWT signedJWT
					= new SignedJWT(new JWSHeader(JWSAlgorithm.HS256),
					claimsSet);

			// Sign the token
			signedJWT.sign(signer);

			// Finish token
			token = signedJWT.serialize();
		} catch (KeyLengthException e) {
			token = null;
		} catch (JOSEException e) {
			token = null;
		}
		return token;
	}

	public static boolean validateToken(String token) {
		boolean result = false;
		if (token != null) {
			try {
				JWSVerifier verifier
						= new MACVerifier(RESTService.SHARED_SECRET);
				SignedJWT jwt = SignedJWT.parse(token);
				// Verify token
				if (jwt.verify(verifier)) {
					Calendar expirationTime = Calendar.getInstance();
					expirationTime.setTime(jwt.getJWTClaimsSet()
							.getExpirationTime());
					Calendar currentTime = Calendar.getInstance();
					int compareIndex = currentTime.compareTo(expirationTime);
					if (!(compareIndex > 0)) {
						result = true;
					}
				}
				result = jwt.verify(verifier);
			} catch (JOSEException e) {
				// Do nothing
			} catch (ParseException e) {
				// Do nothing
			}
		}
		return result;
	}

}
