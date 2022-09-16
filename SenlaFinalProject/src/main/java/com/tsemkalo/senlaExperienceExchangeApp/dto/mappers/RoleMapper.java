package com.tsemkalo.senlaExperienceExchangeApp.dto.mappers;

import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Role;
import com.tsemkalo.senlaExperienceExchangeApp.dto.RoleDto;
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
