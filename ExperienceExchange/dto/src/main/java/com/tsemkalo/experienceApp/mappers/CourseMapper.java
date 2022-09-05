package com.tsemkalo.experienceApp.mappers;

import com.tsemkalo.experienceApp.CourseDto;
import com.tsemkalo.experienceApp.UserDao;
import com.tsemkalo.experienceApp.entities.Course;
import com.tsemkalo.experienceApp.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper implements Mapper<Course, CourseDto> {
	@Autowired
	private UserDao userDao;

	@Override
	public CourseDto toDto(Course course) {
		Long teacherId = null;
		if (course.getTeacher() != null) {
			teacherId = course.getTeacher().getId();
		}
		return new CourseDto(course.getId(), course.getTheme(), course.getSubject(), course.getStatus(), course.getOnline(), course.getAddress(), teacherId, course.getIndividual(), course.getCost(), course.getStartDate(), course.getFinishDate(), course.getDescription(), course.getAverageRating());
	}

	@Override
	public Course toEntity(CourseDto dto) {
		User teacher = null;
		if (dto.getTeacherId() != null) {
			teacher = userDao.getById(dto.getTeacherId());
		}
		Course course = new Course(dto.getTheme(), dto.getSubject(), dto.getStatus(), dto.getOnline(), dto.getAddress(), teacher, dto.getIndividual(), dto.getCost(), dto.getStartDate(), dto.getFinishDate(), dto.getDescription(), dto.getAverageRating());
		course.setId(dto.getId());
		return course;
	}
}
