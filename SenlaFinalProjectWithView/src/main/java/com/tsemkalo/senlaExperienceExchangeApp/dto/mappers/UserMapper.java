package com.tsemkalo.senlaExperienceExchangeApp.dto.mappers;

import com.tsemkalo.senlaExperienceExchangeApp.dao.RoleDao;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Role;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.User;
import com.tsemkalo.senlaExperienceExchangeApp.dto.UserDto;
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
		return new UserDto(user.getId(), user.getUsername(), user.getPassword(), user.getName(), user.getSurname(), roleId, user.getEmail());
	}

	@Override
	public User toEntity(UserDto dto) {
		Role role = null;
		if (dto.getRoleId() != null) {
			role = roleDao.getById(dto.getRoleId());
		}
		User user = new User(dto.getUsername(), dto.getPassword(), dto.getName(), dto.getSurname(), role, dto.getEmail());
		user.setId(dto.getId());
		return user;
	}
}
