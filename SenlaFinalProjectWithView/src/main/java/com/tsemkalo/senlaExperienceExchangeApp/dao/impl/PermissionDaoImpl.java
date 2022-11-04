package com.tsemkalo.senlaExperienceExchangeApp.dao.impl;

import com.tsemkalo.senlaExperienceExchangeApp.dao.PermissionDao;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Permission;
import org.springframework.stereotype.Component;

@Component
public class PermissionDaoImpl extends AbstractDaoImpl<Permission> implements PermissionDao {
	@Override
	public Class<Permission> getEntityClass() {
		return Permission.class;
	}
}
