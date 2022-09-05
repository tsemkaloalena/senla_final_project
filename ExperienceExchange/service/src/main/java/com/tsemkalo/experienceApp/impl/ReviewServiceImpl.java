package com.tsemkalo.experienceApp.impl;

import com.tsemkalo.experienceApp.ReviewDao;
import com.tsemkalo.experienceApp.ReviewService;
import com.tsemkalo.experienceApp.SubscriptionDao;
import com.tsemkalo.experienceApp.UserDao;
import com.tsemkalo.experienceApp.entities.Course;
import com.tsemkalo.experienceApp.entities.Lesson;
import com.tsemkalo.experienceApp.entities.Review;
import com.tsemkalo.experienceApp.entities.User;
import com.tsemkalo.experienceApp.enums.RoleType;
import com.tsemkalo.experienceApp.exceptions.IncorrectDataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReviewServiceImpl extends AbstractServiceImpl<Review, ReviewDao> implements ReviewService {
	@Autowired
	private ReviewDao reviewDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private SubscriptionDao subscriptionDao;

	@Override
	public List<Review> getLessonReviews(Long lessonId) {
		return reviewDao.getLessonReviews(lessonId);
	}

	@Override
	public List<Review> getCourseReviews(Long courseId) {
		return reviewDao.getCourseReviews(courseId);
	}

	@Override
	public List<Review> getCourseLessonsReviews(Long courseId) {
		return reviewDao.getCourseLessonsReviews(courseId);
	}

	private void recountRatings(Review review) {
		Lesson lesson = review.getLesson();
		Course course = review.getCourse();
		if (lesson != null) {
			lesson.countAverageRating();
		}
		if (course != null) {
			course.countAverageRating();
		}
	}

	@Override
	public Long createReview(String currentUsername, Review review) {
		User user = userDao.getUserByUsername(currentUsername);
		review.setUser(user);
		Lesson lesson = review.getLesson();
		Course course = review.getCourse();
		if (review.getRating() == null) {
			throw new IncorrectDataException("The review must contain a rating");
		}
		if (lesson == null && course == null) {
			throw new IncorrectDataException("Lesson or course id is not set in review");
		}
		if (lesson != null && course != null) {
			throw new IncorrectDataException("You can not write review for a lesson and a course at the same time");
		}
		if (lesson != null && subscriptionDao.getSubscription(user.getId(), lesson.getId()) == null) {
			throw new AccessDeniedException("You are not subscribed to this lesson, so you can not write a review for it.");
		}
		if (course != null && !subscriptionDao.getSubscriptionCourses(user.getId()).contains(course)) {
			throw new AccessDeniedException("You are not subscribed to this course, so you can not write a review for it.");
		}
		reviewDao.create(review);
		recountRatings(review);
		return review.getId();
	}

	@Override
	public void editReview(String currentUsername, Review editedReview) {
		User user = userDao.getUserByUsername(currentUsername);
		if (editedReview.getId() == null) {
			throw new IncorrectDataException("You didn't choose which review you want to edit (id is not set)");
		}
		Review review = reviewDao.getById(editedReview.getId());
		if (!review.getUser().getId().equals(user.getId())) {
			throw new AccessDeniedException("Review with id " + review.getId() + " is not yours.");
		}
		if (editedReview.getText() != null) {
			review.setText(editedReview.getText());
		}
		if (editedReview.getRating() != null) {
			review.setRating(editedReview.getRating());
		}
		if (editedReview.getRating() != null) {
			recountRatings(review);
		}
	}

	@Override
	public void deleteReview(String currentUsername, Long reviewId) {
		User user = userDao.getUserByUsername(currentUsername);
		Review review = reviewDao.getById(reviewId);
		if (!RoleType.ADMIN.equals(user.getRole().getName()) && !review.getUser().getId().equals(user.getId())) {
			throw new AccessDeniedException("You don't have writes to delete this review.");
		}
		reviewDao.deleteById(reviewId);
		review.getUser().getReviews().remove(review);
		if (review.getLesson() != null) {
			review.getLesson().getReviews().remove(review);
		}
		if (review.getCourse() != null) {
			review.getCourse().getReviews().remove(review);
		}
		recountRatings(review);
	}
}
