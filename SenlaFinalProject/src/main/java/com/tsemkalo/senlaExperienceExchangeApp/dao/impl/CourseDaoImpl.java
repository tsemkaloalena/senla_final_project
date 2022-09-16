package com.tsemkalo.senlaExperienceExchangeApp.dao.impl;

import com.tsemkalo.senlaExperienceExchangeApp.dao.CourseDao;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Course;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Lesson;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Review;
import com.tsemkalo.senlaExperienceExchangeApp.enums.LessonStatus;
import com.tsemkalo.senlaExperienceExchangeApp.enums.OnlineType;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.IncorrectDataException;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;


@Component
public class CourseDaoImpl extends CatalogDaoImpl<Course> implements CourseDao {
	@Override
	public Class<Course> getEntityClass() {
		return Course.class;
	}

	@Override
	public Predicate getDatePredicate(LocalDateTime from, LocalDateTime to) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Course> criteriaQuery = criteriaBuilder.createQuery(Course.class);
		Root<Course> rootEntry = criteriaQuery.from(Course.class);
		rootEntry.alias("alias1");
		Predicate fromDatePredicate = null;
		Predicate toDatePredicate = null;
		if (from != null) {
			fromDatePredicate = criteriaBuilder.greaterThanOrEqualTo(rootEntry.get("startDate"), from);
		}
		if (to != null) {
			toDatePredicate = criteriaBuilder.lessThanOrEqualTo(rootEntry.get("finishDate"), to);
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
		throw new IncorrectDataException("Can't filter courses by date");
	}

	@Override
	public List<Course> getCourses(List<Predicate> predicates) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Course> criteriaQuery = criteriaBuilder.createQuery(Course.class);
		Root<Course> rootEntry = criteriaQuery.from(Course.class);
		rootEntry.alias("alias1");
		CriteriaQuery<Course> lessons = criteriaQuery.select(rootEntry)
				.where(
						criteriaBuilder.and(predicates.toArray(new Predicate[0])
						));
		return entityManager.createQuery(lessons).getResultList();
	}

	@Override
	public void updateCourseAccordingToLesson(Lesson lesson) {
		Course course = lesson.getCourse();
		updateOnlineType(course.getId());
		if (course.getAddress() == null) {
			course.setAddress(lesson.getAddress());
		}
		countStartDate(lesson.getCourse().getId());
		countFinishDate(lesson.getCourse().getId());
		course.setCost(course.getLessons().stream().map(Lesson::getCost).mapToInt(Integer::intValue).sum());
	}

	@Override
	public void updateOnlineType(Long courseId) {
		Course course = getById(courseId);
		Boolean onlineExists = course.getLessons().stream().anyMatch(lesson -> OnlineType.ONLINE.equals(lesson.getOnline()));
		Boolean offlineExists = course.getLessons().stream().anyMatch(lesson -> OnlineType.OFFLINE.equals(lesson.getOnline()));
		if (onlineExists && offlineExists) {
			course.setOnline(OnlineType.PARTIALLY_ONLINE);
		} else if (offlineExists) {
			course.setOnline(OnlineType.OFFLINE);
		} else {
			course.setOnline(OnlineType.ONLINE);
		}
	}

	@Override
	public void countAverageRating(Long courseId) {
		Course course = getById(courseId);
		if (course.getReviews().isEmpty()) {
			course.setAverageRating(null);
		} else {
			course.setAverageRating(course.getReviews().stream().map(Review::getRating)
					.mapToDouble(Integer::doubleValue).sum() / course.getReviews().size());
		}
	}

	@Override
	public void countCost(Long courseId) {
		Course course = getById(courseId);
		if (course.getLessons().isEmpty()) {
			course.setCost(null);
		} else {
			course.setCost(course.getLessons().stream()
					.filter(lesson -> !LessonStatus.DENIED.equals(lesson.getStatus()))
					.map(Lesson::getCost).mapToInt(Integer::intValue).sum());
		}
	}

	@Override
	public void countFinishDate(Long courseId) {
		Course course = getById(courseId);
		if (!course.getLessons().isEmpty()) {
			course.setFinishDate(course.getLessons().stream()
					.map(lesson -> lesson.getLessonDate().plusMinutes(lesson.getDurability()))
					.max(LocalDateTime::compareTo).get());
		}
	}

	@Override
	public void countStartDate(Long courseId) {
		Course course = getById(courseId);
		if (!course.getLessons().isEmpty()) {
			course.setStartDate(course.getLessons().stream()
					.map(Lesson::getLessonDate).min(LocalDateTime::compareTo).get());
		}
	}
}
