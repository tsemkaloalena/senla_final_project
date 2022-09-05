package com.tsemkalo.experienceApp;

import com.tsemkalo.experienceApp.entities.Course;
import com.tsemkalo.experienceApp.entities.Lesson;

import javax.persistence.criteria.Predicate;
import java.util.List;

public interface CourseDao extends CatalogDao<Course> {
	List<Course> getCourses(List<Predicate> predicates);

	void updateCourseAccordingToLesson(Lesson lesson);
}
