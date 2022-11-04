package com.tsemkalo.senlaExperienceExchangeApp.service;

import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Review;

import java.util.List;

public interface ReviewService extends AbstractService<Review> {
	List<Review> getLessonReviews(Long lessonId);

	List<Review> getCourseReviews(Long courseId);

	List<Review> getCourseLessonsReviews(Long courseId);

	Long createReview(String currentUsername, Review review);

	void editReview(String currentUsername, Review editedReview);

	void deleteReview(String currentUsername, Long reviewId);
}
