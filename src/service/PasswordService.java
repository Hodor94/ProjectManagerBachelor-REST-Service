package service;

import dao.UserDAO;
import entity.UserEntity;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.util.Properties;

public class PasswordService {
	private final String USERNAME = "pwserviceprojectmanager";
	private final String PASSWORD = "OoHeedei1eiw8gaa";
	private final String SUBJECT = "Ihre temporäre PIN";
	private String recipient;
	private UserEntity userToChangePassword;

	public PasswordService(UserEntity userToChangePassword) {
		this.userToChangePassword = userToChangePassword;
		recipient = this.userToChangePassword.getEmail();
	}

	public void sendFromGmail() {
		Properties properties = System.getProperties();
		String host = "smtp.gmail.com";
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.user", USERNAME);
		properties.put("mail.smtp.password", PASSWORD);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.port", "587");

		Session session = Session.getDefaultInstance(properties);

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
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

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