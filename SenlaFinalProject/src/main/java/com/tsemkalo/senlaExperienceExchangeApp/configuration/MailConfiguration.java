package com.tsemkalo.senlaExperienceExchangeApp.configuration;

import com.tsemkalo.senlaExperienceExchangeApp.exceptions.MailSendingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Arrays;
import java.util.Properties;

@Slf4j
@Configuration
public class MailConfiguration {
	@Value("${spring.mail.host}")
	private String host;

	@Value("${spring.mail.username}")
	private String username;

	@Value("${spring.mail.password}")
	private String password;

	@Value("${spring.mail.port}")
	private int port;

	@Value("${spring.mail.protocol}")
	private String protocol;

	@Bean
	public JavaMailSender mailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(host);
		mailSender.setPort(port);
		mailSender.setUsername(username);
		mailSender.setPassword(password);

		Properties properties = mailSender.getJavaMailProperties();
		properties.setProperty("mail.transport.protocol", protocol);
		return mailSender;
	}

	@Bean
	public VelocityEngine velocityEngine() {
		Properties properties = new Properties();
		properties.setProperty("input.encoding", "UTF-8");
		properties.setProperty("output.encoding", "UTF-8");
		properties.setProperty("resource.loader", "class");
		properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		properties.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
		properties.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
		try {
			VelocityEngine velocityEngine = new VelocityEngine(properties);
			velocityEngine.init();
			return velocityEngine;
		} catch (Exception exception) {
			log.error(Arrays.toString(exception.getStackTrace()));
			throw new MailSendingException(exception.getMessage());
		}
	}
}
