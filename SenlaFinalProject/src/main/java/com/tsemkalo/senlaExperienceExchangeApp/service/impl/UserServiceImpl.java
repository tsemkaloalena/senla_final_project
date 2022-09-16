package com.tsemkalo.senlaExperienceExchangeApp.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.tsemkalo.senlaExperienceExchangeApp.dao.UserDao;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.User;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.IncorrectDataException;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.MailSendingException;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.UserExistsException;
import com.tsemkalo.senlaExperienceExchangeApp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;

import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.SecurityConstants.EXPIRATION_TIME;
import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.SecurityConstants.SECRET;
import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.SecurityConstants.SECRET_FORGOT_PASSWORD;

@Slf4j
@Component
public class UserServiceImpl extends AbstractServiceImpl<User, UserDao> implements UserService {
	@Autowired
	private UserDao userDao;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private VelocityEngine velocityEngine;

	@Autowired
	private JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String emailFrom;

	/**
	 * @param username username of the searched user
	 * @return found user
	 * @throws UsernameNotFoundException if user is not found
	 */
	@Override
	public User loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userDao.getUserByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException(username);
		}
		return user;
	}

	/**
	 * @param user user entity
	 * @return added to database user entity
	 * @throws UserExistsException if user with given username already exists
	 */
	@Override
	public User saveUser(User user) {
		if (userDao.getUserByUsername(user.getUsername()) != null) {
			throw new UserExistsException(user.getUsername());
		}
		user.setEmail(user.getEmail()); // check is built into the setter
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userDao.create(user);
		return user;
	}

	/**
	 * @param currentUsername username of current user
	 * @param oldPassword     old password for checking with current
	 * @param newPassword     new password to which we should change the current one
	 */
	@Override
	public void changePassword(String currentUsername, String oldPassword, String newPassword) {
		if (oldPassword == null) {
			throw new IncorrectDataException("You didn't set old password");
		}
		if (newPassword == null) {
			throw new IncorrectDataException("You didn't set new password");
		}
		User user = loadUserByUsername(currentUsername);
		if (bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
			user.setPassword(bCryptPasswordEncoder.encode(newPassword));
			return;
		}
		throw new AccessDeniedException("Your old password is not correct.");
	}

	/**
	 * @param currentUsername username of current user
	 * @param newUsername     new username
	 * @param password        password for checking with current
	 * @return message with new generated token
	 */
	@Override
	public String changeUsername(String currentUsername, String newUsername, String password) {
		if (newUsername == null) {
			throw new IncorrectDataException("You didn't set new username");
		}
		if (password == null) {
			throw new IncorrectDataException("You didn't set your password");
		}
		User user = loadUserByUsername(currentUsername);
		User userWithGivenUserName = userDao.getUserByUsername(newUsername);
		if (userWithGivenUserName != null && !userWithGivenUserName.getId().equals(user.getId())) {
			throw new UserExistsException(newUsername);
		}
		if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
			user.setUsername(newUsername);
			String token = JWT.create()
					.withSubject(user.getUsername())
					.withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
					.sign(Algorithm.HMAC512(SECRET.getBytes()));

			return "Your username is changed successfully. Your new token: " + token;
		}
		throw new AccessDeniedException(user.getUsername() + "'s password is not correct.");
	}

	/**
	 * @param currentUsername username of current user
	 * @param editedUser      user entity with fields that should be changed
	 * @return id of the edited user
	 */
	@Override
	public Long editInfo(String currentUsername, User editedUser) {
		User user = loadUserByUsername(currentUsername);
		if (editedUser.getName() == null && editedUser.getSurname() == null) {
			throw new IncorrectDataException("You didn't change any data");
		}
		if (editedUser.getEmail() != null) {
			user.setEmail(editedUser.getEmail());
		}
		if (editedUser.getName() != null) {
			user.setName(editedUser.getName());
		}
		if (editedUser.getSurname() != null) {
			user.setSurname(editedUser.getSurname());
		}

		return user.getId();
	}

	/**
	 * Sending an email with generated link for resetting a password
	 * @param username username of user for which the password should be reset
	 */
	@Override
	public void sendForgotPasswordEmail(String username) {
		try {
			User user = loadUserByUsername(username);
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);
			message.setFrom(emailFrom);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
			message.setSubject("Reset password");

			String resetPasswordToken = JWT.create()
					.withSubject(username)
					.withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
					.sign(Algorithm.HMAC512(SECRET_FORGOT_PASSWORD.getBytes()));

			VelocityContext context = new VelocityContext();
			context.put("name", user.getName());
			context.put("surname", user.getSurname());
			context.put("resetPasswordToken", resetPasswordToken);
			StringWriter stringWriter = new StringWriter();
			velocityEngine.mergeTemplate("forgot_password.vm", "UTF-8", context, stringWriter);
			String text = stringWriter.toString();
			helper.setText(text, true);
			mailSender.send(message);
		} catch (Exception exception) {
			log.error(Arrays.toString(exception.getStackTrace()));
			throw new MailSendingException(exception.getMessage());
		}
	}

	/**
	 * @param resetPasswordToken token from email by which the username is taken
	 * @param newPassword new password
	 */
	@Override
	public void resetPassword(String resetPasswordToken, String newPassword) {
		if (newPassword == null) {
			throw new IncorrectDataException("You didn't set new password");
		}
		String username = JWT.require(Algorithm.HMAC512(SECRET_FORGOT_PASSWORD.getBytes()))
				.build()
				.verify(resetPasswordToken)
				.getSubject();
		User user = loadUserByUsername(username);
		user.setPassword(bCryptPasswordEncoder.encode(newPassword));
	}
}
