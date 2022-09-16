package com.tsemkalo.senlaExperienceExchangeApp.dto.mappers;

import com.tsemkalo.senlaExperienceExchangeApp.dao.CourseDao;
import com.tsemkalo.senlaExperienceExchangeApp.dao.LessonDao;
import com.tsemkalo.senlaExperienceExchangeApp.dao.UserDao;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Course;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Lesson;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Review;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.User;
import com.tsemkalo.senlaExperienceExchangeApp.dto.ReviewDto;
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
