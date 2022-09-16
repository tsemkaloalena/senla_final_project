package com.tsemkalo.senlaExperienceExchangeApp.dao;

import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.AbstractEntity;
import com.tsemkalo.senlaExperienceExchangeApp.enums.LessonStatus;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;

public interface CatalogDao<T extends AbstractEntity> extends AbstractDao<T> {
	Predicate getTeacherPredicate(String teacherName, String teacherSurname);

	Predicate getRatingPredicate(Double minRating);

	Predicate getThemePredicate(String theme);

	Predicate getOnlineOfflinePredicate(Boolean online, String address);

	Predicate getCostPredicate(Boolean free, Integer minCost, Integer maxCost);

	Predicate getIndividualPredicate(Boolean individual);

	Predicate getStatusPredicate(LessonStatus lessonStatus);

	Predicate getDatePredicate(LocalDateTime from, LocalDateTime to);

	Predicate getFreePlacesLeftPredicate(Integer numberOfPlaces);
}
