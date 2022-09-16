package com.tsemkalo.senlaExperienceExchangeApp.service;

import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Lesson;
import com.tsemkalo.senlaExperienceExchangeApp.enums.LessonStatus;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface LessonService extends AbstractService<Lesson> {
	Map<String, Set<String>> getThemes();

	Predicate getTeacherPredicate(String teacherName, String teacherSurname);

	Predicate getRatingPredicate(Double minRating);

	Predicate getThemePredicate(String theme);

	Predicate getOnlineOfflinePredicate(Boolean online, String address);

	Predicate getCostPredicate(Boolean free, Integer minCost, Integer maxCost);

	Predicate getIndividualPredicate(Boolean individual);

	Predicate getStatusPredicate(LessonStatus lessonStatus);

	Predicate getDatePredicate(LocalDateTime from, LocalDateTime to);

	Predicate getFreePlacesLeftPredicate(Integer numberOfPlaces);

	Predicate getSingleLessonsPredicate();

	List<Lesson> getLessons(List<Predicate> predicates);

	Lesson addLesson(String currentUsername, Lesson lesson);

	Lesson editLesson(String currentUsername, Lesson lesson);

	void denyLesson(String currentUsername, Long lessonId);

	void deleteLesson(String currentUsername, Long lessonId);

	Boolean subscribe(String currentUsername, Long lessonId);

	Boolean unsubscribe(String currentUsername, Long lessonId);

	List<Lesson> getSubscriptions(String currentUsername);

	List<Lesson> getSchedule(String currentUsername);

	Double countSalary(String currentUsername, Long lessonId);

	void updateLessonStatus(String currentUsername, LocalDateTime currentTime, Long lessonId);
}
