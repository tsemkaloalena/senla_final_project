package com.tsemkalo.senlaExperienceExchangeApp.service.impl;

import com.tsemkalo.senlaExperienceExchangeApp.dao.AbstractDao;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.AbstractEntity;
import com.tsemkalo.senlaExperienceExchangeApp.service.AbstractService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = Exception.class)
public abstract class AbstractServiceImpl<T extends AbstractEntity, D extends AbstractDao<T>> implements AbstractService<T> {
	@Autowired
	@Setter
	private D defaultDao;

	protected AbstractServiceImpl(D defaultDao) {
		this.defaultDao = defaultDao;
	}

	protected AbstractServiceImpl() {
		this.defaultDao = null;
	}

	@Override
	public T getById(Long id) {
		return defaultDao.getById(id);
	}

	@Override
	public List<T> getAll() {
		return defaultDao.getAll();
	}

	@Override
	public void deleteById(Long id) {
		defaultDao.deleteById(id);
	}

	@Override
	public void create(T entity) {
		defaultDao.create(entity);
	}
}
