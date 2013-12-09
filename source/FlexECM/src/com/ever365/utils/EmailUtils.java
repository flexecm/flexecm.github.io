package com.ever365.utils;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

/**
 * 
 * @author LiuHan
 *
 */
public class EmailUtils {
	
	public static int sendEmail(String host, String port, String user, String password, String from, 
			String subject, String msg,  String to) {
		
		Email email = new SimpleEmail();
		email.setHostName(host);
		email.setSmtpPort(Integer.valueOf(port));
		email.setAuthentication(user, password);
		email.setSSLOnConnect(false);
		try {
			email.setFrom(user);
			email.setSubject(subject);
			email.setMsg(msg);
			email.addTo(to);
			email.send();
		} catch (EmailException e) {
			throw new RuntimeException();
		}
		
		return 1;
	}

	public static boolean check(String smtp, String smtpport, String email,
			String smtppass) {
		
		return true;
	}
	
	public static void main(String[] args) {
		try {
			EmailUtils.sendEmail("smtp.126.com", "25", "liuhann@126.com", "overlord1232",  "liuhann@126.com",
					"hi", "this is a test", "liuhan@ever365.com");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
