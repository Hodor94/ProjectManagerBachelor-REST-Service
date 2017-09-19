import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.io.*;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Date;

public class Main {

	public static void main(String[] args) {
		/*
		SecureRandom random = new SecureRandom();
		byte[] sharedSecret = new byte[32];
		byte[] read = new byte[32];
		random.nextBytes(sharedSecret);
		try {
			FileOutputStream outputStream
					= new FileOutputStream(new File("resources.txt"));
			org.apache.commons.io.IOUtils.write(sharedSecret, outputStream);
			outputStream.close();
			FileInputStream fis = new FileInputStream(new File("resources" +
					".txt"));
			fis.read(read);
			System.out.println("Read from file: " + read);
			System.out.println(read == null);
			JWSSigner testSigner = new MACSigner(read);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeyLengthException e) {
			e.printStackTrace();
		}

		//----------------------------------------------------------------------
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
		} */

		String testTeams =  "{yolo swag chill}";
		System.out.println(testTeams);
		String cuttedTestTeams = testTeams.substring(1, testTeams.length()-1);
		System.out.println(cuttedTestTeams);
		String[] noSpaces = cuttedTestTeams.split("\\s+");
		for (String text : noSpaces) {
			System.out.println(text);
		}

	}
}