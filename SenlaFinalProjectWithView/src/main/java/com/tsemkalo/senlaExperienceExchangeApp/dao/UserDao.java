package com.tsemkalo.senlaExperienceExchangeApp.dao;

import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Lesson;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.User;

import java.time.LocalDateTime;
import java.util.List;

public interface UserDao extends AbstractDao<User> {
	User getUserByUsername(String username);

	Lesson studentScheduleOverlapsPresent(String username, LocalDateTime start, Integer duration);

	Lesson teacherScheduleOverlapsPresent(String username, LocalDateTime start, Integer duration);

	List<Lesson> getTeacherLessons(Long teacherId);
}
