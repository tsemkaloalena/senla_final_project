package com.tsemkalo.experienceApp.mappers;

import com.tsemkalo.experienceApp.CourseDao;
import com.tsemkalo.experienceApp.LessonDto;
import com.tsemkalo.experienceApp.UserDao;
import com.tsemkalo.experienceApp.entities.Course;
import com.tsemkalo.experienceApp.entities.Lesson;
import com.tsemkalo.experienceApp.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LessonMapper implements Mapper<Lesson, LessonDto> {
	@Autowired
	private UserDao userDao;

	@Autowired
	private CourseDao courseDao;

	@Override
	public LessonDto toDto(Lesson lesson) {
		Long teacherId = null;
		if (lesson.getTeacher() != null) {
			teacherId = lesson.getTeacher().getId();
		}
		Long courseId = null;
		if (lesson.getCourse() != null) {
			courseId = lesson.getCourse().getId();
		}
		return new LessonDto(lesson.getId(), lesson.getTheme(), lesson.getSubject(), teacherId, courseId, lesson.getStatus(), lesson.getAddress(), lesson.getIndividual(), lesson.getMaxStudentsNumber(), lesson.getDescription(), lesson.getCost(), lesson.getDurability(), lesson.getLessonDate(), lesson.getFixedSalary(), lesson.getUnfixedSalary(), lesson.getFreePlacesLeft(), lesson.getAverageRating());
	}

	@Override
	public Lesson toEntity(LessonDto dto) {
		User teacher = null;
		if (dto.getTeacherId() != null) {
			teacher = userDao.getById(dto.getTeacherId());
		}
		Course course = null;
		if (dto.getCourseId() != null) {
			course = courseDao.getById(dto.getCourseId());
		}
		Lesson lesson = new Lesson(dto.getTheme(), dto.getSubject(), teacher, course, dto.getStatus(), dto.getAddress(), dto.getIndividual(), dto.getMaxStudentsNumber(), dto.getDescription(), dto.getCost(), dto.getDurability(), dto.getLessonDate(), dto.getFixedSalary(), dto.getUnfixedSalary(), dto.getAverageRating());
		lesson.setId(dto.getId());
		return lesson;
	}
}
