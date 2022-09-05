package com.tsemkalo.experienceApp.mappers;

import com.tsemkalo.experienceApp.RoleDao;
import com.tsemkalo.experienceApp.UserDto;
import com.tsemkalo.experienceApp.entities.Role;
import com.tsemkalo.experienceApp.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements Mapper<User, UserDto> {
	@Autowired
	private RoleDao roleDao;

	@Override
	public UserDto toDto(User user) {
		Role role = user.getRole();
		Long roleId = null;
		if (role != null) {
			roleId = role.getId();
		}
		return new UserDto(user.getId(), user.getUsername(), user.getPassword(), user.getName(), user.getSurname(), roleId);
	}

	@Override
	public User toEntity(UserDto dto) {
		Role role = null;
		if (dto.getRoleId() != null) {
			role = roleDao.getById(dto.getRoleId());
		}
		User user = new User(dto.getUsername(), dto.getPassword(), dto.getName(), dto.getSurname(), role);
		user.setId(dto.getId());
		return user;
	}
}
