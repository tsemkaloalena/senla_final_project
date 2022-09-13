package com.tsemkalo.experienceApp.requestControllers;

import com.tsemkalo.experienceApp.PersonalAccountDto;
import com.tsemkalo.experienceApp.UserDto;
import com.tsemkalo.experienceApp.UserService;
import com.tsemkalo.experienceApp.entities.User;
import com.tsemkalo.experienceApp.mappers.PersonalAccountMapper;
import com.tsemkalo.experienceApp.mappers.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public String changePassword(@RequestBody UserDto userDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		userService.changePassword(authentication.getName(), userDto.getOldPassword(), userDto.getPassword());
		return "Your password is successfully changed.";
	}

	/**
	 * @param userDto current password and new username
	 * @return new token
	 */
	@PostMapping("/change_username")
	public String changeUsername(@RequestBody UserDto userDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return userService.changeUsername(authentication.getName(), userDto.getUsername(), userDto.getPassword());
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
	@GetMapping("/personal_account")
	public PersonalAccountDto getPersonalInfo() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) userService.loadUserByUsername(authentication.getName());
		return personalAccountMapper.toDto(user);
	}
}
