package com.tsemkalo.senlaExperienceExchangeApp.service.impl;

import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Course;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Lesson;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Review;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Role;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Subscription;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.User;
import com.tsemkalo.senlaExperienceExchangeApp.dao.impl.CourseDaoImpl;
import com.tsemkalo.senlaExperienceExchangeApp.dao.impl.LessonDaoImpl;
import com.tsemkalo.senlaExperienceExchangeApp.dao.impl.ReviewDaoImpl;
import com.tsemkalo.senlaExperienceExchangeApp.dao.impl.SubscriptionDaoImpl;
import com.tsemkalo.senlaExperienceExchangeApp.dao.impl.UserDaoImpl;
import com.tsemkalo.senlaExperienceExchangeApp.enums.LessonStatus;
import com.tsemkalo.senlaExperienceExchangeApp.enums.OnlineType;
import com.tsemkalo.senlaExperienceExchangeApp.enums.RoleType;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.NotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;

@Setter
@Getter
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractServiceTest {
	@Mock
	private CourseDaoImpl courseDao;
	@Mock
	private LessonDaoImpl lessonDao;
	@Mock
	private ReviewDaoImpl reviewDao;
	@Mock
	private SubscriptionDaoImpl subscriptionDao;
	@Mock
	private UserDaoImpl userDao;
	@Spy
	private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

	private Map<Long, Course> courseTable = new HashMap<>();
	private Map<Long, Lesson> lessonTable = new HashMap<>();
	private Map<Long, Review> reviewTable = new HashMap<>();
	private Map<Long, Subscription> subscriptionTable = new HashMap<>();
	private Map<Long, User> userTable = new HashMap<>();

	private CommonMethodsSetUpper<User> userCommonMethodsSetUpper = new CommonMethodsSetUpper<>();
	private CommonMethodsSetUpper<Course> courseCommonMethodsSetUpper = new CommonMethodsSetUpper<>();
	private CommonMethodsSetUpper<Lesson> lessonCommonMethodsSetUpper = new CommonMethodsSetUpper<>();
	private CommonMethodsSetUpper<Review> reviewCommonMethodsSetUpper = new CommonMethodsSetUpper<>();
	private CommonMethodsSetUpper<Subscription> subscriptionCommonMethodsSetUpper = new CommonMethodsSetUpper<>();

	@BeforeAll
	public void fillDB() {
		fillUserTable();
		fillCourseTable();
		fillLessonTable();
	}

	@BeforeEach
	public void setup() {
		userDaoSetup();
		courseDaoSetup();
		lessonDaoSetup();
		reviewDaoSetup();
		subscriptionDaoSetup();
	}

	private void fillUserTable() {
		addUser(1L, "svetlana", "$2a$10$eHrKX3q0Gi.K3IXxDVSnmevyIf.LLq/DEAoHCMSCkIPDsOQsrix7W", "Svetlana", "Oleina", new Role(RoleType.ADMIN), "tsemkaloalena@gmail.com");
		addUser(2L, "amy", "$2a$10$uURzRp0Dr/iC5GEP3POsPeEvpF8GBfy1WNx0l2eNbM/WyvyrZHV4S", "Amy", "Norton", new Role(RoleType.TEACHER), "tsemkaloalena@gmail.com");
		addUser(3L, "rav", "$2a$10$K9VIDic.2ZRNlDUn5Ex8p.WztqJ9VnFz935itAylM3/zMCV9TGTjW", "Rav", "Ahuja", new Role(RoleType.TEACHER), "tsemkaloalena@gmail.com");
		addUser(4L, "robby", "$2a$10$YIePp00IQCDH5cSjZdcBAeBmKJUJ8QjaJh.bKjPA51kW30PBpic.K", "Robert", "Shiller", new Role(RoleType.TEACHER), "tsemkaloalena@gmail.com");
		addUser(5L, "nadya", "$2a$10$6pY.lRuSXtWLupFhyHzZD.uyoRojk6ecvKGBqlJ5WllPdfFdOE3Vi", "Nader", "Tavassoli", new Role(RoleType.TEACHER), "tsemkaloalena@gmail.com");
		addUser(6L, "rohit", "$2a$10$d4rss2UUzw1jY0m6m8eZxOIJSAwocb0oelOhpPTjHw1GW/1XlBw42", "Rohit", "Rahi", new Role(RoleType.TEACHER), "tsemkaloalena@gmail.com");
		addUser(7L, "pasha", "$2a$10$YAy0lNUFouUPvxJZoFaYR.uwV2uKNJz8hdbmJ9dA4M.LCJbgD/jXi", "Pavel", "Pevzner", new Role(RoleType.TEACHER), "tsemkaloalena@gmail.com");
		addUser(8L, "philya", "$2a$10$gLr2A2aU1w8C6JV2x0CZKuAtaUvbtHO.whMinTKaOnvx.ggTwPyme", "Phillip", "Compeau", new Role(RoleType.TEACHER), "tsemkaloalena@gmail.com");
		addUser(9L, "lisa", "$2a$10$bbP1/bhs/ByqDYsQtg.FFO3M7Qwoiug5p8cPhQWX2xqBtn7anYoOq", "Lisa", "Mazzola", new Role(RoleType.TEACHER), "tsemkaloalena@gmail.com");
		addUser(10L, "marik", "$2a$10$h2O/buH8cHiKx6eSgFuhs..c5fB15lu2Bi1SRSMpq3d8nTJBhJROW", "Marik", "Thewho", new Role(RoleType.STUDENT), "tsemkaloalena@gmail.com");
		addUser(11L, "edik", "$2a$10$5ufPvlwI5MBXQDa/7wIdsuHneB2/ppWSAn5cz5tmhA2NuMjwrg2DS", "Edik", "NotYarik", new Role(RoleType.STUDENT), "tsemkaloalena@gmail.com");
		addUser(12L, "derevo", "$2a$10$qOIhlqKOgDb0Ftrv5cn/zOGnPIa1Rv5p83TxhiHZOnOMORkXwBVuC", "Drevo", "Obrabotka", new Role(RoleType.STUDENT), "tsemkaloalena@gmail.com");
		addUser(13L, "sashenka", "$2a$10$bo4DgfkF4rXX2x0iO3Xn0eMvBGm4wOvxOkx0utSPrdnml2/V/r32.", "Danya", "Saharok", new Role(RoleType.STUDENT), "tsemkaloalena@gmail.com");
	}

	private void fillCourseTable() {
		addCourse(1L, "IT", "Google IT Support", LessonStatus.NOT_STARTED, OnlineType.ONLINE, null, userTable.get(2L), false, 20, LocalDateTime.parse("03.10.2022 15:40", dateTimeFormatter), LocalDateTime.parse("31.10.2022 15:40", dateTimeFormatter), "This is your path to a career in IT. In this program, you’ll learn in-demand skills that will have you job-ready in less than 6 months. No degree or experience required.", null);
		addCourse(2L, "IT", "Oracle Cloud Infrastructure Foundations", LessonStatus.NOT_STARTED, OnlineType.ONLINE, null, userTable.get(3L), false, 40, LocalDateTime.parse("10.09.2022 12:00", dateTimeFormatter), LocalDateTime.parse("01.10.2022 12:00", dateTimeFormatter), "Welcome to the course OCI Foundations Course. This course is a starting point to prepare you for the Oracle Cloud Infrastructure Foundations Associate Certification. Begin with an introduction of the OCI platform, and then dive into the core primitives, compute, storage, networking, identity, databases, security, and more.", null);
	}

	private void fillLessonTable() {
		addLesson(1L, "IT", "Technical Support Fundamentals", userTable.get(2L), courseTable.get(1L), LessonStatus.NOT_STARTED, null, false, null, "This course is the first of a series that aims to prepare you for a role as an entry-level IT Support Specialist. In this course, you’ll be introduced to the world of Information Technology, or IT. You’ll learn about the different facets of Information Technology, like computer hardware, the Internet, computer software, troubleshooting, and customer service. This course covers a wide variety of topics in IT that are designed to give you an overview of what’s to come in this certificate program.", 4, 60, LocalDateTime.parse("03.10.2022 15:40", dateTimeFormatter), 80, 70, null);
		addLesson(2L, "IT", "The Bits and Bytes of Computer Networking", userTable.get(2L), courseTable.get(1L), LessonStatus.NOT_STARTED, null, false, null, "This course is designed to provide a full overview of computer networking. We’ll cover everything from the fundamentals of modern networking technologies and protocols to an overview of the cloud to practical applications and network troubleshooting.", 4, 60, LocalDateTime.parse("10.10.2022 15:40", dateTimeFormatter), 80, 70, null);
		addLesson(3L, "IT", "Operating Systems and You: Becoming a Power User", userTable.get(2L), courseTable.get(1L), LessonStatus.NOT_STARTED, null, false, null, "In this course -- through a combination of video lectures, demonstrations, and hands-on practice -- you’ll learn about the main components of an operating system and how to perform critical tasks like managing software and users, and configuring hardware.", 4, 60, LocalDateTime.parse("17.10.2022 15:40", dateTimeFormatter), 80, 70, null);
		addLesson(4L, "IT", "System Administration and IT Infrastructure Services", userTable.get(2L), courseTable.get(1L), LessonStatus.NOT_STARTED, null, false, null, "This course will transition you from working on a single computer to an entire fleet. Systems administration is the field of IT that’s responsible for maintaining reliable computers systems in a multi-user environment. In this course, you’ll learn about the infrastructure services that keep all organizations, big and small, up and running. We’ll deep dive on cloud so that you’ll understand everything from typical cloud infrastructure setups to how to manage cloud resources. You'll also learn how to manage and configure servers and how to use industry tools to manage computers, user information, and user productivity. Finally, you’ll learn how to recover your organization’s IT infrastructure in the event of a disaster.", 4, 60, LocalDateTime.parse("24.10.2022 15:40", dateTimeFormatter), 80, 70, null);
		addLesson(5L, "IT", "IT Security: Defense against the digital dark arts", userTable.get(2L), courseTable.get(1L), LessonStatus.NOT_STARTED, null, false, null, "This course covers a wide variety of IT security concepts, tools, and best practices. It introduces threats and attacks and the many ways they can show up. We’ll give you some background of encryption algorithms and how they’re used to safeguard data. Then, we’ll dive into the three As of information security: authentication, authorization, and accounting. We’ll also cover network security solutions, ranging from firewalls to Wifi encryption options. The course is rounded out by putting all these elements together into a multi-layered, in-depth security architecture, followed by recommendations on how to integrate a culture of security into your organization or team.", 4, 60, LocalDateTime.parse("31.10.2022 15:40", dateTimeFormatter), 80, 70, null);

		addLesson(6L, "IT", "Getting Started with OCI", userTable.get(3L), courseTable.get(2L), LessonStatus.NOT_STARTED, null, false, null, "Kick start your Cloud journey with a strong foundation in core concepts, architecture and features of Oracle Cloud Infrastructure (OCI)", 10, 107, LocalDateTime.parse("10.09.2022 12:00", dateTimeFormatter), 20, 75, null);
		addLesson(7L, "IT", "Compute,Storage and Database", userTable.get(3L), courseTable.get(2L), LessonStatus.NOT_STARTED, null, false, null, "This module focuses on the key features of Cloud. It gives an insight into the basic concepts for optimizing the use of these services.", 10, 108, LocalDateTime.parse("17.09.2022 12:00", dateTimeFormatter), 20, 75, null);
		addLesson(8L, "IT", "OCI Services", userTable.get(3L), courseTable.get(2L), LessonStatus.NOT_STARTED, null, false, null, "Oracle Cloud has a broad platform of cloud services to support a wide range of applications in a scalable environment. This module is a walkthrough of a few of the popular OCI services.", 10, 105, LocalDateTime.parse("24.09.2022 12:00", dateTimeFormatter), 20, 75, null);
		addLesson(9L, "IT", "OCI Security and Administration", userTable.get(3L), courseTable.get(2L), LessonStatus.NOT_STARTED, null, false, null, "This module focuses on the key features of Cloud. It gives an insight into the basic concepts for optimizing the use of these services.", 10, 73, LocalDateTime.parse("01.10.2022 12:00", dateTimeFormatter), 20, 75, null);
	}

	public void addUser(Long id, String username, String password, String name, String surname, Role role, String email) {
		User user = new User(username, password, name, surname, role, email);
		user.setId(id);
		user.setSubscriptions(new ArrayList<>());
		user.setReviews(new ArrayList<>());
		userTable.put(id, user);
	}

	public void addCourse(Long id, String theme, String subject, LessonStatus status, OnlineType online, String address, User teacher, Boolean individual, Integer cost, LocalDateTime startDate, LocalDateTime finishDate, String description, Double averageRating) {
		Course course = new Course(theme, subject, status, online, address, teacher, individual, cost, startDate, finishDate, description, averageRating);
		course.setId(id);
		courseTable.put(id, course);
		course.setReviews(new ArrayList<>());
		course.setLessons(new ArrayList<>());
	}

	public void addLesson(Long id, String theme, String subject, User teacher, Course course, LessonStatus status, String address, Boolean individual, Integer maxStudentsNumber, String description, Integer cost, Integer durability, LocalDateTime lessonDate, Integer fixedSalary, Integer unfixedSalary, Double averageRating) {
		Lesson lesson = new Lesson(theme, subject, teacher, course, status, address, individual, maxStudentsNumber, description, cost, durability, lessonDate, fixedSalary, unfixedSalary, averageRating);
		lesson.setId(id);
		lesson.setSubscriptions(new ArrayList<>());
		lesson.setReviews(new ArrayList<>());
		lessonTable.put(id, lesson);
		if (course != null) {
			courseTable.get(course.getId()).getLessons().add(lesson);
		}
	}

	public void addSubscription(Long id, Long userId, Long lessonId) {
		Subscription subscription = new Subscription(getUserTable().get(userId), getLessonTable().get(lessonId));
		subscription.setId(id);
		subscriptionTable.put(id, subscription);
		getLessonTable().get(lessonId).getSubscriptions().add(subscription);
		getUserTable().get(userId).getSubscriptions().add(subscription);
	}

	private void userDaoSetup() {
		userCommonMethodsSetUpper.getAllSetup(userTable, userDao);
		userCommonMethodsSetUpper.getByIdSetup(userTable, userDao);
		userCommonMethodsSetUpper.createSetup(userTable, userDao, User.class);
		userCommonMethodsSetUpper.deleteByIdSetup(userTable, userDao);
		lenient().doAnswer(invocationOnMock -> {
			String username = invocationOnMock.getArgument(0);
			for (User user : userTable.values()) {
				if (user.getUsername().equals(username)) {
					return user;
				}
			}
			return null;
		}).when(userDao).getUserByUsername(any(String.class));
		lenient().doAnswer(invocationOnMock -> {
			Long teacherId = invocationOnMock.getArgument(0);
			List<Lesson> lessons = new ArrayList<>();
			for (Lesson lesson : lessonTable.values()) {
				if (lesson.getTeacher().getId().equals(teacherId)) {
					lessons.add(lesson);
				}
			}
			return lessons;
		}).when(userDao).getTeacherLessons(any(Long.class));
		lenient().doCallRealMethod().when(userDao).studentScheduleOverlapsPresent(any(String.class), any(LocalDateTime.class), any(Integer.class));
		lenient().doCallRealMethod().when(userDao).teacherScheduleOverlapsPresent(any(String.class), any(LocalDateTime.class), any(Integer.class));
	}

	private void courseDaoSetup() {
		courseCommonMethodsSetUpper.getAllSetup(courseTable, courseDao);
		courseCommonMethodsSetUpper.getByIdSetup(courseTable, courseDao);
		courseCommonMethodsSetUpper.createSetup(courseTable, courseDao, Course.class);
		deleteCourseByIdSetup();
		lenient().doCallRealMethod().when(courseDao).updateCourseAccordingToLesson(any(Lesson.class));
		lenient().doCallRealMethod().when(courseDao).updateOnlineType(anyLong());
		lenient().doCallRealMethod().when(courseDao).countAverageRating(anyLong());
		lenient().doCallRealMethod().when(courseDao).countCost(anyLong());
		lenient().doCallRealMethod().when(courseDao).countFinishDate(anyLong());
		lenient().doCallRealMethod().when(courseDao).countStartDate(anyLong());
	}

	private void lessonDaoSetup() {
		lessonCommonMethodsSetUpper.getAllSetup(lessonTable, lessonDao);
		lessonCommonMethodsSetUpper.getByIdSetup(lessonTable, lessonDao);
		createLessonSetup();
		deleteLessonByIdSetup();
		lenient().doCallRealMethod().when(lessonDao).updateLessonStatus(anyLong(), any(LocalDateTime.class));
		lenient().doCallRealMethod().when(lessonDao).countAverageRating(anyLong());
		lenient().doCallRealMethod().when(lessonDao).getSalaryForLesson(anyLong());
		lenient().doCallRealMethod().when(lessonDao).getSalaryForCourse(anyList());
	}

	private void subscriptionDaoSetup() {
		subscriptionCommonMethodsSetUpper.getAllSetup(subscriptionTable, subscriptionDao);
		subscriptionCommonMethodsSetUpper.getByIdSetup(subscriptionTable, subscriptionDao);
		subscriptionCommonMethodsSetUpper.deleteByIdSetup(subscriptionTable, subscriptionDao);
		createSubscriptionSetup();
	}

	private void reviewDaoSetup() {
		reviewCommonMethodsSetUpper.getAllSetup(reviewTable, reviewDao);
		reviewCommonMethodsSetUpper.getByIdSetup(reviewTable, reviewDao);
		reviewCommonMethodsSetUpper.deleteByIdSetup(reviewTable, reviewDao);
		createReviewSetup();
	}

	private void deleteLessonByIdSetup() {
		lenient().doAnswer(invocationOnMock -> {
			Long id = invocationOnMock.getArgument(0);
			if (lessonTable.containsKey(id)) {
				Lesson lesson = lessonTable.get(id);
				lesson.getCourse().getLessons().remove(lesson);
				lessonTable.remove(id);
				lessonCommonMethodsSetUpper.getAllSetup(lessonTable, lessonDao);
				lessonCommonMethodsSetUpper.getByIdSetup(lessonTable, lessonDao);
			} else {
				throw new NotFoundException("Entity with id " + id);
			}
			return null;
		}).when(lessonDao).deleteById(any(Long.class));
	}

	private void deleteCourseByIdSetup() {
		lenient().doAnswer(invocationOnMock -> {
			Long id = invocationOnMock.getArgument(0);
			if (courseTable.containsKey(id)) {
				Course course = courseTable.get(id);
				List<Long> lessonIds = course.getLessons().stream().map(Lesson::getId).collect(Collectors.toList());
				for (Long lessonId : lessonIds) {
					lessonDao.deleteById(lessonId);
				}
				courseTable.remove(id);
				courseCommonMethodsSetUpper.getAllSetup(courseTable, courseDao);
				courseCommonMethodsSetUpper.getByIdSetup(courseTable, courseDao);
			} else {
				throw new NotFoundException("Entity with id " + id);
			}
			return null;
		}).when(courseDao).deleteById(any(Long.class));
	}

	private void createLessonSetup() {
		lenient().doAnswer(invocationOnMock -> {
			Lesson lesson = invocationOnMock.getArgument(0);
			Long id = (long) lessonTable.size() + 1;
			while (lessonTable.containsKey(id)) {
				id++;
			}
			lesson.setId(id);
			lesson.setSubscriptions(new ArrayList<>());
			lesson.setReviews(new ArrayList<>());
			lessonTable.put(id, lesson);

			if (lesson.getCourse() != null) {
				lesson.getCourse().getLessons().add(lesson);
			}
			lessonCommonMethodsSetUpper.getAllSetup(lessonTable, lessonDao);
			lessonCommonMethodsSetUpper.getByIdSetup(lessonTable, lessonDao);
			return lesson;
		}).when(lessonDao).create(any(Lesson.class));
	}

	private void createSubscriptionSetup() {
		lenient().doAnswer(invocationOnMock -> {
			Subscription subscription = invocationOnMock.getArgument(0);
			Long id = (long) subscriptionTable.size() + 1;
			while (subscriptionTable.containsKey(id)) {
				id++;
			}
			subscription.setId(id);
			subscriptionTable.put(id, subscription);

			lessonTable.get(subscription.getLesson().getId()).getSubscriptions().add(subscription);
			userTable.get(subscription.getUser().getId()).getSubscriptions().add(subscription);

			subscriptionCommonMethodsSetUpper.getAllSetup(subscriptionTable, subscriptionDao);
			subscriptionCommonMethodsSetUpper.getByIdSetup(subscriptionTable, subscriptionDao);
			return subscription;
		}).when(subscriptionDao).create(any(Subscription.class));
	}

	private void createReviewSetup() {
		lenient().doAnswer(invocationOnMock -> {
			Review review = invocationOnMock.getArgument(0);
			Long id = (long) reviewTable.size() + 1;
			while (reviewTable.containsKey(id)) {
				id++;
			}
			review.setId(id);
			reviewTable.put(id, review);

			userTable.get(review.getUser().getId()).getReviews().add(review);
			if (review.getLesson() != null) {
				lessonTable.get(review.getLesson().getId()).getReviews().add(review);
			}
			if (review.getCourse() != null) {
				courseTable.get(review.getCourse().getId()).getReviews().add(review);
			}
			reviewCommonMethodsSetUpper.getAllSetup(reviewTable, reviewDao);
			reviewCommonMethodsSetUpper.getByIdSetup(reviewTable, reviewDao);
			return review;
		}).when(reviewDao).create(any(Review.class));
	}
}
