package com.tsemkalo.experienceApp.requestControllers;

import com.tsemkalo.experienceApp.UserDto;
import com.tsemkalo.experienceApp.UserService;
import com.tsemkalo.experienceApp.entities.User;
import com.tsemkalo.experienceApp.mappers.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

	@PostMapping("/sign_up")
	public UserDto saveDto(@RequestBody UserDto userDto) {
		User user = userMapper.toEntity(userDto);
		return userMapper.toDto(userService.saveUser(user));
	}

	@PostMapping("/change_password")
	public String changePassword(@RequestBody UserDto userDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		userService.changePassword(authentication.getName(), userDto.getOldPassword(), userDto.getPassword());
		return "Your password is successfully changed.";
	}

	@PostMapping("/change_username")
	public String changeUsername(@RequestBody UserDto userDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return userService.changeUsername(authentication.getName(), userDto.getUsername(), userDto.getPassword());
	}

	@PostMapping("/edit")
	public String editInfo(@RequestBody UserDto userDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.getById(userService.editInfo(authentication.getName(), userDto.getName(), userDto.getSurname()));
		return "Your new name is " + user.getName() + " " + user.getSurname();
	}

	// TODO show personal info
}
