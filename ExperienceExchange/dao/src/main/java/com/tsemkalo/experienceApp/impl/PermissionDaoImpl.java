package com.tsemkalo.experienceApp.impl;

import com.tsemkalo.experienceApp.PermissionDao;
import com.tsemkalo.experienceApp.entities.Permission;
import org.springframework.stereotype.Component;

@Component
public class PermissionDaoImpl extends AbstractDaoImpl<Permission> implements PermissionDao {
	@Override
	public Class<Permission> getEntityClass() {
		return Permission.class;
	}
}
