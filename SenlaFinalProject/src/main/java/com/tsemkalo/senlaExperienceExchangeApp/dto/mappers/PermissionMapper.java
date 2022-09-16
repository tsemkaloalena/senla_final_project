package com.tsemkalo.senlaExperienceExchangeApp.dto.mappers;

import com.tsemkalo.senlaExperienceExchangeApp.dao.RoleDao;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Permission;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Role;
import com.tsemkalo.senlaExperienceExchangeApp.dto.PermissionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper implements Mapper<Permission, PermissionDto> {
	@Autowired
	RoleDao roleDao;

	@Override
	public PermissionDto toDto(Permission permission) {
		Role role = permission.getRole();
		Long roleId = null;
		if (role != null) {
			roleId = role.getId();
		}
		return new PermissionDto(permission.getId(), permission.getName(), roleId);
	}

	@Override
	public Permission toEntity(PermissionDto dto) {
		Role role = null;
		if (dto.getRoleId() != null) {
			role = roleDao.getById(dto.getRoleId());
		}
		Permission permission = new Permission(dto.getName(), role);
		permission.setId(dto.getId());
		return permission;
	}
}
