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
import com.tsemkalo.senlaExperienceExchangeApp.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CourseServiceImpl extends AbstractServiceImpl<Course, CourseDao> implements CourseService {
	@Autowired
	private LessonDao lessonDao;

	@Autowired
	private CourseDao courseDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private SubscriptionDao subscriptionDao;

	/**
	 * @param teacherName    teacher's name
	 * @param teacherSurname teacher's surname
	 * @return Predicate that later will be sent to {@link CourseServiceImpl#getCourses(List)} for list filtering
	 */
	@Override
	public Predicate getTeacherPredicate(String teacherName, String teacherSurname) {
		return courseDao.getTeacherPredicate(teacherName, teacherSurname);
	}

	/**
	 * @param theme course theme
	 * @return Predicate that later will be sent to {@link CourseServiceImpl#getCourses(List)} for list filtering
	 */
	@Override
	public Predicate getThemePredicate(String theme) {
		return courseDao.getThemePredicate(theme);
	}

	/**
	 * @param minRating minimal course rating
	 * @return Predicate that later will be sent to {@link CourseServiceImpl#getCourses(List)} for list filtering
	 */
	@Override
	public Predicate getRatingPredicate(Double minRating) {
		return lessonDao.getRatingPredicate(minRating);
	}

	/**
	 * @param online  is course online or offline
	 * @param address address of the course, if null, then the course is online
	 * @return Predicate that later will be sent to {@link CourseServiceImpl#getCourses(List)} for list filtering
	 */
	@Override
	public Predicate getOnlineOfflinePredicate(Boolean online, String address) {
		return courseDao.getOnlineOfflinePredicate(online, address);
	}

	/**
	 * @param free    is the course free or not (can be null)
	 * @param minCost minimal cost (can be null)
	 * @param maxCost maximal cost (can be null)
	 * @return Predicate that later will be sent to {@link CourseServiceImpl#getCourses(List)} for list filtering
	 */
	@Override
	public Predicate getCostPredicate(Boolean free, Integer minCost, Integer maxCost) {
		return courseDao.getCostPredicate(free, minCost, maxCost);
	}

	/**
	 * @param individual is the course individual or group
	 * @return Predicate that later will be sent to {@link CourseServiceImpl#getCourses(List)} for list filtering
	 */
	@Override
	public Predicate getIndividualPredicate(Boolean individual) {
		return courseDao.getIndividualPredicate(individual);
	}

	/**
	 * @param lessonStatus is the course started, finished and etc
	 * @return Predicate that later will be sent to {@link CourseServiceImpl#getCourses(List)} for list filtering
	 */
	@Override
	public Predicate getStatusPredicate(LessonStatus lessonStatus) {
		return courseDao.getStatusPredicate(lessonStatus);
	}

	/**
	 * @param from start searching time, time after which the course have to be started
	 * @param to   finish searching time, the time by which the course have to be completed
	 * @return Predicate that later will be sent to {@link CourseServiceImpl#getCourses(List)} for list filtering
	 */
	@Override
	public Predicate getDatePredicate(LocalDateTime from, LocalDateTime to) {
		return courseDao.getDatePredicate(from, to);
	}

	/**
	 * @param numberOfPlaces minimal number of free places left
	 * @return Predicate that later will be sent to {@link CourseServiceImpl#getCourses(List)} for list filtering
	 */
	@Override
	public Predicate getFreePlacesLeftPredicate(Integer numberOfPlaces) {
		return courseDao.getFreePlacesLeftPredicate(numberOfPlaces);
	}

	/**
	 * @param predicates predicates by which the list will be filtered
	 * @return list of all courses (if the param is null then not filtered, if param is not null then filtered)
	 */
	@Override
	public List<Course> getCourses(List<Predicate> predicates) {
		return courseDao.getCourses(predicates);
	}

	/**
	 * @param currentUsername username of current user (the user is teacher or admin)
	 * @param course          course that should be added
	 * @return added to database course
	 * @throws IncorrectDataException if id is set
	 * @throws AccessDeniedException  the current user is a teacher and another teacher is set to course
	 * @throws IncorrectDataException the current user is admin and teacher is not set to course
	 */
	@Override
	public Course addCourse(String currentUsername, Course course) {
		User user = userDao.getUserByUsername(currentUsername);
		if (course.getId() != null) {
			throw new IncorrectDataException("New course can not have id");
		}
		if (RoleType.TEACHER.equals(user.getRole().getName())) {
			if (course.getTeacher() == null) {
				course.setTeacher(user);
			} else if (!course.getTeacher().equals(user)) {
				throw new AccessDeniedException("You can not create a course for other teacher");
			}
		}
		if (course.getTeacher() == null) {
			throw new IncorrectDataException("The course can not exist without teacher");
		}
		courseDao.create(course);
		return course;
	}

	/**
	 * @param currentUsername username of current user (the user is teacher or admin)
	 * @param newCourse       course entity with fields that should be changed
	 * @return edited course
	 * @throws IncorrectDataException if edited course id is not set
	 * @throws AccessDeniedException  if the current user is a teacher and another teacher is set to course
	 * @throws IncorrectDataException if the course rating is edited
	 */
	@Override
	public Course editCourse(String currentUsername, Course newCourse) {
		User teacher = userDao.getUserByUsername(currentUsername);
		if (newCourse.getId() == null) {
			throw new IncorrectDataException("Course id is not set");
		}
		Course course = courseDao.getById(newCourse.getId());
		if (!RoleType.ADMIN.equals(teacher.getRole().getName()) && !teacher.equals(course.getTeacher())) {
			throw new AccessDeniedException("This course belongs to other teacher");
		}
		if (newCourse.getAverageRating() != null && !newCourse.getAverageRating().equals(course.getAverageRating())) {
			throw new IncorrectDataException("You can not change rating, it is not fair");
		}
		if (newCourse.getTeacher() != null && !newCourse.getTeacher().equals(teacher)) {
			editCourseTeacher(currentUsername, newCourse, course);
		}
		if (newCourse.getTheme() != null) {
			course.setTheme(newCourse.getTheme());
		}
		if (newCourse.getSubject() != null) {
			course.setSubject(newCourse.getSubject());
		}
		if (newCourse.getStatus() != null) {
			editCourseStatus(newCourse, course);
		}
		if (newCourse.getOnline() != null) {
			course.setOnline(newCourse.getOnline());
		}
		if (newCourse.getAddress() != null) {
			course.setAddress(newCourse.getAddress());
		}
		if (newCourse.getIndividual() != null) {
			course.setIndividual(newCourse.getIndividual());
		}
		if (newCourse.getCost() != null) {
			course.setCost(newCourse.getCost());
		}
		if (newCourse.getStartDate() != null) {
			course.setStartDate(newCourse.getStartDate());
		}
		if (newCourse.getFinishDate() != null) {
			course.setFinishDate(newCourse.getFinishDate());
		}
		if (newCourse.getDescription() != null) {
			course.setDescription(newCourse.getDescription());
		}
		return course;
	}

	private void editCourseTeacher(String currentUsername, Course newCourse, Course course) {
		User teacher = userDao.getUserByUsername(currentUsername);
		if (RoleType.ADMIN.equals(teacher.getRole().getName())) {
			if (!RoleType.TEACHER.equals(newCourse.getTeacher().getRole().getName())) {
				throw new IncorrectDataException(newCourse.getTeacher().getName() + " " + newCourse.getTeacher().getSurname() + " is not a teacher");
			}
			for (Lesson lesson : course.getLessons()) {
				Lesson scheduleOverlap = userDao.teacherScheduleOverlapsPresent(newCourse.getTeacher().getUsername(), lesson.getLessonDate(), lesson.getDurability());
				if (scheduleOverlap != null) {
					throw new IncorrectDataException("This teacher already has a lesson from " + scheduleOverlap.getLessonDate() +
							" to " + scheduleOverlap.getLessonDate().plusMinutes(scheduleOverlap.getDurability()));
				}
			}
			course.setTeacher(newCourse.getTeacher());
			for (Lesson lesson : course.getLessons()) {
				lesson.setTeacher(newCourse.getTeacher());
			}
		} else {
			throw new IncorrectDataException("You can not change teacher");
		}
	}

	private void editCourseStatus(Course newCourse, Course course) {
		course.setStatus(newCourse.getStatus());
		for (Lesson lesson : course.getLessons()) {
			lesson.setStatus(newCourse.getStatus());
			if (LessonStatus.DENIED.equals(lesson.getStatus()) || LessonStatus.FINISHED.equals(lesson.getStatus())) {
				while (lesson.getSubscriptions().size() != 0) {
					subscriptionDao.deleteById(lesson.getSubscriptions().get(0).getId());
					lesson.getSubscriptions().get(0).getUser().getSubscriptions().remove(0);
					lesson.getSubscriptions().remove(0);
				}
			}
		}
	}

	/**
	 * @param currentUsername username of current user (the user is a teacher or an admin)
	 * @param courseId        id of the course that should be denied
	 * @return string with denied lesson ids
	 * @throws AccessDeniedException if the current user is a teacher and another teacher is set to course
	 * @throws DenyLessonException   if the course is already denied or finished
	 * @throws DenyLessonException   if the course has in progress lessons
	 */
	@Override
	public List<Long> denyCourse(String currentUsername, Long courseId) {
		User teacher = userDao.getUserByUsername(currentUsername);
		Course course = courseDao.getById(courseId);
		if (!RoleType.ADMIN.equals(teacher.getRole().getName()) && !teacher.getId().equals(course.getTeacher().getId())) {
			throw new AccessDeniedException("This course belongs to other teacher");
		}
		if (LessonStatus.DENIED.equals(course.getStatus()) || LessonStatus.FINISHED.equals(course.getStatus())) {
			throw new DenyLessonException("course with id " + courseId, "This course is already " + course.getStatus().name().toLowerCase());
		}
		if (course.getLessons().stream().anyMatch(lesson -> LessonStatus.IN_PROGRESS.equals(lesson.getStatus()))) {
			throw new DenyLessonException("course with id " + courseId, "This course has in progress lessons");
		}
		course.setStatus(LessonStatus.DENIED);
		List<Lesson> deniedLessons = course.getLessons().stream().filter(lesson -> LessonStatus.NOT_STARTED.equals(lesson.getStatus())).collect(Collectors.toList());
		if (deniedLessons.isEmpty()) {
			return null;
		}
		deniedLessons.forEach(lesson -> lesson.setStatus(LessonStatus.DENIED));
		for (Lesson lesson : deniedLessons) {
			while (lesson.getSubscriptions().size() != 0) {
				subscriptionDao.deleteById(lesson.getSubscriptions().get(0).getId());
				lesson.getSubscriptions().get(0).getUser().getSubscriptions().remove(0);
				lesson.getSubscriptions().remove(0);
			}
		}
		return deniedLessons.stream().map(Lesson::getId).collect(Collectors.toList());
	}

	/**
	 * @param currentUsername username of current user (the user is a teacher or an admin)
	 * @param courseId        id of the course that should be deleted
	 * @return string with deleted lesson ids
	 * @throws AccessDeniedException if the current user is a teacher and another teacher is set to course
	 * @throws DenyLessonException   if the course has in progress lessons
	 */
	@Override
	public List<Long> deleteCourse(String currentUsername, Long courseId) {
		User teacher = userDao.getUserByUsername(currentUsername);
		Course course = courseDao.getById(courseId);
		if (!RoleType.ADMIN.equals(teacher.getRole().getName()) && !teacher.getId().equals(course.getTeacher().getId())) {
			throw new AccessDeniedException("This course belongs to other teacher");
		}
		if (course.getLessons().stream().anyMatch(lesson -> LessonStatus.IN_PROGRESS.equals(lesson.getStatus()))) {
			throw new DenyLessonException("course with id " + courseId, "This course has in progress lessons");
		}
		List<Long> deniedLessons = course.getLessons().stream().filter(lesson -> LessonStatus.NOT_STARTED.equals(lesson.getStatus())).map(Lesson::getId).collect(Collectors.toList());
		courseDao.deleteById(courseId);
		if (deniedLessons.isEmpty()) {
			return null;
		}
		return deniedLessons;
	}

	/**
	 * @param currentUsername username of current user (the user is a student)
	 * @param courseId        id of the course to which the student wants to subscribe
	 * @return boolean if the user successfully subscribed to the course
	 * @throws LessonSubscriptionException if the course is already in progress, finished or denied
	 * @throws LessonSubscriptionException if there are no free places left for the course
	 * @throws LessonSubscriptionException if there is some schedule overlap
	 */
	@Override
	public Boolean subscribe(String currentUsername, Long courseId) {
		User student = userDao.getUserByUsername(currentUsername);
		Course course = courseDao.getById(courseId);
		if (!course.getStatus().equals(LessonStatus.NOT_STARTED)) {
			throw new LessonSubscriptionException("This course is already " + course.getStatus().name().toLowerCase().replace("_", " "));
		}
		if (course.getFreePlacesLeft() != null && course.getFreePlacesLeft() <= 0) {
			throw new LessonSubscriptionException("There are no free places left for course with id " + courseId);
		}

		for (Lesson lesson : course.getLessons()) {
			if (subscriptionDao.getSubscription(student.getId(), lesson.getId()) != null) {
				return false;
			}
			Lesson scheduleOverlap = userDao.studentScheduleOverlapsPresent(currentUsername, lesson.getLessonDate(), lesson.getDurability());
			if (scheduleOverlap != null) {
				throw new LessonSubscriptionException("You already have a lesson from " + scheduleOverlap.getLessonDate() +
						" to " + scheduleOverlap.getLessonDate().plusMinutes(scheduleOverlap.getDurability()));
			}
		}
		course.getLessons().forEach(lesson -> subscriptionDao.create(new Subscription(student, lesson)));
		return true;
	}

	/**
	 * @param currentUsername username of current user (the user is a student)
	 * @param courseId        id of the course which the student wants to unsubscribe
	 * @return boolean if the user successfully unsubscribed the course
	 */
	@Override
	public Boolean unsubscribe(String currentUsername, Long courseId) {
		User student = userDao.getUserByUsername(currentUsername);
		Course course = courseDao.getById(courseId);
		for (Lesson lesson : course.getLessons()) {
			Subscription subscription = subscriptionDao.getSubscription(student.getId(), lesson.getId());
			if (subscription == null) {
				return false;
			}
			if (lesson.getFreePlacesLeft() != null) {
				lesson.setFreePlacesLeft(lesson.getFreePlacesLeft() + 1);
			}
			subscriptionDao.deleteById(subscription.getId());
			lesson.getSubscriptions().get(0).getUser().getSubscriptions().remove(0);
			lesson.getSubscriptions().remove(0);
		}
		return true;
	}

	/**
	 * @param currentUsername username of current user (the user is a student)
	 * @return list of courses to which the student is subscribed
	 */
	@Override
	public List<Course> getSubscriptions(String currentUsername) {
		User student = userDao.getUserByUsername(currentUsername);
		return subscriptionDao.getSubscriptionCourses(student.getId());
	}

	/**
	 * @param currentUsername username of current user (the user is a teacher)
	 * @param courseId        id of the course for which the salary should be counted
	 * @return teacher salary for the course
	 * @throws AccessDeniedException if the course doesn't belong to current teacher
	 */
	@Override
	public Double countSalary(String currentUsername, Long courseId) {
		User user = userDao.getUserByUsername(currentUsername);
		Course course = courseDao.getById(courseId);
		if (course.getTeacher().getId().equals(user.getId())) {
			return lessonDao.getSalaryForCourse(course.getLessons().stream().map(Lesson::getId).collect(Collectors.toList()));
		}
		throw new AccessDeniedException("You don't have a course with id " + courseId);
	}

	/**
	 * @param currentUsername username of current user (the user is a teacher or an admin)
	 * @param courseId        id of the course that should change its status according to lessons that belong to it
	 */
	@Override
	public void updateCourseStatuses(String currentUsername, Long courseId) {
		User teacher = userDao.getUserByUsername(currentUsername);
		Course course = courseDao.getById(courseId);
		if (!course.getTeacher().getId().equals(teacher.getId()) && !RoleType.ADMIN.equals(teacher.getRole().getName())) {
			throw new AccessDeniedException("You don't have a course with id " + courseId);
		}
		List<LessonStatus> statuses = course.getLessons().stream().map(Lesson::getStatus).collect(Collectors.toList());
		if (statuses.stream().allMatch(lessonStatus -> lessonStatus.equals(statuses.get(0)))) {
			if (!course.getStatus().equals(statuses.get(0))) {
				course.setStatus(statuses.get(0));
			}
		} else {
			Boolean hasNotStarted = statuses.stream().anyMatch(lessonStatus -> lessonStatus.equals(LessonStatus.NOT_STARTED));
			Boolean hasFinished = statuses.stream().anyMatch(lessonStatus -> lessonStatus.equals(LessonStatus.FINISHED));
			boolean hasInProgress = statuses.stream().anyMatch(lessonStatus -> lessonStatus.equals(LessonStatus.IN_PROGRESS));
			if (hasInProgress || (hasNotStarted && hasFinished)) {
				course.setStatus(LessonStatus.IN_PROGRESS);
			}
		}
	}

	/**
	 * @param currentUsername username of current user (the user is a teacher or an admin)
	 * @param currentTime     time according to which the lessons should change their statuses
	 * @param courseId        id of the course that the lessons belong to
	 */
	@Override
	public void updateLessonStatusesInCourse(String currentUsername, LocalDateTime currentTime, Long courseId) {
		if (currentTime == null) {
			currentTime = LocalDateTime.now();
		}
		User teacher = userDao.getUserByUsername(currentUsername);
		Course course = courseDao.getById(courseId);
		if (!course.getTeacher().getId().equals(teacher.getId()) && !RoleType.ADMIN.equals(teacher.getRole().getName())) {
			throw new AccessDeniedException("You don't have a course with id " + courseId);
		}
		for (Lesson lesson : course.getLessons()) {
			lessonDao.updateLessonStatus(lesson.getId(), currentTime);
		}
	}

	/**
	 * @param courseId id of the course that the lessons belong to
	 * @return lessons that belong to given course
	 */
	@Override
	public List<Lesson> getCourseLessons(Long courseId) {
		Course course = courseDao.getById(courseId);
		return course.getLessons();
	}
}
