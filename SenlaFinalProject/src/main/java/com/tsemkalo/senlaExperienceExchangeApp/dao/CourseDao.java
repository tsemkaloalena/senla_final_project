package com.tsemkalo.senlaExperienceExchangeApp.dao;

import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Course;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Lesson;

import javax.persistence.criteria.Predicate;
import java.util.List;

public interface CourseDao extends CatalogDao<Course> {
	List<Course> getCourses(List<Predicate> predicates);

	void updateCourseAccordingToLesson(Lesson lesson);

	void updateOnlineType(Long courseId);

	void countAverageRating(Long courseId);

	void countCost(Long courseId);

	void countFinishDate(Long courseId);

	void countStartDate(Long courseId);
}
