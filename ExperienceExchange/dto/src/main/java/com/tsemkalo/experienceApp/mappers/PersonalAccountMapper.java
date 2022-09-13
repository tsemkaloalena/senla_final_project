package com.tsemkalo.experienceApp.mappers;

import com.tsemkalo.experienceApp.PersonalAccountDto;
import com.tsemkalo.experienceApp.entities.Role;
import com.tsemkalo.experienceApp.entities.User;
import org.springframework.stereotype.Component;

@Component
public class PersonalAccountMapper implements Mapper<User, PersonalAccountDto> {
	@Override
	public PersonalAccountDto toDto(User user) {
		Role role = user.getRole();
		String roleName = "unknown role";
		if (role != null) {
			roleName = role.getName().name().toLowerCase();
		}
		return new PersonalAccountDto(user.getId(), user.getUsername(), user.getName(), user.getSurname(), roleName, user.getEmail());
	}

	@Override
	public User toEntity(PersonalAccountDto dto) {
		return null;
	}
}
