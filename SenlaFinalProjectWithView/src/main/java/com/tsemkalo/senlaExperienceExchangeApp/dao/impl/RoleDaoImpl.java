package com.tsemkalo.senlaExperienceExchangeApp.dao.impl;

import com.tsemkalo.senlaExperienceExchangeApp.dao.RoleDao;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleDaoImpl extends AbstractDaoImpl<Role> implements RoleDao {
	@Override
	public Class<Role> getEntityClass() {
		return Role.class;
	}
}
