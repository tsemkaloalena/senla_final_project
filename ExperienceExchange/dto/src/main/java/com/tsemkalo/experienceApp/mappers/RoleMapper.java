package com.tsemkalo.experienceApp.mappers;

import com.tsemkalo.experienceApp.RoleDto;
import com.tsemkalo.experienceApp.entities.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper implements Mapper<Role, RoleDto> {
	@Override
	public RoleDto toDto(Role role) {
		return new RoleDto(role.getId(), role.getName());
	}

	@Override
	public Role toEntity(RoleDto dto) {
		Role role = new Role(dto.getName());
		role.setId(dto.getId());
		return role;
	}
}
