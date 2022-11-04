package com.tsemkalo.senlaExperienceExchangeApp.dao;

import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Lesson;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface LessonDao extends CatalogDao<Lesson> {
	Map<String, Set<String>> getThemes();

	Predicate getSingleLessonsPredicate();

	List<Lesson> getLessons(List<Predicate> predicates);

	void updateLessonStatus(Long lessonId, LocalDateTime currentTime);

	void countAverageRating(Long lessonId);

	Double getSalaryForLesson(Long lessonId);

	Double getSalaryForCourse(List<Long> lessonIds);
}
