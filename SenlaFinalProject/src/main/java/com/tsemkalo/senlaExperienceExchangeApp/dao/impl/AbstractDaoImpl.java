package com.tsemkalo.senlaExperienceExchangeApp.dao.impl;

import com.tsemkalo.senlaExperienceExchangeApp.dao.AbstractDao;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.AbstractEntity;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.NotFoundException;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Transactional
public abstract class AbstractDaoImpl<T extends AbstractEntity> implements AbstractDao<T> {
	@PersistenceContext
	@Getter(AccessLevel.PROTECTED)
	private EntityManager entityManager;

	public abstract Class<T> getEntityClass();

	public T getById(Long id) {
		T entity = entityManager.find(getEntityClass(), id);
		if (entity == null) {
			throw new NotFoundException(getEntityClass().getSimpleName() + " with id " + id);
		}
		return entity;
	}

	public List<T> getAll() {
		Class<T> entityClass = getEntityClass();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
		Root<T> rootEntry = criteriaQuery.from(entityClass);
		CriteriaQuery<T> all = criteriaQuery.select(rootEntry);
		return entityManager.createQuery(all).getResultList();
	}

	public void create(T entity) {
		entityManager.persist(entity);
	}

	public void updateOrCreateWithId(T entity) {
		entityManager.merge(entity);
	}

	public void deleteById(Long id) {
		T entity = entityManager.find(getEntityClass(), id);
		if (entity == null) {
			throw new NotFoundException(getEntityClass().getSimpleName() + " with id " + id);
		}
		entityManager.remove(entity);
	}
}
