package service;

import dao.UserDAO;
import entity.UserEntity;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.util.Properties;

/**
 * A service for the user to set a random pin for password due to the fact
 * that the original password has been forgotten or lost.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since version 1.0
 */
public class PasswordService {
	// The username of the used support email address.
	private final String USERNAME = "pwserviceprojectmanager";
	// The password of the used support email address.
	private final String PASSWORD = "OoHeedei1eiw8gaa";
	// The subject of the email.
	private final String SUBJECT = "Ihre temporäre PIN";
	private String recipient;
	// The user who wants to change his or her password.
	private UserEntity userToChangePassword;

	/**
	 * Creates a new instance of PasswordService with the user who wants to
	 * change the password set as a parameter value.
	 *
	 * @param userToChangePassword The user who wants to change his or her
	 *                                password.
	 */
	public PasswordService(UserEntity userToChangePassword) {
		this.userToChangePassword = userToChangePassword;
		recipient = this.userToChangePassword.getEmail();
	}

	/**
	 * Creates a random secure PIN for the user who wants to change the
	 * password. Sends the PIN to the email address of the user.
	 */
	public void sendFromGmail() {
		Properties properties = System.getProperties();
		// Ste up all meta data needed for sending an email.
		String host = "smtp.gmail.com";
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.user", USERNAME);
		properties.put("mail.smtp.password", PASSWORD);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.port", "587");

		Session session = Session.getDefaultInstance(properties);

		// Create a message for the email.
		MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(USERNAME));
			InternetAddress toAddress = new InternetAddress(recipient);
			message.addRecipient(Message.RecipientType.TO, toAddress);
			message.setSubject(SUBJECT);
			String newPassword = generatePINAndSetPassword();
			String messageContent = "Ihr altes Passwort hat die Gültigkeit " +
					"verloren.<br> Ihr neues Passwort ist nun: <br> " +
					"<center><b>" + newPassword + "</b></center>";
			message.setContent(messageContent, "text/html; charset=	UTF-8");
			Transport transport = session.getTransport();
			transport.connect(host, USERNAME, PASSWORD);
			// Send the email.
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	/*
	Generates a secure random PIN with a length of 6 digits.
	 */
	private String generatePINAndSetPassword() {
		SecureRandom random = new SecureRandom();
		int num = random.nextInt(100000);
		String pin = String.format("%06d", num);
		userToChangePassword.setPassword(pin);
		UserDAO userDAO = new UserDAO();
		userDAO.saveOrUpdate(userToChangePassword);
		return pin;
	}

}