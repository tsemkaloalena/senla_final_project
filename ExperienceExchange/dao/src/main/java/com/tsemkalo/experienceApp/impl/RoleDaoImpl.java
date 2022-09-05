package com.tsemkalo.experienceApp.impl;

import com.tsemkalo.experienceApp.RoleDao;
import com.tsemkalo.experienceApp.entities.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleDaoImpl extends AbstractDaoImpl<Role> implements RoleDao {
	@Override
	public Class<Role> getEntityClass() {
		return Role.class;
	}
}
