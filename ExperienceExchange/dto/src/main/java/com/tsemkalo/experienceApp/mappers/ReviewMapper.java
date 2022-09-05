package com.tsemkalo.experienceApp.mappers;

import com.tsemkalo.experienceApp.CourseDao;
import com.tsemkalo.experienceApp.LessonDao;
import com.tsemkalo.experienceApp.ReviewDto;
import com.tsemkalo.experienceApp.UserDao;
import com.tsemkalo.experienceApp.entities.Course;
import com.tsemkalo.experienceApp.entities.Lesson;
import com.tsemkalo.experienceApp.entities.Review;
import com.tsemkalo.experienceApp.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper implements Mapper<Review, ReviewDto> {
	@Autowired
	private UserDao userDao;

	@Autowired
	private LessonDao lessonDao;

	@Autowired
	private CourseDao courseDao;

	@Override
	public ReviewDto toDto(Review review) {
		Long lessonId = null;
		Long courseId = null;
		Long userId = null;
		if (review.getLesson() != null) {
			lessonId = review.getLesson().getId();
		}
		if (review.getCourse() != null) {
			courseId = review.getCourse().getId();
		}
		if (review.getUser() != null) {
			userId = review.getUser().getId();
		}
		return new ReviewDto(review.getId(), review.getRating(), review.getText(), userId, lessonId, courseId);
	}

	@Override
	public Review toEntity(ReviewDto dto) {
		User user = null;
		if (dto.getUserId() != null) {
			user = userDao.getById(dto.getUserId());
		}
		Lesson lesson = null;
		if (dto.getLessonId() != null) {
			lesson = lessonDao.getById(dto.getLessonId());
		}
		Course course = null;
		if (dto.getCourseId() != null) {
			course = courseDao.getById(dto.getCourseId());
		}
		Review review = new Review(dto.getRating(), dto.getText(), user, lesson, course);
		review.setId(dto.getId());
		return review;
	}
}
