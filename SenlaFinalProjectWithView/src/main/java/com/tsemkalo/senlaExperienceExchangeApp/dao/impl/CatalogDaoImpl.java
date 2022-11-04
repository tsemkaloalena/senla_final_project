package com.tsemkalo.senlaExperienceExchangeApp.dao.impl;

import com.tsemkalo.senlaExperienceExchangeApp.dao.CatalogDao;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.AbstractEntity;
import com.tsemkalo.senlaExperienceExchangeApp.enums.LessonStatus;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public abstract class CatalogDaoImpl<T extends AbstractEntity> extends AbstractDaoImpl<T> implements CatalogDao<T> {
	private final Class<T> entityClass = getEntityClass();

	@Override
	public Predicate getTeacherPredicate(String teacherName, String teacherSurname) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
		Root<T> rootEntry = criteriaQuery.from(entityClass);
		rootEntry.alias("alias1");
		Predicate namePredicate = criteriaBuilder.equal(
				rootEntry.get("teacher").get("name"), teacherName
		);
		Predicate surnamePredicate = criteriaBuilder.equal(
				rootEntry.get("teacher").get("surname"), teacherSurname
		);
		return criteriaBuilder.and(namePredicate, surnamePredicate);
	}

	@Override
	public Predicate getThemePredicate(String theme) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
		Root<T> rootEntry = criteriaQuery.from(entityClass);
		rootEntry.alias("alias1");
		return criteriaBuilder.equal(rootEntry.get("theme"), theme);
	}

	@Override
	public Predicate getRatingPredicate(Double minRating) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
		Root<T> rootEntry = criteriaQuery.from(entityClass);
		rootEntry.alias("alias1");
		return criteriaBuilder.greaterThanOrEqualTo(rootEntry.get("averageRating"), minRating);
	}

	@Override
	public Predicate getOnlineOfflinePredicate(Boolean online, String address) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
		Root<T> rootEntry = criteriaQuery.from(entityClass);
		rootEntry.alias("alias1");
		if (online) {
			return criteriaBuilder.isNull(rootEntry.get("address"));
		} else if (address != null) {
			return criteriaBuilder.equal(rootEntry.get("address"), address);
		}
		return criteriaBuilder.isNotNull(rootEntry.get("address"));
	}

	@Override
	public Predicate getCostPredicate(Boolean free, Integer minCost, Integer maxCost) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
		Root<T> rootEntry = criteriaQuery.from(entityClass);
		rootEntry.alias("alias1");
		if (free != null && free) {
			return criteriaBuilder.isNull(rootEntry.get("cost"));
		}
		Predicate minCostPredicate = null;
		Predicate maxCostPredicate = null;
		if (minCost != null) {
			minCostPredicate = criteriaBuilder.greaterThanOrEqualTo(rootEntry.get("cost"), minCost);
		}
		if (maxCost != null) {
			maxCostPredicate = criteriaBuilder.lessThanOrEqualTo(rootEntry.get("cost"), maxCost);
		}
		if (minCostPredicate != null && maxCostPredicate != null) {
			return criteriaBuilder.and(minCostPredicate, maxCostPredicate);
		}
		if (minCostPredicate != null) {
			return minCostPredicate;
		}
		if (maxCostPredicate != null) {
			return maxCostPredicate;
		}
		return criteriaBuilder.isNotNull(rootEntry.get("cost"));
	}

	@Override
	public Predicate getIndividualPredicate(Boolean individual) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
		Root<T> rootEntry = criteriaQuery.from(entityClass);
		rootEntry.alias("alias1");
		return criteriaBuilder.equal(rootEntry.get("individual"), individual);
	}

	@Override
	public Predicate getStatusPredicate(LessonStatus lessonStatus) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
		Root<T> rootEntry = criteriaQuery.from(entityClass);
		rootEntry.alias("alias1");
		if (lessonStatus == null) {
			Predicate inProgressPredicate = criteriaBuilder.equal(
					rootEntry.get("status"), LessonStatus.IN_PROGRESS
			);
			Predicate notStartedPredicate = criteriaBuilder.equal(
					rootEntry.get("status"), LessonStatus.NOT_STARTED
			);
			return criteriaBuilder.or(inProgressPredicate, notStartedPredicate);
		}
		return criteriaBuilder.equal(rootEntry.get("status"), lessonStatus);
	}

	@Override
	public Predicate getFreePlacesLeftPredicate(Integer numberOfPlaces) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
		Root<T> rootEntry = criteriaQuery.from(entityClass);
		rootEntry.alias("alias1");
		Predicate minimalPredicate = criteriaBuilder.greaterThanOrEqualTo(
				rootEntry.get("freePlacesLeft"), numberOfPlaces
		);
		Predicate infinityPredicate = criteriaBuilder.isNull(rootEntry.get("freePlacesLeft"));
		return criteriaBuilder.or(minimalPredicate, infinityPredicate);
	}
}
