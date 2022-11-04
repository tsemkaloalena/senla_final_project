package com.tsemkalo.senlaExperienceExchangeApp.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.User;
import com.tsemkalo.senlaExperienceExchangeApp.dto.PersonalAccountDto;
import com.tsemkalo.senlaExperienceExchangeApp.dto.UserDto;
import com.tsemkalo.senlaExperienceExchangeApp.dto.mappers.PersonalAccountMapper;
import com.tsemkalo.senlaExperienceExchangeApp.dto.mappers.UserMapper;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.NotFoundException;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.UserExistsException;
import com.tsemkalo.senlaExperienceExchangeApp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.SecurityConstants.EXPIRATION_TIME;
import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.SecurityConstants.HEADER_STRING;
import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.SecurityConstants.SECRET;
import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.SecurityConstants.TOKEN_PREFIX;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private PersonalAccountMapper personalAccountMapper;

	@GetMapping("/login")
	public String showLoginForm(Model model) {
		UserDto userDto = new UserDto();
		model.addAttribute("user", userDto);
		return "login";
	}

	@GetMapping("/sign_up")
	public String showSignUpForm(Model model) {
		UserDto userDto = new UserDto();
		model.addAttribute("user", userDto);
		return "sign_up";
	}

	/**
	 * @param userDto user data
	 * @return signed up and added to database user
	 */
	@PostMapping("/sign_up")
	public String saveDto(@ModelAttribute UserDto userDto, Model model) {
		User user = userMapper.toEntity(userDto);
		model.addAttribute("user", personalAccountMapper.toDto(userService.saveUser(user)));
		userService.login(userDto.getUsername(), userDto.getPassword());
		return "redirect:/user";
	}

	/**
	 * @param userDto current password and new password
	 * @return message about successful password change
	 */
	@PutMapping("/change_password")
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
	@PutMapping("/change_username")
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
	@PutMapping("/edit")
	public PersonalAccountDto editInfo(@RequestBody UserDto userDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.getById(userService.editInfo(authentication.getName(), userMapper.toEntity(userDto)));
		return personalAccountMapper.toDto(user);
	}

	/**
	 * @return personal account info (user data)
	 */
	@GetMapping
	public String getPersonalInfo(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) userService.loadUserByUsername(authentication.getName());
		model.addAttribute("user", personalAccountMapper.toDto(user));
		return "account";
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
