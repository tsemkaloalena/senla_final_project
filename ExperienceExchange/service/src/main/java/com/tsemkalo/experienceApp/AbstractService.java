package com.tsemkalo.experienceApp;

import com.tsemkalo.experienceApp.entities.AbstractEntity;

import java.util.List;

public interface AbstractService<T extends AbstractEntity> {

	T getById(Long id);

	List<T> getAll();

	void deleteById(Long id);

	void create(T entity);
}
