package com.tsemkalo.senlaExperienceExchangeApp.service.impl;

import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.AbstractEntity;
import com.tsemkalo.senlaExperienceExchangeApp.dao.impl.AbstractDaoImpl;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

public class CommonMethodsSetUpper<T extends AbstractEntity> {
	public void getAllSetup(Map<Long, T> hashMap, AbstractDaoImpl<T> dao) {
		lenient().when(dao.getAll()).thenReturn(new ArrayList<T>(hashMap.values()));
	}

	public void getByIdSetup(Map<Long, T> hashMap, AbstractDaoImpl<T> dao) {
		lenient().doAnswer(invocationOnMock -> {
			Long id = invocationOnMock.getArgument(0);
			if (hashMap.containsKey(id)) {
				return hashMap.get(id);
			}
			throw new NotFoundException("Entity with id " + id);
		}).when(dao).getById(any(Long.class));
	}

	public void createSetup(Map<Long, T> hashMap, AbstractDaoImpl<T> dao, Class<T> type) {
		lenient().doAnswer(invocationOnMock -> {
			T entity = invocationOnMock.getArgument(0);
			Long id = (long) hashMap.size() + 1;
			while (hashMap.containsKey(id)) {
				id++;
			}
			entity.setId(id);
			hashMap.put(id, entity);
			getAllSetup(hashMap, dao);
			getByIdSetup(hashMap, dao);
			return entity;
		}).when(dao).create(any(type));
	}

	public void deleteByIdSetup(Map<Long, T> hashMap, AbstractDaoImpl<T> dao) {
		lenient().doAnswer(invocationOnMock -> {
			Long id = invocationOnMock.getArgument(0);
			if (hashMap.containsKey(id)) {
				hashMap.remove(id);
				getAllSetup(hashMap, dao);
				getByIdSetup(hashMap, dao);
			} else {
				throw new NotFoundException("Entity with id " + id);
			}
			return null;
		}).when(dao).deleteById(any(Long.class));
	}
}
