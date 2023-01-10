package com.example.tablenow.config.mail;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.example.tablenow.config.SecretProperties;

@Configuration
public class MailConfig {

	@Bean
	public JavaMailSender javaMailService() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost(SecretProperties.MAIL_HOST); // 네이버 메일 이용
		javaMailSender.setUsername(SecretProperties.MAIL_USERNAME); // 네이버 ID
		javaMailSender.setPassword(SecretProperties.MAIL_PASSWORD); // 네이버 PW
		javaMailSender.setPort(465); // SMTP 포트
		javaMailSender.setJavaMailProperties(getMailProperties());
		javaMailSender.setDefaultEncoding("UTF-8"); // 인코딩
		return javaMailSender;
	}

	private Properties getMailProperties() {
		Properties properties = new Properties();
		properties.setProperty("mail.transport.protocol", "smtp");
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.starttls.enable", "true");
		properties.setProperty("mail.debug", "true");
		properties.setProperty("mail.smtp.ssl.trust", "smtp.naver.com");
		properties.setProperty("mail.smtp.ssl.enable", "true");
		return properties;
	}
}
