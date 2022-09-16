package com.tsemkalo.senlaExperienceExchangeApp.service;

import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Course;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Lesson;
import com.tsemkalo.senlaExperienceExchangeApp.enums.LessonStatus;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.List;

public interface CourseService extends AbstractService<Course> {
	Predicate getTeacherPredicate(String teacherName, String teacherSurname);

	Predicate getThemePredicate(String theme);

	Predicate getRatingPredicate(Double minRating);

	Predicate getOnlineOfflinePredicate(Boolean online, String address);

	Predicate getCostPredicate(Boolean free, Integer minCost, Integer maxCost);

	Predicate getIndividualPredicate(Boolean individual);

	Predicate getStatusPredicate(LessonStatus lessonStatus);

	Predicate getDatePredicate(LocalDateTime from, LocalDateTime to);

	List<Course> getCourses(List<Predicate> predicates);

	Predicate getFreePlacesLeftPredicate(Integer numberOfPlaces);

	Course addCourse(String currentUsername, Course course);

	Course editCourse(String currentUsername, Course course);

	List<Long> denyCourse(String currentUsername, Long courseId);

	List<Long> deleteCourse(String currentUsername, Long courseId);

	Boolean subscribe(String currentUsername, Long courseId);

	Boolean unsubscribe(String currentUsername, Long courseId);

	List<Course> getSubscriptions(String currentUsername);

	Double countSalary(String currentUsername, Long courseId);

	void updateCourseStatuses(String currentUsername, Long courseId);

	void updateLessonStatusesInCourse(String currentUsername, LocalDateTime currentTime, Long courseId);

	List<Lesson> getCourseLessons(Long courseId);
}
