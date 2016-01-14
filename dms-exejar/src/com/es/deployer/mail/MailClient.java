package com.es.deployer.mail;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

public class MailClient {
	private static Logger logger = Logger.getLogger(MailClient.class);
	
	private static final String _MAIL_PROTOCOL_SMTP = "smtp";
	private static final String _MAIL_SERVER_ADDR = "mail.test.com";
	
	private MailMessage mailMessage;
	
	public MailClient(MailMessage mailMessage) {
		super();
		this.mailMessage = mailMessage;
	}

	public MailMessage getMailMessage() {
		return this.mailMessage;
	}

	public void sendMail(MailMessage mailMessage) throws AddressException, MessagingException {
		Properties prop = new Properties();
		prop.put("mail.transport.protocol", _MAIL_PROTOCOL_SMTP);
		prop.put("mail.smtp.host", _MAIL_SERVER_ADDR);
		
		Session mailSession = Session.getInstance(prop);
		Message message = new MimeMessage(mailSession);

		logger.debug("보내는사람 : " + mailMessage.getFrom());
		logger.debug("받는사람들 : " + mailMessage.getTo());
		logger.debug("제목 : " + mailMessage.getSubject());
		logger.debug("내용 : " + mailMessage.getText());
		
		message.setFrom(new InternetAddress(mailMessage.getFrom())); 
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailMessage.getTo())); 
		message.setSentDate(new Date());
		message.setSubject(mailMessage.getSubject()); // 제목
		message.setText(mailMessage.getText()); // 내용
		Transport.send(message);
	}
}
