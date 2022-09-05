package com.tsemkalo.experienceApp.impl;

import com.tsemkalo.experienceApp.CourseDao;
import com.tsemkalo.experienceApp.entities.Course;
import com.tsemkalo.experienceApp.entities.Lesson;
import com.tsemkalo.experienceApp.exceptions.IncorrectDataException;
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
		CriteriaQuery<Course> lessons = criteriaQuery.select(rootEntry).where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
		return entityManager.createQuery(lessons).getResultList();
	}

	@Override
	public void updateCourseAccordingToLesson(Lesson lesson) {
		Course course = lesson.getCourse();
		course.setOnline();
		if (course.getAddress() == null) {
			course.setAddress(lesson.getAddress());
		}
		lesson.getCourse().countStartDate();
		lesson.getCourse().countFinishDate();
		course.setCost(course.getLessons().stream().map(Lesson::getCost).mapToInt(Integer::intValue).sum());
	}
}
