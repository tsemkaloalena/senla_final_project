package com.tsemkalo.experienceApp;

import com.tsemkalo.experienceApp.entities.Review;

import java.util.List;

public interface ReviewDao extends AbstractDao<Review> {
	List<Review> getLessonReviews(Long lessonId);

	List<Review> getCourseReviews(Long courseId);

	List<Review> getCourseLessonsReviews(Long courseId);
}
