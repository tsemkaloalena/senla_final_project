package com.tsemkalo.experienceApp;

import com.tsemkalo.experienceApp.entities.Course;
import com.tsemkalo.experienceApp.entities.Lesson;
import com.tsemkalo.experienceApp.entities.Subscription;

import java.util.List;

public interface SubscriptionDao extends AbstractDao<Subscription> {
	Subscription getSubscription(Long userId, Long lessonId);

	List<Lesson> getSubscriptionLessons(Long userId);

	List<Course> getSubscriptionCourses(Long userId);
}
