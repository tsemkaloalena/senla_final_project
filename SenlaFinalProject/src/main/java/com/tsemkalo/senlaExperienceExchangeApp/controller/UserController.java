package com.tsemkalo.senlaExperienceExchangeApp.controller;

import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.User;
import com.tsemkalo.senlaExperienceExchangeApp.dto.PersonalAccountDto;
import com.tsemkalo.senlaExperienceExchangeApp.dto.UserDto;
import com.tsemkalo.senlaExperienceExchangeApp.dto.mappers.PersonalAccountMapper;
import com.tsemkalo.senlaExperienceExchangeApp.dto.mappers.UserMapper;
import com.tsemkalo.senlaExperienceExchangeApp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private PersonalAccountMapper personalAccountMapper;

	/**
	 * @param userDto user data
	 * @return signed up and added to database user
	 */
	@PostMapping("/sign_up")
	public PersonalAccountDto saveDto(@RequestBody UserDto userDto) {
		User user = userMapper.toEntity(userDto);
		return personalAccountMapper.toDto(userService.saveUser(user));
	}

	/**
	 * @param userDto current password and new password
	 * @return message about successful password change
	 */
	@PostMapping("/change_password")
	public ResponseEntity<Object> changePassword(@RequestBody UserDto userDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		userService.changePassword(authentication.getName(), userDto.getOldPassword(), userDto.getPassword());
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("message",  "Your password is successfully changed.");
		return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
	}

	/**
	 * @param userDto current password and new username
	 * @return new token
	 */
	@PostMapping("/change_username")
	public ResponseEntity<Object> changeUsername(@RequestBody UserDto userDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("message",  userService.changeUsername(authentication.getName(), userDto.getUsername(), userDto.getPassword()));
		return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
	}

	/**
	 * @param userDto user data that should be edited (email, name or surname)
	 * @return edited user data
	 */
	@PostMapping("/edit")
	public PersonalAccountDto editInfo(@RequestBody UserDto userDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.getById(userService.editInfo(authentication.getName(), userMapper.toEntity(userDto)));
		return personalAccountMapper.toDto(user);
	}

	/**
	 * @return personal account info (user data)
	 */
	@GetMapping
	public PersonalAccountDto getPersonalInfo() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) userService.loadUserByUsername(authentication.getName());
		return personalAccountMapper.toDto(user);
	}

	@PostMapping("/forgot_password")
	public ResponseEntity<Object> sendForgotPasswordEmail(@RequestParam String username) {
		userService.sendForgotPasswordEmail(username);
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("message",  "Email for resetting your password is sent. Follow the instructions and don't lose your new password :)");
		return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
	}

	@PostMapping("/reset_password/{resetPasswordToken}")
	public ResponseEntity<Object> resetPassword(@PathVariable String resetPasswordToken, @RequestParam String newPassword) {
		userService.resetPassword(resetPasswordToken, newPassword);
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("message",  "Your password is successfully changed.");
		return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
	}
}
