package com.tsemkalo.experienceApp.impl;

import com.tsemkalo.experienceApp.CourseDao;
import com.tsemkalo.experienceApp.LessonDao;
import com.tsemkalo.experienceApp.LessonService;
import com.tsemkalo.experienceApp.SubscriptionDao;
import com.tsemkalo.experienceApp.UserDao;
import com.tsemkalo.experienceApp.entities.Course;
import com.tsemkalo.experienceApp.entities.Lesson;
import com.tsemkalo.experienceApp.entities.Subscription;
import com.tsemkalo.experienceApp.entities.User;
import com.tsemkalo.experienceApp.enums.LessonStatus;
import com.tsemkalo.experienceApp.enums.RoleType;
import com.tsemkalo.experienceApp.exceptions.DenyLessonException;
import com.tsemkalo.experienceApp.exceptions.IncorrectDataException;
import com.tsemkalo.experienceApp.exceptions.LessonSubscriptionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LessonServiceImpl extends AbstractServiceImpl<Lesson, LessonDao> implements LessonService {
	@Autowired
	private LessonDao lessonDao;

	@Autowired
	private CourseDao courseDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	SubscriptionDao subscriptionDao;

	@Override
	public Map<String, Set<String>> getThemes() {
		return lessonDao.getThemes();
	}

	@Override
	public Predicate getTeacherPredicate(String teacherName, String teacherSurname) {
		return lessonDao.getTeacherPredicate(teacherName, teacherSurname);
	}

	@Override
	public Predicate getThemePredicate(String theme) {
		return lessonDao.getThemePredicate(theme);
	}

	@Override
	public Predicate getRatingPredicate(Double minRating) {
		return lessonDao.getRatingPredicate(minRating);
	}

	@Override
	public Predicate getOnlineOfflinePredicate(Boolean online, String address) {
		return lessonDao.getOnlineOfflinePredicate(online, address);
	}

	@Override
	public Predicate getCostPredicate(Boolean free, Integer minCost, Integer maxCost) {
		return lessonDao.getCostPredicate(free, minCost, maxCost);
	}

	@Override
	public Predicate getIndividualPredicate(Boolean individual) {
		return lessonDao.getIndividualPredicate(individual);
	}

	@Override
	public Predicate getStatusPredicate(LessonStatus lessonStatus) {
		return lessonDao.getStatusPredicate(lessonStatus);
	}

	@Override
	public Predicate getDatePredicate(LocalDateTime from, LocalDateTime to) {
		return lessonDao.getDatePredicate(from, to);
	}

	@Override
	public Predicate getFreePlacesLeftPredicate(Integer numberOfPlaces) {
		return lessonDao.getFreePlacesLeftPredicate(numberOfPlaces);
	}

	@Override
	public Predicate getSingleLessonsPredicate() {
		return lessonDao.getSingleLessonsPredicate();
	}

	@Override
	public List<Lesson> getLessons(List<Predicate> predicates) {
		return lessonDao.getLessons(predicates);
	}

	@Override
	public Lesson addLesson(String currentUsername, Lesson lesson) {
		User user = userDao.getUserByUsername(currentUsername);
		if (lesson.getId() != null) {
			throw new IncorrectDataException("New lesson can not have id");
		}
		if (RoleType.TEACHER.equals(user.getRole().getName())) {
			if (lesson.getTeacher() == null) {
				lesson.setTeacher(user);
			} else if (!lesson.getTeacher().equals(user)) {
				throw new AccessDeniedException("You can not create a lesson for other teacher");
			}
		}
		if (lesson.getTeacher() == null) {
			throw new IncorrectDataException("The course can not exist without teacher");
		}
		if (lesson.getCourse() != null) {
			checkCourseAccordingToLesson(currentUsername, lesson);
			lessonDao.create(lesson);
			courseDao.updateCourseAccordingToLesson(lesson);
		} else {
			lessonDao.create(lesson);
		}
		return lesson;
	}

	@Override
	public Lesson editLesson(String currentUsername, Lesson newLesson) {
		User teacher = userDao.getUserByUsername(currentUsername);
		if (newLesson.getId() == null) {
			throw new IncorrectDataException("Lesson id is not set");
		}
		Lesson lesson = lessonDao.getById(newLesson.getId());
		if (!RoleType.ADMIN.equals(teacher.getRole().getName()) && !teacher.getId().equals(lesson.getTeacher().getId())) {
			throw new AccessDeniedException("This lesson belongs to other teacher");
		}
		if (newLesson.getTeacher() != null && !newLesson.getTeacher().equals(teacher)) {
			if (RoleType.ADMIN.equals(teacher.getRole().getName())) {
				if (!RoleType.TEACHER.equals(newLesson.getTeacher().getRole().getName())) {
					throw new IncorrectDataException(newLesson.getTeacher().getName() + " " + newLesson.getTeacher().getSurname() + " is not a teacher");
				}
				Lesson scheduleOverlap = userDao.teacherScheduleOverlapsPresent(newLesson.getTeacher().getUsername(), newLesson.getLessonDate(), newLesson.getDurability());
				if (scheduleOverlap != null) {
					throw new IncorrectDataException("This teacher already has a lesson from " + scheduleOverlap.getLessonDate() +
							" to " + scheduleOverlap.getLessonDate().plusMinutes(scheduleOverlap.getDurability()));
				}
				lesson.setTeacher(newLesson.getTeacher());
			} else {
				throw new IncorrectDataException("You can not change teacher");
			}
		}
		if (newLesson.getTheme() != null) {
			lesson.setTheme(newLesson.getTheme());
		}
		if (newLesson.getSubject() != null) {
			lesson.setSubject(newLesson.getSubject());
		}
		if (newLesson.getStatus() != null) {
			lesson.setStatus(newLesson.getStatus());
			if (LessonStatus.DENIED.equals(lesson.getStatus()) || LessonStatus.FINISHED.equals(lesson.getStatus())) {
				while (lesson.getSubscriptions().size() != 0) {
					subscriptionDao.deleteById(lesson.getSubscriptions().get(0).getId());
					lesson.getSubscriptions().get(0).getUser().getSubscriptions().remove(0);
					lesson.getSubscriptions().remove(0);
				}
			}
		}
		if (newLesson.getIndividual() != null) {
			lesson.setIndividual(newLesson.getIndividual());
		}
		if (newLesson.getAddress() != null) {
			if ("".equals(newLesson.getAddress())) {
				lesson.setOnline();
			} else {
				lesson.setAddress(newLesson.getAddress());
			}
		}
		if (newLesson.getMaxStudentsNumber() != null) {
			if (newLesson.getMaxStudentsNumber() < lesson.getSubscriptions().size()) {
				throw new IncorrectDataException("There are already too many students subscribed to this lesson. You can not make max students number less then subscribers amount.");
			}
			lesson.setMaxStudentsNumber(newLesson.getMaxStudentsNumber());
		}
		if (newLesson.getDescription() != null) {
			lesson.setDescription(newLesson.getDescription());
		}
		if (newLesson.getCost() != null) {
			lesson.setCost(newLesson.getCost());
		}
		if (newLesson.getFixedSalary() != null) {
			lesson.setFixedSalary(newLesson.getFixedSalary());
		}
		if (newLesson.getUnfixedSalary() != null) {
			lesson.setUnfixedSalary(newLesson.getUnfixedSalary());
		}
		if (newLesson.getCourse() != null) {
			lesson.setCourse(newLesson.getCourse());
		}
		if (newLesson.getAverageRating() != null && !newLesson.getAverageRating().equals(lesson.getAverageRating())) {
			throw new IncorrectDataException("You can not change rating, it is not fair");
		}
		if (newLesson.getLessonDate() != null) {
			lesson.setLessonDate(newLesson.getLessonDate());
		}
		if (newLesson.getDurability() != null) {
			lesson.setDurability(newLesson.getDurability());
		}
		if (lesson.getCourse() != null) {
			checkCourseAccordingToLesson(currentUsername, lesson);
			courseDao.updateCourseAccordingToLesson(lesson);
		}
		return lesson;
	}

	private void checkCourseAccordingToLesson(String currentUsername, Lesson lesson) {
		// Lesson is an argument, because it is not in database yet, so id doesn't exist
		User user = userDao.getUserByUsername(currentUsername);
		Course course = lesson.getCourse();
		if (!RoleType.ADMIN.equals(user.getRole().getName()) && !user.getId().equals(course.getTeacher().getId())) {
			throw new AccessDeniedException("This course belongs to other teacher");
		}
		if (!lesson.getTeacher().getId().equals(course.getTeacher().getId())) {
			throw new IncorrectDataException("The course and lesson should have the same teacher");
		}
		if (!course.getTheme().equals(lesson.getTheme())) {
			throw new IncorrectDataException("The course and lesson should be have the same theme");
		}
		if (!course.getIndividual().equals(lesson.getIndividual())) {
			throw new IncorrectDataException("The course and lesson should be both group or individual");
		}
	}

	private void removeLessonFromCourse(Long lessonId) {
		Lesson lesson = lessonDao.getById(lessonId);
		Course course = lesson.getCourse();
		List<Lesson> lessons = course.getLessons();
		lessons.removeIf(removalLesson -> lessonId.equals(removalLesson.getId()));
		if (lessons.isEmpty()) {
			course.setStatus(LessonStatus.DENIED);
		}
		course.countCost();
		course.countStartDate();
		course.countFinishDate();
	}

	@Override
	public void denyLesson(String currentUsername, Long lessonId) {
		User teacher = userDao.getUserByUsername(currentUsername);
		Lesson lesson = lessonDao.getById(lessonId);
		if (!RoleType.ADMIN.equals(teacher.getRole().getName()) && !teacher.getId().equals(lesson.getTeacher().getId())) {
			throw new AccessDeniedException("This lesson belongs to other teacher");
		}
		if (LessonStatus.DENIED.equals(lesson.getStatus()) || LessonStatus.FINISHED.equals(lesson.getStatus()) || LessonStatus.IN_PROGRESS.equals(lesson.getStatus())) {
			throw new DenyLessonException("lesson with id " + lessonId, "This lesson is already " + lesson.getStatus().name().toLowerCase().replace("_", " "));
		}
		Course course = lesson.getCourse();
		if (course != null) {
			removeLessonFromCourse(lesson.getId());
		}
		lesson.setStatus(LessonStatus.DENIED);
		while (lesson.getSubscriptions().size() != 0) {
			subscriptionDao.deleteById(lesson.getSubscriptions().get(0).getId());
			lesson.getSubscriptions().get(0).getUser().getSubscriptions().remove(0);
			lesson.getSubscriptions().remove(0);
		}
	}

	@Override
	public void deleteLesson(String currentUsername, Long lessonId) {
		User teacher = userDao.getUserByUsername(currentUsername);
		Lesson lesson = lessonDao.getById(lessonId);
		if (!RoleType.ADMIN.equals(teacher.getRole().getName()) && !teacher.getId().equals(lesson.getTeacher().getId())) {
			throw new AccessDeniedException("This lesson belongs to other teacher");
		}
		if (LessonStatus.IN_PROGRESS.equals(lesson.getStatus())) {
			throw new DenyLessonException("lesson with id " + lessonId, "This lesson is in progress");
		}
		Course course = lesson.getCourse();
		if (course != null) {
			removeLessonFromCourse(lesson.getId());
		}
		lessonDao.deleteById(lessonId);
	}

	@Override
	public Boolean subscribe(String currentUsername, Long lessonId) {
		User student = userDao.getUserByUsername(currentUsername);
		Lesson lesson = lessonDao.getById(lessonId);
		if (!lesson.getStatus().equals(LessonStatus.NOT_STARTED)) {
			throw new LessonSubscriptionException("This lesson is already " + lesson.getStatus().name().toLowerCase().replace("_", " "));
		}
		if (subscriptionDao.getSubscription(student.getId(), lessonId) != null) {
			return false;
		}
		if (lesson.getFreePlacesLeft() != null && lesson.getFreePlacesLeft() <= 0) {
			throw new LessonSubscriptionException("There are no free places left for lesson with id " + lessonId);
		}
		Lesson scheduleOverlap = userDao.studentScheduleOverlapsPresent(currentUsername, lesson.getLessonDate(), lesson.getDurability());
		if (scheduleOverlap != null) {
			throw new LessonSubscriptionException("You already have a lesson from " + scheduleOverlap.getLessonDate() +
					" to " + scheduleOverlap.getLessonDate().plusMinutes(scheduleOverlap.getDurability()));
		}
		Subscription subscription = new Subscription(student, lesson);
		subscriptionDao.create(subscription);
		return true;
	}

	@Override
	public Boolean unsubscribe(String currentUsername, Long lessonId) {
		User student = userDao.getUserByUsername(currentUsername);
		Lesson lesson = lessonDao.getById(lessonId);
		Subscription subscription = subscriptionDao.getSubscription(student.getId(), lessonId);
		if (subscription == null) {
			return false;
		}
		if (lesson.getFreePlacesLeft() != null) {
			lesson.setFreePlacesLeft(lesson.getFreePlacesLeft() + 1);
		}
		subscriptionDao.deleteById(subscription.getId());
		lesson.getSubscriptions().remove(subscription);
		student.getSubscriptions().remove(subscription);
		return true;
	}

	@Override
	public List<Lesson> getSubscriptions(String currentUsername) {
		User student = userDao.getUserByUsername(currentUsername);
		return subscriptionDao.getSubscriptionLessons(student.getId());
	}

	@Override
	public List<Lesson> getSchedule(String currentUsername) {
		User user = userDao.getUserByUsername(currentUsername);
		if (RoleType.STUDENT.equals(user.getRole().getName())) {
			return user.getSubscriptions().stream().map(Subscription::getLesson).filter(lesson -> lesson.getStatus().equals(LessonStatus.NOT_STARTED) || lesson.getStatus().equals(LessonStatus.IN_PROGRESS)).collect(Collectors.toList());
		} else if (RoleType.TEACHER.equals(user.getRole().getName())) {
			return userDao.getTeacherLessons(user.getId()).stream().filter(lesson -> lesson.getStatus().equals(LessonStatus.NOT_STARTED) || lesson.getStatus().equals(LessonStatus.IN_PROGRESS)).collect(Collectors.toList());
		}
		return null;
	}

	@Override
	public Double countSalary(String currentUsername, Long lessonId) {
		User user = userDao.getUserByUsername(currentUsername);
		Lesson lesson = lessonDao.getById(lessonId);
		if (lesson.getTeacher().getId().equals(user.getId())) {
			return lesson.getSalary();
		}
		throw new AccessDeniedException("You don't have a lesson with id " + lessonId);
	}

	@Override
	public void updateLessonStatuses(String currentUsername, LocalDateTime currentTime) {
		if (currentTime == null) {
			currentTime = LocalDateTime.now();
		}
		User teacher = userDao.getUserByUsername(currentUsername);
		for (Lesson lesson : userDao.getTeacherLessons(teacher.getId())) {
			if (LessonStatus.DENIED.equals(lesson.getStatus())) {
				continue;
			}
			if (currentTime.equals(lesson.getLessonDate().plusMinutes(lesson.getDurability())) ||
					currentTime.isAfter(lesson.getLessonDate().plusMinutes(lesson.getDurability()))) {
				lesson.setStatus(LessonStatus.FINISHED);
			}
			if (lesson.getStatus().equals(LessonStatus.NOT_STARTED)) {
				if (lesson.getLessonDate().equals(currentTime) || currentTime.isAfter(lesson.getLessonDate())) {
					lesson.setStatus(LessonStatus.IN_PROGRESS);
				}
			}
			if (lesson.getLessonDate().isAfter(currentTime)) {
				lesson.setStatus(LessonStatus.NOT_STARTED);
			}
		}
	}
}
