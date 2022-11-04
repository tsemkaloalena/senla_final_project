package com.tsemkalo.senlaExperienceExchangeApp.dao;

import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Review;

import java.util.List;

public interface ReviewDao extends AbstractDao<Review> {
	List<Review> getLessonReviews(Long lessonId);

	List<Review> getCourseReviews(Long courseId);

	List<Review> getCourseLessonsReviews(Long courseId);
}
