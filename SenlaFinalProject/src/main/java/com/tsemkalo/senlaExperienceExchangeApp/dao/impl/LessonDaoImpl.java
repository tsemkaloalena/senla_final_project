package com.tsemkalo.senlaExperienceExchangeApp.dao.impl;

import com.tsemkalo.senlaExperienceExchangeApp.dao.LessonDao;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Lesson;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Review;
import com.tsemkalo.senlaExperienceExchangeApp.enums.LessonStatus;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.IncorrectDataException;
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
		CriteriaQuery<Lesson> lessons = criteriaQuery.select(rootEntry)
				.where(criteriaBuilder
						.and(predicates.toArray(new Predicate[0])
						));
		return entityManager.createQuery(lessons).getResultList();
	}

	@Override
	public void updateLessonStatus(Long lessonId, LocalDateTime currentTime) {
		Lesson lesson = this.getById(lessonId);
		if (LessonStatus.DENIED.equals(lesson.getStatus())) {
			return;
		}
		if (currentTime.equals(lesson.getLessonDate().plusMinutes(lesson.getDurability())) ||
				currentTime.isAfter(lesson.getLessonDate().plusMinutes(lesson.getDurability()))) {
			lesson.setStatus(LessonStatus.FINISHED);
		} else if (lesson.getStatus().equals(LessonStatus.NOT_STARTED)) {
			if (lesson.getLessonDate().equals(currentTime) || currentTime.isAfter(lesson.getLessonDate())) {
				lesson.setStatus(LessonStatus.IN_PROGRESS);
			}
		} else if (lesson.getLessonDate().isAfter(currentTime)) {
			lesson.setStatus(LessonStatus.NOT_STARTED);
		}
	}

	@Override
	public void countAverageRating(Long lessonId) {
		Lesson lesson = getById(lessonId);
		if (lesson.getReviews().isEmpty()) {
			lesson.setAverageRating(null);
		} else {
			lesson.setAverageRating(lesson.getReviews().stream().map(Review::getRating)
					.mapToDouble(Integer::doubleValue).sum() / lesson.getReviews().size());
		}
	}

	@Override
	public Double getSalaryForLesson(Long lessonId) {
		Lesson lesson = getById(lessonId);
		int income = lesson.getSubscriptions().size() * lesson.getCost();
		if (income == 0) {
			return null;
		}
		if (lesson.getUnfixedSalary() != null) {
			return (double) (income * lesson.getUnfixedSalary()) / 100;
		}
		if (lesson.getFixedSalary() != null && income > lesson.getFixedSalary()) {
			return (double) lesson.getFixedSalary();
		}
		return null;
	}

	@Override
	public Double getSalaryForCourse(List<Long> lessonIds) {
		if (lessonIds.isEmpty() || getSalaryForLesson(getById(lessonIds.get(0)).getId()) == null) {
			return null;
		}
		return lessonIds.stream().map(this::getSalaryForLesson).mapToDouble(Double::doubleValue).sum();
	}
}
