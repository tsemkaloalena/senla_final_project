package com.tsemkalo.senlaExperienceExchangeApp.dao;

import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Course;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Lesson;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Subscription;

import java.util.List;

public interface SubscriptionDao extends AbstractDao<Subscription> {
	Subscription getSubscription(Long userId, Long lessonId);

	List<Lesson> getSubscriptionLessons(Long userId);

	List<Course> getSubscriptionCourses(Long userId);
}
