package com.tsemkalo.senlaExperienceExchangeApp.dao.impl;

import com.tsemkalo.senlaExperienceExchangeApp.dao.UserDao;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Lesson;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Subscription;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.User;
import com.tsemkalo.senlaExperienceExchangeApp.enums.RoleType;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.IncorrectDataException;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class UserDaoImpl extends AbstractDaoImpl<User> implements UserDao {
	@Override
	public Class<User> getEntityClass() {
		return User.class;
	}

	@Override
	public User getUserByUsername(String username) {
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
		Root<User> rootEntry = criteriaQuery.from(User.class);
		CriteriaQuery<User> query = criteriaQuery.select(rootEntry)
				.where(
						criteriaBuilder.equal(rootEntry.get("username"), username)
				);
		List<User> resultList = getEntityManager().createQuery(query).getResultList();
		if (resultList.isEmpty()) {
			return null;
		}
		return getEntityManager().createQuery(query).getSingleResult();
	}

	@Override
	public List<Lesson> getTeacherLessons(Long teacherId) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Lesson> criteriaQuery = criteriaBuilder.createQuery(Lesson.class);
		Root<Lesson> rootEntry = criteriaQuery.from(Lesson.class);
		CriteriaQuery<Lesson> lessons = criteriaQuery.select(rootEntry)
				.where(
						criteriaBuilder.equal(rootEntry.get("teacher").get("id"), teacherId)
				);
		return entityManager.createQuery(lessons).getResultList();
	}

	@Override
	public Lesson studentScheduleOverlapsPresent(String username, LocalDateTime start, Integer durability) {
		User student = getUserByUsername(username);
		if (!RoleType.STUDENT.equals(student.getRole().getName())) {
			throw new IncorrectDataException(username + " is not a student");
		}
		LocalDateTime finish = start.plusMinutes(durability);
		for (Subscription subscription : student.getSubscriptions()) {
			LocalDateTime subscriptionStart = subscription.getLesson().getLessonDate();
			LocalDateTime subscriptionFinish = subscription.getLesson().getLessonDate().plusMinutes(subscription.getLesson().getDurability());
			if (start.isAfter(subscriptionStart) && start.isBefore(subscriptionFinish) ||
					finish.isAfter(subscriptionStart) && finish.isBefore(subscriptionFinish)) {
				return subscription.getLesson();
			}
		}
		return null;
	}

	@Override
	public Lesson teacherScheduleOverlapsPresent(String username, LocalDateTime start, Integer durability) {
		User teacher = getUserByUsername(username);
		if (!RoleType.TEACHER.equals(teacher.getRole().getName())) {
			throw new IncorrectDataException(username + " is not a teacher");
		}
		LocalDateTime finish = start.plusMinutes(durability);
		for (Lesson lesson : getTeacherLessons(teacher.getId())) {
			LocalDateTime subscriptionStart = lesson.getLessonDate();
			LocalDateTime subscriptionFinish = lesson.getLessonDate().plusMinutes(lesson.getDurability());
			if (start.isAfter(subscriptionStart) && start.isBefore(subscriptionFinish) ||
					finish.isAfter(subscriptionStart) && finish.isBefore(subscriptionFinish)) {
				return lesson;
			}
		}
		return null;
	}
}
