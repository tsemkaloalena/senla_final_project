package com.tsemkalo.experienceApp.impl;

import com.tsemkalo.experienceApp.LessonDao;
import com.tsemkalo.experienceApp.entities.Lesson;
import com.tsemkalo.experienceApp.exceptions.IncorrectDataException;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class LessonDaoImpl extends CatalogDaoImpl<Lesson> implements LessonDao {
	@Override
	public Class<Lesson> getEntityClass() {
		return Lesson.class;
	}

	@Override
	public Map<String, Set<String>> getThemes() {
		Map<String, Set<String>> themes = new HashMap<>();
		for (Lesson lesson : getAll()) {
			String theme = lesson.getTheme();
			String subject = lesson.getSubject();
			if (!themes.containsKey(theme)) {
				themes.put(theme, new HashSet<>());
			}
			themes.get(theme).add(subject);
		}
		return themes;
	}

	@Override
	public Predicate getDatePredicate(LocalDateTime from, LocalDateTime to) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Lesson> criteriaQuery = criteriaBuilder.createQuery(Lesson.class);
		Root<Lesson> rootEntry = criteriaQuery.from(Lesson.class);
		rootEntry.alias("alias1");
		Predicate fromDatePredicate = null;
		Predicate toDatePredicate = null;
		if (from != null) {
			fromDatePredicate = criteriaBuilder.greaterThanOrEqualTo(rootEntry.get("lessonDate"), from);
		}
		if (to != null) {
			toDatePredicate = criteriaBuilder.lessThanOrEqualTo(rootEntry.get("lessonDate"), to);
		}
		if (fromDatePredicate != null && toDatePredicate != null) {
			return criteriaBuilder.and(fromDatePredicate, toDatePredicate);
		}
		if (fromDatePredicate != null) {
			return fromDatePredicate;
		}
		if (toDatePredicate != null) {
			return toDatePredicate;
		}
		throw new IncorrectDataException("Can't filter lessons by date");
	}

	@Override
	public Predicate getSingleLessonsPredicate() {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Lesson> criteriaQuery = criteriaBuilder.createQuery(Lesson.class);
		Root<Lesson> rootEntry = criteriaQuery.from(Lesson.class);
		rootEntry.alias("alias1");
		return criteriaBuilder.isNull(rootEntry.get("course"));
	}

	@Override
	public List<Lesson> getLessons(List<Predicate> predicates) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Lesson> criteriaQuery = criteriaBuilder.createQuery(Lesson.class);
		Root<Lesson> rootEntry = criteriaQuery.from(Lesson.class);
		rootEntry.alias("alias1");
		CriteriaQuery<Lesson> lessons = criteriaQuery.select(rootEntry).where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
		return entityManager.createQuery(lessons).getResultList();
	}
}
