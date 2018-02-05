package service.helper;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import service.RESTService;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * This class is used to create and validate authentication tokens for the
 * users in the system.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since version 1.0
 */
public class JWTHelper {

	/**
	 * Creates a JSON Web Token with username, user role and team of the user
	 * saved in it. It expires after 15 minutes after creation and is
	 * encrypted by HS256 algorithm.
	 *
	 * @param userid The id of the user will be the tokens id.
	 * @param username The username saved in the token.
	 * @param userRole The role of the user saved in the token.
	 * @param teamName The team name of the users team saved in the token.
	 *
	 * @return A encrypted JWT String.
	 */
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
					.subject("authentication") // Defines for what the token
										       // is used
					.jwtID(userid)    // The user id is the id for the token
					.issueTime(creationTime)   // The creation time of the token
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

	/**
	 * Checks if the signature of the given token is valid and if the token
	 * did not expire already.
	 *
	 * @param token The JWT to validate.
	 *
	 * @return Returns true if the token is valid and did not expire and
	 * false if it is not valid or already expired.
	 */
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
