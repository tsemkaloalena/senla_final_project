package com.tsemkalo.senlaExperienceExchangeApp.service.impl;

import com.tsemkalo.senlaExperienceExchangeApp.dao.CourseDao;
import com.tsemkalo.senlaExperienceExchangeApp.dao.LessonDao;
import com.tsemkalo.senlaExperienceExchangeApp.dao.SubscriptionDao;
import com.tsemkalo.senlaExperienceExchangeApp.dao.UserDao;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Course;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Lesson;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Subscription;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.User;
import com.tsemkalo.senlaExperienceExchangeApp.enums.LessonStatus;
import com.tsemkalo.senlaExperienceExchangeApp.enums.RoleType;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.DenyLessonException;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.IncorrectDataException;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.LessonSubscriptionException;
import com.tsemkalo.senlaExperienceExchangeApp.service.LessonService;
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

	/**
	 * @return themes of all courses and lessons
	 */
	@Override
	public Map<String, Set<String>> getThemes() {
		return lessonDao.getThemes();
	}

	/**
	 * @param teacherName    teacher's name
	 * @param teacherSurname teacher's surname
	 * @return Predicate that later will be sent to {@link LessonServiceImpl#getLessons(List)} for list filtering
	 */
	@Override
	public Predicate getTeacherPredicate(String teacherName, String teacherSurname) {
		return lessonDao.getTeacherPredicate(teacherName, teacherSurname);
	}

	/**
	 * @param theme lesson theme
	 * @return Predicate that later will be sent to {@link LessonServiceImpl#getLessons(List)} for list filtering
	 */
	@Override
	public Predicate getThemePredicate(String theme) {
		return lessonDao.getThemePredicate(theme);
	}

	/**
	 * @param minRating minimal lesson rating
	 * @return Predicate that later will be sent to {@link LessonServiceImpl#getLessons(List)} for list filtering
	 */
	@Override
	public Predicate getRatingPredicate(Double minRating) {
		return lessonDao.getRatingPredicate(minRating);
	}

	/**
	 * @param online  is lesson online or offline
	 * @param address address of the lesson, if null, then the lesson is online
	 * @return Predicate that later will be sent to {@link LessonServiceImpl#getLessons(List)} for list filtering
	 */
	@Override
	public Predicate getOnlineOfflinePredicate(Boolean online, String address) {
		return lessonDao.getOnlineOfflinePredicate(online, address);
	}

	/**
	 * @param free    is the lesson free or not (can be null)
	 * @param minCost minimal cost (can be null)
	 * @param maxCost maximal cost (can be null)
	 * @return Predicate that later will be sent to {@link LessonServiceImpl#getLessons(List)} for list filtering
	 */
	@Override
	public Predicate getCostPredicate(Boolean free, Integer minCost, Integer maxCost) {
		return lessonDao.getCostPredicate(free, minCost, maxCost);
	}

	/**
	 * @param individual is the lesson individual or group
	 * @return Predicate that later will be sent to {@link LessonServiceImpl#getLessons(List)} for list filtering
	 */
	@Override
	public Predicate getIndividualPredicate(Boolean individual) {
		return lessonDao.getIndividualPredicate(individual);
	}

	/**
	 * @param lessonStatus is the course started, finished and etc
	 * @return Predicate that later will be sent to {@link LessonServiceImpl#getLessons(List)} for list filtering
	 */
	@Override
	public Predicate getStatusPredicate(LessonStatus lessonStatus) {
		return lessonDao.getStatusPredicate(lessonStatus);
	}

	/**
	 * @param from start searching time, time after which the lesson have to be started
	 * @param to   finish searching time, the time by which the lesson have to be completed
	 * @return Predicate that later will be sent to {@link LessonServiceImpl#getLessons(List)} for list filtering
	 */
	@Override
	public Predicate getDatePredicate(LocalDateTime from, LocalDateTime to) {
		return lessonDao.getDatePredicate(from, to);
	}

	/**
	 * @param numberOfPlaces minimal number of free places left
	 * @return Predicate that later will be sent to {@link LessonServiceImpl#getLessons(List)} for list filtering
	 */
	@Override
	public Predicate getFreePlacesLeftPredicate(Integer numberOfPlaces) {
		return lessonDao.getFreePlacesLeftPredicate(numberOfPlaces);
	}

	/**
	 * @return Predicate that later will be sent to {@link LessonServiceImpl#getLessons(List)} to show lessons that don't belong to anu course
	 */
	@Override
	public Predicate getSingleLessonsPredicate() {
		return lessonDao.getSingleLessonsPredicate();
	}

	/**
	 * @param predicates predicates by which the list will be filtered
	 * @return list of all lessons (if the param is null then not filtered, if param is not null then filtered)
	 */
	@Override
	public List<Lesson> getLessons(List<Predicate> predicates) {
		return lessonDao.getLessons(predicates);
	}

	/**
	 * @param currentUsername username of current user (the user is teacher or admin)
	 * @param lesson          lesson that should be added
	 * @return added to database lesson
	 * @throws IncorrectDataException if id is set
	 * @throws AccessDeniedException  the current user is a teacher and another teacher is set to lesson
	 * @throws IncorrectDataException the current user is admin and teacher is not set to lesson
	 */
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

	/**
	 * @param currentUsername username of current user (the user is teacher or admin)
	 * @param newLesson       lesson entity with fields that should be changed
	 * @return edited lesson
	 * @throws IncorrectDataException if edited lesson id is not set
	 * @throws AccessDeniedException  if the current user is a teacher and another teacher is set to lesson
	 * @throws IncorrectDataException if schedule overlaps appear
	 * @throws IncorrectDataException if the lesson rating is edited
	 */
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
			editTeacher(currentUsername, lesson.getId(), newLesson);
		}
		if (newLesson.getTheme() != null) {
			lesson.setTheme(newLesson.getTheme());
		}
		if (newLesson.getSubject() != null) {
			lesson.setSubject(newLesson.getSubject());
		}
		if (newLesson.getStatus() != null) {
			editLessonStatus(lesson, newLesson);
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

	private void editLessonStatus(Lesson lesson, Lesson newLesson) {
		lesson.setStatus(newLesson.getStatus());
		if (LessonStatus.DENIED.equals(lesson.getStatus()) || LessonStatus.FINISHED.equals(lesson.getStatus())) {
			while (lesson.getSubscriptions().size() != 0) {
				subscriptionDao.deleteById(lesson.getSubscriptions().get(0).getId());
				lesson.getSubscriptions().get(0).getUser().getSubscriptions().remove(0);
				lesson.getSubscriptions().remove(0);
			}
		}
	}

	private void editTeacher(String currentUsername, Long lessonId, Lesson newLesson) {
		User teacher = userDao.getUserByUsername(currentUsername);
		Lesson lesson = lessonDao.getById(lessonId);
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
		courseDao.countCost(course.getId());
		courseDao.countStartDate(course.getId());
		courseDao.countFinishDate(course.getId());
	}

	/**
	 * @param currentUsername username of current user (the user is a teacher or an admin)
	 * @param lessonId        id of the lesson that should be denied
	 * @throws AccessDeniedException if the current user is a teacher and another teacher is set to lesson
	 * @throws DenyLessonException   if the course is in progress, already denied or finished
	 */
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

	/**
	 * @param currentUsername username of current user (the user is a teacher or an admin)
	 * @param lessonId        id of the lesson that should be deleted
	 * @throws AccessDeniedException if the current user is a teacher and another teacher is set to lesson
	 * @throws DenyLessonException   if the lesson is in progress
	 */
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

	/**
	 * @param currentUsername username of current user (the user is a student)
	 * @param lessonId        id of the lesson to which the student wants to subscribe
	 * @return boolean if the user successfully subscribed to the lesson
	 * @throws LessonSubscriptionException if the lesson is already in progress, finished or denied
	 * @throws LessonSubscriptionException if there are no free places left for the lesson
	 * @throws LessonSubscriptionException if there is some schedule overlap
	 */
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

	/**
	 * @param currentUsername username of current user (the user is a student)
	 * @param lessonId        id of the lesson which the student wants to unsubscribe
	 * @return boolean if the user successfully unsubscribed the lesson
	 */
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

	/**
	 * @param currentUsername username of current user (the user is a student)
	 * @return list of lessons to which the student is subscribed
	 */
	@Override
	public List<Lesson> getSubscriptions(String currentUsername) {
		User student = userDao.getUserByUsername(currentUsername);
		return subscriptionDao.getSubscriptionLessons(student.getId());
	}

	/**
	 * @param currentUsername username of current user (the user is a student or a teacher)
	 * @return list of not started or in progress lessons to which a student is subscribed (or a teacher leads)
	 */
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

	/**
	 * @param currentUsername username of current user (the user is a teacher or an admin)
	 * @param lessonId        id of the lesson for which the salary should be counted
	 * @return teacher salary for the lesson
	 * @throws AccessDeniedException if the lesson doesn't belong to current teacher
	 */
	@Override
	public Double countSalary(String currentUsername, Long lessonId) {
		User user = userDao.getUserByUsername(currentUsername);
		Lesson lesson = lessonDao.getById(lessonId);
		if (RoleType.ADMIN.equals(user.getRole().getName()) || lesson.getTeacher().getId().equals(user.getId())) {
			return lessonDao.getSalaryForLesson(lessonId);
		}
		throw new AccessDeniedException("You don't have a lesson with id " + lessonId);
	}

	/**
	 * @param currentUsername username of current user (the user is a teacher or an admin)
	 * @param currentTime     time according to which the lesson should change its status
	 * @param lessonId        lesson id whose status should be changed
	 */
	@Override
	public void updateLessonStatus(String currentUsername, LocalDateTime currentTime, Long lessonId) {
		if (currentTime == null) {
			currentTime = LocalDateTime.now();
		}
		User teacher = userDao.getUserByUsername(currentUsername);
		Lesson lesson = lessonDao.getById(lessonId);
		if (!lesson.getTeacher().getId().equals(teacher.getId()) && !RoleType.ADMIN.equals(teacher.getRole().getName())) {
			throw new AccessDeniedException("You don't have a lesson with id " + lessonId);
		}
		lessonDao.updateLessonStatus(lessonId, currentTime);
	}
}
