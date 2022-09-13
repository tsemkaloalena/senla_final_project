package com.tsemkalo.experienceApp;

import com.tsemkalo.experienceApp.entities.AbstractEntity;

import java.util.List;

public interface AbstractDao<T extends AbstractEntity> {
	T getById(Long id);

	List<T> getAll();

	void deleteById(Long id);

	void create(T entity);

	void updateOrCreateWithId(T entity);
}
