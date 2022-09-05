package com.tsemkalo.experienceApp.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.tsemkalo.experienceApp.UserDao;
import com.tsemkalo.experienceApp.UserService;
import com.tsemkalo.experienceApp.entities.User;
import com.tsemkalo.experienceApp.exceptions.IncorrectDataException;
import com.tsemkalo.experienceApp.exceptions.UserExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.tsemkalo.experienceApp.SecurityConstants.EXPIRATION_TIME;
import static com.tsemkalo.experienceApp.SecurityConstants.SECRET;

@Component
public class UserServiceImpl extends AbstractServiceImpl<User, UserDao> implements UserService {
	@Autowired
	private UserDao userDao;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public User loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userDao.getUserByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException(username);
		}
		return user;
	}

	@Override
	public User saveUser(User user) {
		if (userDao.getUserByUsername(user.getUsername()) != null) {
			throw new UserExistsException(user.getUsername());
		}
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userDao.create(user);
		return user;
	}

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

	@Override
	public Long editInfo(String currentUsername, String name, String surname) {
		User user = loadUserByUsername(currentUsername);
		if (name == null && surname == null) {
			throw new IncorrectDataException("You didn't change any data");
		}
		if (name != null) {
			user.setName(name);
		}
		if (surname != null) {
			user.setSurname(surname);
		}
		return user.getId();
	}
}
