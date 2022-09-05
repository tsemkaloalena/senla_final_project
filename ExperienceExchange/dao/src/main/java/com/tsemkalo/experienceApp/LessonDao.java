package com.tsemkalo.experienceApp;

import com.tsemkalo.experienceApp.entities.Lesson;

import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface LessonDao extends CatalogDao<Lesson> {
	Map<String, Set<String>> getThemes();

	Predicate getSingleLessonsPredicate();

	List<Lesson> getLessons(List<Predicate> predicates);
}
