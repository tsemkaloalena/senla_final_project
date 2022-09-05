package com.tsemkalo.experienceApp.impl;

import com.tsemkalo.experienceApp.CourseDao;
import com.tsemkalo.experienceApp.CourseService;
import com.tsemkalo.experienceApp.LessonDao;
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
import org.springframework.transaction.annotation.Transactional;

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

	@Override
	public Predicate getTeacherPredicate(String teacherName, String teacherSurname) {
		return courseDao.getTeacherPredicate(teacherName, teacherSurname);
	}

	@Override
	public Predicate getThemePredicate(String theme) {
		return courseDao.getThemePredicate(theme);
	}

	@Override
	public Predicate getRatingPredicate(Double minRating) {
		return lessonDao.getRatingPredicate(minRating);
	}

	@Override
	public Predicate getOnlineOfflinePredicate(Boolean online, String address) {
		return courseDao.getOnlineOfflinePredicate(online, address);
	}

	@Override
	public Predicate getCostPredicate(Boolean free, Integer minCost, Integer maxCost) {
		return courseDao.getCostPredicate(free, minCost, maxCost);
	}

	@Override
	public Predicate getIndividualPredicate(Boolean individual) {
		return courseDao.getIndividualPredicate(individual);
	}

	@Override
	public Predicate getStatusPredicate(LessonStatus lessonStatus) {
		return courseDao.getStatusPredicate(lessonStatus);
	}

	@Override
	public Predicate getDatePredicate(LocalDateTime from, LocalDateTime to) {
		return courseDao.getDatePredicate(from, to);
	}

	@Override
	public Predicate getFreePlacesLeftPredicate(Integer numberOfPlaces) {
		return courseDao.getFreePlacesLeftPredicate(numberOfPlaces);
	}

	@Override
	public List<Course> getCourses(List<Predicate> predicates) {
		return courseDao.getCourses(predicates);
	}

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
		if (newCourse.getTeacher() != null && !newCourse.getTeacher().equals(teacher)) {
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
		if (newCourse.getTheme() != null) {
			course.setTheme(newCourse.getTheme());
		}
		if (newCourse.getSubject() != null) {
			course.setSubject(newCourse.getSubject());
		}
		if (newCourse.getStatus() != null) {
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
		if (newCourse.getAverageRating() != null && !newCourse.getAverageRating().equals(course.getAverageRating())) {
			throw new IncorrectDataException("You can not change rating, it is not fair");
		}
		return course;
	}

	@Override
	public String denyCourse(String currentUsername, Long courseId) {
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
		return "Lessons with id " + deniedLessons.stream().map(n -> String.valueOf(n.getId())).collect(Collectors.joining(", ")) + " are denied";
	}

	@Override
	public String deleteCourse(String currentUsername, Long courseId) {
		User teacher = userDao.getUserByUsername(currentUsername);
		Course course = courseDao.getById(courseId);
		if (!RoleType.ADMIN.equals(teacher.getRole().getName()) && !teacher.getId().equals(course.getTeacher().getId())) {
			throw new AccessDeniedException("This course belongs to other teacher");
		}
		if (course.getLessons().stream().anyMatch(lesson -> LessonStatus.IN_PROGRESS.equals(lesson.getStatus()))) {
			throw new DenyLessonException("course with id " + courseId, "This course has in progress lessons");
		}
		String deniedLessons = course.getLessons().stream().filter(lesson -> LessonStatus.NOT_STARTED.equals(lesson.getStatus())).map(n -> String.valueOf(n.getId())).collect(Collectors.joining(", "));
		courseDao.deleteById(courseId);
		if ("".equals(deniedLessons)) {
			return null;
		}
		return "Lessons with id " + deniedLessons + " are deleted";
	}

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

	@Override
	public List<Course> getSubscriptions(String currentUsername) {
		User student = userDao.getUserByUsername(currentUsername);
		return subscriptionDao.getSubscriptionCourses(student.getId());
	}

	@Override
	public Double countSalary(String currentUsername, Long courseId) {
		User user = userDao.getUserByUsername(currentUsername);
		Course course = courseDao.getById(courseId);
		if (course.getTeacher().getId().equals(user.getId())) {
			return course.getSalary();
		}
		throw new AccessDeniedException("You don't have a course with id " + courseId);
	}

	@Override
	public void updateCourseStatuses(String currentUsername) {
		User teacher = userDao.getUserByUsername(currentUsername);
		for (Course course : courseDao.getAll()) {
			if (!course.getTeacher().getId().equals(teacher.getId())) {
				continue;
			}
			List<LessonStatus> statuses = course.getLessons().stream().map(Lesson::getStatus).collect(Collectors.toList());
			if (statuses.stream().allMatch(lessonStatus -> lessonStatus.equals(statuses.get(0)))) {
				if (!course.getStatus().equals(statuses.get(0))) {
					course.setStatus(statuses.get(0));
				}
			} else {
				Boolean hasNotStarted = statuses.stream().anyMatch(lessonStatus -> lessonStatus.equals(LessonStatus.NOT_STARTED));
				Boolean hasFinished = statuses.stream().anyMatch(lessonStatus -> lessonStatus.equals(LessonStatus.FINISHED));
				Boolean hasInProgress = statuses.stream().anyMatch(lessonStatus -> lessonStatus.equals(LessonStatus.IN_PROGRESS));
				if (hasInProgress || (hasNotStarted && hasFinished)) {
					course.setStatus(LessonStatus.IN_PROGRESS);
				}
			}
		}
	}

	@Transactional
	@Override
	public List<Lesson> getCourseLessons(Long courseId) {
		Course course = courseDao.getById(courseId);
		return course.getLessons();
	}
}
