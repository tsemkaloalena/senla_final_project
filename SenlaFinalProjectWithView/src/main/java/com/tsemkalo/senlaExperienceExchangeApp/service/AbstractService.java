package com.tsemkalo.senlaExperienceExchangeApp.service;

import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.AbstractEntity;

import java.util.List;

public interface AbstractService<T extends AbstractEntity> {

	T getById(Long id);

	List<T> getAll();

	void deleteById(Long id);

	void create(T entity);
}
