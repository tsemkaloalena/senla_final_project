package com.tsemkalo.experienceApp;

import com.tsemkalo.experienceApp.entities.Course;
import com.tsemkalo.experienceApp.entities.Lesson;
import com.tsemkalo.experienceApp.entities.Review;
import com.tsemkalo.experienceApp.enums.LessonStatus;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.List;

public interface CourseDao extends CatalogDao<Course> {
	List<Course> getCourses(List<Predicate> predicates);

	void updateCourseAccordingToLesson(Lesson lesson);

	void updateOnlineType(Long courseId); // TODO check

	void countAverageRating(Long courseId); // TODO check

	void countCost(Long courseId); // TODO check

	void countFinishDate(Long courseId); // TODO check

	void countStartDate(Long courseId); // TODO check
}
