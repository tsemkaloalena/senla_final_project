package com.tsemkalo.senlaExperienceExchangeApp.service.impl;

import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Course;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Lesson;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.User;
import com.tsemkalo.senlaExperienceExchangeApp.enums.LessonStatus;
import com.tsemkalo.senlaExperienceExchangeApp.enums.OnlineType;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.DenyLessonException;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.IncorrectDataException;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.LessonSubscriptionException;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.NotFoundException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CourseServiceImplTest extends AbstractServiceTest {
	@InjectMocks
	private CourseServiceImpl courseService = new CourseServiceImpl();

	@Test
	@Order(1)
	public void addCourse_givenCurrentUserIsAdmin_whenDataIsCorrect_thanSuccess() {
		String currentUsername = "svetlana";
		int courseNumber = getCourseTable().size();
		Course course = new Course("IT", "Technical Support Specialist (IBM)", LessonStatus.NOT_STARTED, OnlineType.ONLINE, null, getUserTable().get(3L), false, 77, LocalDateTime.parse("05.11.2022 12:00", getDateTimeFormatter()), LocalDateTime.parse("17.12.2022 12:00", getDateTimeFormatter()), "Launch your rewarding new career in tech. This program will prepare you with job-ready skills valued by employers in as little as 3 months. No degree or prior experience needed to get started.", null);
		course.setLessons(new ArrayList<>());

		courseService.addCourse(currentUsername, course);

		assertEquals(courseNumber + 1, getCourseTable().size());
		assertNotNull(course.getId());
		assertEquals(getCourseTable().get(course.getId()), course);

		addLesson(16L, "IT", "Introduction to Technical Support", getUserTable().get(3L), getCourseTable().get(3L), LessonStatus.NOT_STARTED, null, false, null, "Get an insider’s view into IT Support work. Learn about IT Support roles and levels, the support escalation matrix, ticketing systems, common support tools, and remote support software. Then, hear about career opportunities and career pathways from experienced, expert Information Technology professionals.", 11, 90, LocalDateTime.parse("05.11.2022 12:00", getDateTimeFormatter()), 30, 60, null);
		addLesson(17L, "IT", "Introduction to Hardware and Operating Systems", getUserTable().get(3L), getCourseTable().get(3L), LessonStatus.NOT_STARTED, null, false, null, "If you're ready to enter the world of Information Technology (IT), you need job-ready skills. This course enables you to develop the skills to work with computer hardware and operating systems, and is your first step to prepare for all types of tech related careers that require IT Fundamental skills.", 11, 90, LocalDateTime.parse("12.11.2022 12:00", getDateTimeFormatter()), 30, 60, null);
		addLesson(18L, "IT", "Introduction to Software, Programming, and Databases", getUserTable().get(3L), getCourseTable().get(3L), LessonStatus.NOT_STARTED, null, false, null, "There are many types of software and understanding software can be overwhelming. This course aims to help you understand more about the types of software and how to manage software from an information technology (IT) perspective. This course will help you understand the basics of software, cloud computing, web browsers, development and concepts of software, programming languages, and database fundamentals.", 11, 90, LocalDateTime.parse("19.11.2022 12:00", getDateTimeFormatter()), 30, 60, null);
		addLesson(19L, "IT", "Introduction to Networking and Storage", getUserTable().get(3L), getCourseTable().get(3L), LessonStatus.NOT_STARTED, null, false, null, "Gain skills that keep users connected. Learn how to diagnose and repair basic networking and security problems.", 11, 90, LocalDateTime.parse("26.11.2022 12:00", getDateTimeFormatter()), 30, 60, null);
		addLesson(20L, "IT", "Introduction to Cybersecurity Essentials", getUserTable().get(3L), getCourseTable().get(3L), LessonStatus.NOT_STARTED, null, false, null, "Build key skills needed to recognize common security threats and risks. Discover the characteristics of cyber-attacks and learn how organizations employ best practices to guard against them.", 11, 90, LocalDateTime.parse("03.12.2022 12:00", getDateTimeFormatter()), 30, 60, null);
		addLesson(21L, "IT", "Introduction to Cloud Computing", getUserTable().get(3L), getCourseTable().get(3L), LessonStatus.NOT_STARTED, null, false, null, "Even though this course does not require any prior cloud computing or programming experience, by the end of the course, you will have created your own account on IBM Cloud and gained some hands-on experience by provisioning a cloud service and working with it.", 11, 90, LocalDateTime.parse("10.12.2022 12:00", getDateTimeFormatter()), 30, 60, null);
		addLesson(22L, "IT", "Technical Support Case Studies and Capstone", getUserTable().get(3L), getCourseTable().get(3L), LessonStatus.NOT_STARTED, null, false, null, "This course gives you an opportunity to show what you’ve learned in the previous IT Technical Support professional certification courses, as well as helping you solidify abstract knowledge into applied skills you can use.", 11, 90, LocalDateTime.parse("17.12.2022 12:00", getDateTimeFormatter()), 30, 60, null);
	}

	@Test
	@Order(2)
	public void addCourse_givenCurrentUserIsTeacher_whenSetTeacherIsNotEqualToCurrent_thenAccessDeniedExceptionThrown() {
		String currentUsername = "rohit";
		Integer courseNumber = getCourseTable().size();
		Course course = new Course("Business", "Financial markets", LessonStatus.NOT_STARTED, OnlineType.ONLINE, null, getUserTable().get(4L), false, 105, LocalDateTime.parse("10.11.2022 13:00", getDateTimeFormatter()), LocalDateTime.parse("22.11.2022 13:00", getDateTimeFormatter()), "An overview of the ideas, methods, and institutions that permit human society to manage risks and foster enterprise.  Emphasis on financially-savvy leadership skills. Description of practices today and analysis of prospects for the future. Introduction to risk management and behavioral finance principles to understand the real-world functioning of securities, insurance, and banking industries.  The ultimate goal of this course is using such industries effectively and towards a better society.", null);
		course.setLessons(new ArrayList<>());

		assertThrows(AccessDeniedException.class, () -> courseService.addCourse(currentUsername, course));
		assertEquals(courseNumber, getCourseTable().size());
		assertNull(course.getId());
	}

	@Test
	@Order(3)
	public void addCourse_givenCurrentUserIsTeacher_whenSetTeacherIsEqualToCurrent_thenSuccess() {
		String currentUsername = "robby";
		int courseNumber = getCourseTable().size();
		Course course = new Course("Business", "Financial markets", LessonStatus.NOT_STARTED, OnlineType.ONLINE, null, getUserTable().get(4L), false, 105, LocalDateTime.parse("10.11.2022 13:00", getDateTimeFormatter()), LocalDateTime.parse("22.11.2022 13:00", getDateTimeFormatter()), "An overview of the ideas, methods, and institutions that permit human society to manage risks and foster enterprise.  Emphasis on financially-savvy leadership skills. Description of practices today and analysis of prospects for the future. Introduction to risk management and behavioral finance principles to understand the real-world functioning of securities, insurance, and banking industries.  The ultimate goal of this course is using such industries effectively and towards a better society.", null);
		course.setLessons(new ArrayList<>());

		courseService.addCourse(currentUsername, course);

		assertEquals(courseNumber + 1, getCourseTable().size());
		assertNotNull(course.getId());
		assertEquals(getCourseTable().get(course.getId()), course);

		addLesson(23L, "Business", "Module 1", getUserTable().get(4L), getCourseTable().get(4L), LessonStatus.NOT_STARTED, null, false, null, "Welcome to the course! In this opening module, you will learn the basics of financial markets, insurance, and CAPM (Capital Asset Pricing Model). This module serves as the foundation of this course.", 15, 184, LocalDateTime.parse("10.11.2022 13:00", getDateTimeFormatter()), 28, 75, null);
		addLesson(24L, "Business", "Module 2", getUserTable().get(4L), getCourseTable().get(4L), LessonStatus.NOT_STARTED, null, false, null, "In this next module, dive into some details of behavioral finance, forecasting, pricing, debt, and inflation.", 15, 139, LocalDateTime.parse("12.11.2022 13:00", getDateTimeFormatter()), 28, 75, null);
		addLesson(25L, "Business", "Module 3", getUserTable().get(4L), getCourseTable().get(4L), LessonStatus.NOT_STARTED, null, false, null, "Stocks, bonds, dividends, shares, market caps; what are these? Who needs them? Why? Module 3 explores these concepts, along with corporation basics and some basic financial markets history.", 15, 137, LocalDateTime.parse("14.11.2022 13:00", getDateTimeFormatter()), 28, 75, null);
		addLesson(26L, "Business", "Module 4", getUserTable().get(4L), getCourseTable().get(4L), LessonStatus.NOT_STARTED, null, false, null, "Take a look into the recent past, exploring recessions, bubbles, the mortgage crisis, and regulation.", 15, 189, LocalDateTime.parse("16.11.2022 13:00", getDateTimeFormatter()), 28, 75, null);
		addLesson(27L, "Business", "Module 5", getUserTable().get(4L), getCourseTable().get(4L), LessonStatus.NOT_STARTED, null, false, null, "Options and bond markets are explored in module 5, important components of financial markets.", 15, 125, LocalDateTime.parse("18.11.2022 13:00", getDateTimeFormatter()), 28, 75, null);
		addLesson(28L, "Business", "Module 6", getUserTable().get(4L), getCourseTable().get(4L), LessonStatus.NOT_STARTED, null, false, null, "In module 6, Professor Shiller introduces investment banking, underwriting processes, brokers, dealers, exchanges, and new innovations in financial markets.", 15, 141, LocalDateTime.parse("20.11.2022 13:00", getDateTimeFormatter()), 28, 75, null);
		addLesson(29L, "Business", "Module 7", getUserTable().get(4L), getCourseTable().get(4L), LessonStatus.NOT_STARTED, null, false, null, "Professor Shiller's final module includes lectures about nonprofits and corporations, and your career in finance.", 15, 194, LocalDateTime.parse("22.11.2022 13:00", getDateTimeFormatter()), 28, 75, null);
	}

	@Test
	@Order(4)
	public void addCourse_givenCurrentUserIsTeacher_whenTeacherIsNotSet_thenSuccess() {
		String currentUsername = "nadya";
		int courseNumber = getCourseTable().size();
		Course course = new Course("I am busy", "Brand Management", LessonStatus.DENIED, OnlineType.ONLINE, null, null, true, 50, LocalDateTime.parse("26.11.2022 11:00", getDateTimeFormatter()), LocalDateTime.parse("24.12.2022 11:00", getDateTimeFormatter()), "Small description", null);
		course.setLessons(new ArrayList<>());

		courseService.addCourse(currentUsername, course);

		assertEquals(courseNumber + 1, getCourseTable().size());
		assertNotNull(course.getId());
		assertEquals(getCourseTable().get(course.getId()), course);

		addLesson(30L, "Business", "Brand Purpose & Experience", getUserTable().get(5L), getCourseTable().get(5L), LessonStatus.NOT_STARTED, "Senate House, Malet St, London WC1E 7HU, Great Britain", false, 2, "Welcome to Module 1! In this module, we'll cover the following topics: Traditional notions of branding; Changing market conditions for brands; A new approach to branding. As well as the lecture videos, you will also be learning through interviews with brand practitioners such as Bethany Koby, Director of Technology Will Save us and David Kershaw, CEO of M&C Saatchi. There are optional readings to supplement your understanding, a quiz with 7 questions to test your learning and a peer review assignment based on a task connected to this module.", 12, 71, LocalDateTime.parse("26.11.2022 12:00", getDateTimeFormatter()), 22, 80, null);
		addLesson(31L, "Business", "Brand Design & Delivery", getUserTable().get(5L), getCourseTable().get(5L), LessonStatus.NOT_STARTED, "Senate House, Malet St, London WC1E 7HU, Great Britain", false, 2, "Welcome to Module 2! In this module, we'll cover the following topics:Brand experiences as the basis for differentiation; How to design brand experiences, as different from products and services; Pricing as a differentiating brand experience. As well as the lecture videos, you will also be learning through interviews with brand practitioner Hub van Bockel, an independent consultant and author and with Professor Bernd H. Schmitt of Columbia Business School. There are optional readings to supplement your understanding, a quiz with 6 questions to test your learning and a peer review assignment based on a task connected to this module.", 12, 98, LocalDateTime.parse("03.12.2022 12:00", getDateTimeFormatter()), 22, 80, null);
		addLesson(32L, "Business", "Brand Leadership and Alignment", getUserTable().get(5L), getCourseTable().get(5L), LessonStatus.NOT_STARTED, "Senate House, Malet St, London WC1E 7HU, Great Britain", false, 2, "Welcome to Module 3! In this module, we'll cover the following topics: Aligning the strategies for business, brand and behaviour; Strategic brand portfolio alignment; Delivering global brand alignment. As well as the lecture videos, you will also be learning through interviews with practitioners, Ije Nwokorie, CEO of Wolff Olins, Helen Casey and Henk Viljoen, both of Old Mutual and Keith Weed, CMO of Unilever. There are optional readings to supplement your understanding, a quiz with 7 questions to test your learning and a peer review assignment based on a task connected to this module.", 12, 131, LocalDateTime.parse("10.12.2022 12:00", getDateTimeFormatter()), 22, 80, null);
		addLesson(33L, "Business", "Brand Practices & Engagement", getUserTable().get(5L), getCourseTable().get(5L), LessonStatus.NOT_STARTED, "Senate House, Malet St, London WC1E 7HU, Great Britain", false, 2, "Welcome to Module 4! In this module, we'll cover the following topics:How to design human resource brand best-practices; A model for engaging employees with the brand; The ABCs of behavioural change. As well as the lecture videos, you will also be learning through interviews with practitioners Helen Edwards of Passionbrand, Richard Hytner of Saatchi & Saatchi, Tanya Truman and Nik Allebon of Lush. There are optional readings to supplement your understanding, a quiz with 5 questions to test your learning and a peer review assignment based on a task connected to this module.", 12, 129, LocalDateTime.parse("17.12.2022 12:00", getDateTimeFormatter()), 22, 80, null);
		addLesson(34L, "Business", "Brand Metrics & Returns", getUserTable().get(5L), getCourseTable().get(5L), LessonStatus.NOT_STARTED, "Senate House, Malet St, London WC1E 7HU, Great Britain", false, 2, "Welcome to Module 5! In this module, we'll cover the following topics: How brands create value; Why brand valuation is not the same as the value brands create; How to design a strategic and cross-functional brand dashboard. As well as the lecture videos, you will also be learning through interviews with David Haigh, CEO of Brand Finance. There are optional readings to supplement your understanding, a quiz with 6 questions to test your learning and a peer review assignment which asks you to reflect on your learning throughout the course.", 12, 96, LocalDateTime.parse("24.12.2022 12:00", getDateTimeFormatter()), 22, 80, null);
	}

	@Test
	@Order(5)
	public void addCourse_whenCourseHasId_thenIncorrectDataExceptionThrown() {
		String currentUsername = "nadya";
		Course course = new Course("I am busy", "Brand Management", LessonStatus.DENIED, OnlineType.ONLINE, null, null, true, 50, LocalDateTime.parse("26.11.2022 11:00", getDateTimeFormatter()), LocalDateTime.parse("24.12.2022 11:00", getDateTimeFormatter()), "Small description", null);
		course.setId(100L);

		assertThrows(IncorrectDataException.class, () -> courseService.addCourse(currentUsername, course));
	}

	@Test
	@Order(6)
	public void addCourse_givenCurrentUserIsAdmin_whenTeacherIsNotSet_thenIncorrectDataExceptionThrown() {
		String currentUsername = "svetlana";
		Course course = new Course("I am busy", "Brand Management", LessonStatus.DENIED, OnlineType.ONLINE, null, null, true, 50, LocalDateTime.parse("26.11.2022 11:00", getDateTimeFormatter()), LocalDateTime.parse("24.12.2022 11:00", getDateTimeFormatter()), "Small description", null);

		assertThrows(IncorrectDataException.class, () -> courseService.addCourse(currentUsername, course));
	}

	@Test
	@Order(7)
	public void editCourse_givenAllDataIsCorrect_whenAllInfoIsEdited_thenSuccess() {
		String currentUsername = "nadya";
		String oldTheme = getCourseTable().get(5L).getTheme();
		String oldSubject = getCourseTable().get(5L).getSubject();
		LessonStatus oldStatus = getCourseTable().get(5L).getStatus();
		OnlineType oldOnline = getCourseTable().get(5L).getOnline();
		String oldAddress = getCourseTable().get(5L).getAddress();
		Boolean oldIndividual = getCourseTable().get(5L).getIndividual();
		Integer oldCost = getCourseTable().get(5L).getCost();
		LocalDateTime oldStartDate = getCourseTable().get(5L).getStartDate();
		LocalDateTime oldFinishDate = getCourseTable().get(5L).getFinishDate();
		String oldDescription = getCourseTable().get(5L).getDescription();
		User oldTeacher = getCourseTable().get(5L).getTeacher();
		Double oldRating = getCourseTable().get(5L).getAverageRating();

		Course newCourse = new Course("Business", "Brand Management: Aligning Business", LessonStatus.NOT_STARTED, OnlineType.OFFLINE, "Senate House, Malet St, London WC1E 7HU, Great Britain", null, false, 60, LocalDateTime.parse("26.11.2022 12:00", getDateTimeFormatter()), LocalDateTime.parse("24.12.2022 12:00", getDateTimeFormatter()), "Professor Nader Tavassoli of London Business School contrasts traditional approaches to branding - where brands are a visual identity and a promise to customers - to brands as a customer experience delivered by the entire organisation. The course offers a brand workout for your own brands, as well as guest videos from leading branding professionals.", null);
		newCourse.setId(5L);
		courseService.editCourse(currentUsername, newCourse);

		assertNotEquals(oldTheme, getCourseTable().get(5L).getTheme());
		assertNotEquals(oldSubject, getCourseTable().get(5L).getSubject());
		assertNotEquals(oldStatus, getCourseTable().get(5L).getStatus());
		assertNotEquals(oldOnline, getCourseTable().get(5L).getOnline());
		assertNotEquals(oldAddress, getCourseTable().get(5L).getAddress());
		assertNotEquals(oldIndividual, getCourseTable().get(5L).getIndividual());
		assertNotEquals(oldCost, getCourseTable().get(5L).getCost());
		assertNotEquals(oldStartDate, getCourseTable().get(5L).getStartDate());
		assertNotEquals(oldFinishDate, getCourseTable().get(5L).getFinishDate());
		assertNotEquals(oldDescription, getCourseTable().get(5L).getDescription());
		assertEquals(oldTeacher, getCourseTable().get(5L).getTeacher());
		assertEquals(oldRating, getCourseTable().get(5L).getAverageRating());
	}

	@Test
	@Order(8)
	public void editCourse_givenAllDatIsCorrect_whenSomeEditedInfoIsCorrect_thenSuccess() {
		String currentUsername = "nadya";
		String subject = "Brand Management: Aligning Business, Brand and Behaviour";
		String oldSubject = getCourseTable().get(5L).getSubject();
		Course newCourse = new Course(null, subject, null, null, null, null, null, null, null, null, null, null);
		newCourse.setId(5L);

		courseService.editCourse(currentUsername, newCourse);

		assertEquals(subject, getCourseTable().get(5L).getSubject());
		assertNotEquals(oldSubject, getCourseTable().get(5L).getSubject());
		assertNotNull(getCourseTable().get(5L).getTheme());
	}

	@Test
	@Order(9)
	public void editCourse_givenCurrentUserIsTeacher_whenTeacherIsEdited_thenIncorrectDataExceptionThrown() {
		String currentUsername = "nadya";
		Course newCourse = new Course();
		newCourse.setTeacher(getUserTable().get(4L));
		newCourse.setId(5L);

		assertThrows(IncorrectDataException.class, () -> courseService.editCourse(currentUsername, newCourse));
	}

	@Test
	@Order(10)
	public void editCourse_whenRatingIsEdited_thenIncorrectDataExceptionThrown() {
		String currentUsername = "nadya";
		Course newCourse = new Course();
		newCourse.setAverageRating(5.0);
		newCourse.setId(5L);

		assertThrows(IncorrectDataException.class, () -> courseService.editCourse(currentUsername, newCourse));
	}

	@Test
	@Order(11)
	public void editCourse_givenCurrentUserIsAdmin_whenScheduleOverlapAppears_thenIncorrectDataExceptionThrown() {
		String currentUsername = "svetlana";
		Course newCourse = new Course();
		newCourse.setTeacher(getUserTable().get(3L));
		newCourse.setId(5L);

		assertThrows(IncorrectDataException.class, () -> courseService.editCourse(currentUsername, newCourse));
	}

	@Test
	@Order(12)
	public void editCourse_givenCurrentUserIsAdmin_whenAllDataIsCorrect_thenSuccess() {
		String currentUsername = "svetlana";
		Course newCourse = new Course();
		newCourse.setTeacher(getUserTable().get(4L));
		newCourse.setId(5L);

		courseService.editCourse(currentUsername, newCourse);

		assertEquals(getCourseTable().get(5L).getTeacher(), getUserTable().get(4L));
		assertEquals(getLessonTable().get(34L).getTeacher(), getCourseTable().get(5L).getTeacher());
	}

	@Test
	@Order(13)
	public void editCourse_givenCurrentUserIsTeacher_whenSetTeacherIsNotEqualToCurrent_thenAccessDeniedExceptionThrown() {
		String currentUsername = "nadya";
		Course newCourse = new Course();
		newCourse.setTeacher(getUserTable().get(3L));
		newCourse.setId(5L);

		assertThrows(AccessDeniedException.class, () -> courseService.editCourse(currentUsername, newCourse));
	}

	@Test
	@Order(14)
	public void editCourse_whenCourseIdIsNotSet_thenIncorrectDataExceptionThrown() {
		String currentUsername = "robby";
		Course newCourse = new Course();
		newCourse.setTheme("Some new theme");

		assertThrows(IncorrectDataException.class, () -> courseService.editCourse(currentUsername, newCourse));
	}

	@Test
	@Order(15)
	public void subscribe_whenAllDataIsCorrect_thenSuccess() {
		String currentUsername = "sashenka";
		Long courseId = 5L;

		assertTrue(courseService.subscribe(currentUsername, courseId));

		assertEquals(30L, getSubscriptionTable().get(1L).getLesson().getId());
		assertEquals(31L, getSubscriptionTable().get(2L).getLesson().getId());
		assertEquals(32L, getSubscriptionTable().get(3L).getLesson().getId());
		assertEquals(33L, getSubscriptionTable().get(4L).getLesson().getId());
		assertEquals(34L, getSubscriptionTable().get(5L).getLesson().getId());
	}

	@Test
	@Order(16)
	public void subscribe_whenUserIsAlreadySubscribed_thenFalseReturned() {
		String currentUsername = "sashenka";
		Long courseId = 5L;

		when(getSubscriptionDao().getSubscription(13L, 30L)).thenReturn(getSubscriptionTable().get(1L));

		assertFalse(courseService.subscribe(currentUsername, courseId));
	}

	@Test
	@Order(17)
	public void subscribe_whenNoFreePlacesLeft_thenLessonSubscriptionExceptionThrown() {
		String currentUsername = "derevo";
		Long courseId = 5L;

		courseService.subscribe("edik", courseId);

		assertThrows(LessonSubscriptionException.class, () -> courseService.subscribe(currentUsername, courseId));
	}

	@Test
	@Order(18)
	public void subscribe_whenScheduleOverlapAppears_thenLessonSubscriptionExceptionThrown() {
		String currentUsername = "sashenka";
		Long courseId = 3L;

		assertThrows(LessonSubscriptionException.class, () -> courseService.subscribe(currentUsername, courseId));
	}

	@Test
	@Order(19)
	public void unsubscribe_whenUserIsNotSubscribed_thanFalseReturned() {
		String currentUsername = "sashenka";
		Long courseId = 1L;

		assertFalse(courseService.unsubscribe(currentUsername, courseId));
	}

	@Test
	@Order(20)
	public void unsubscribe_whenUserWasSubscribed_thenSuccess() {
		String currentUsername = "sashenka";
		Long courseId = 5L;
		when(getSubscriptionDao().getSubscription(13L, 30L)).thenReturn(getSubscriptionTable().get(1L));
		when(getSubscriptionDao().getSubscription(13L, 31L)).thenReturn(getSubscriptionTable().get(2L));
		when(getSubscriptionDao().getSubscription(13L, 32L)).thenReturn(getSubscriptionTable().get(3L));
		when(getSubscriptionDao().getSubscription(13L, 33L)).thenReturn(getSubscriptionTable().get(4L));
		when(getSubscriptionDao().getSubscription(13L, 34L)).thenReturn(getSubscriptionTable().get(5L));

		assertTrue(courseService.unsubscribe(currentUsername, courseId));

		assertFalse(getUserTable().get(13L).getSubscriptions().contains(getSubscriptionTable().get(1L)));
		assertFalse(getUserTable().get(13L).getSubscriptions().contains(getSubscriptionTable().get(2L)));
		assertFalse(getUserTable().get(13L).getSubscriptions().contains(getSubscriptionTable().get(3L)));
		assertFalse(getUserTable().get(13L).getSubscriptions().contains(getSubscriptionTable().get(4L)));
		assertFalse(getUserTable().get(13L).getSubscriptions().contains(getSubscriptionTable().get(5L)));
		assertFalse(getLessonTable().get(30L).getSubscriptions().contains(getSubscriptionTable().get(1L)));
		assertFalse(getLessonTable().get(31L).getSubscriptions().contains(getSubscriptionTable().get(2L)));
		assertFalse(getLessonTable().get(32L).getSubscriptions().contains(getSubscriptionTable().get(3L)));
		assertFalse(getLessonTable().get(33L).getSubscriptions().contains(getSubscriptionTable().get(4L)));
		assertFalse(getLessonTable().get(34L).getSubscriptions().contains(getSubscriptionTable().get(5L)));
	}

	@Test
	@Order(21)
	public void countSalary_whenCourseDoesNotBelongToUser_thenAccessDeniedExceptionThrown() {
		String currentUsername = "nadya";
		Long courseId = 5L;

		assertThrows(AccessDeniedException.class, () -> courseService.countSalary(currentUsername, courseId));
	}

	@Test
	@Order(22)
	public void countSalary_whenCourseBelongsToUser_thenSuccess() {
		String currentUsername = "robby";
		Long courseId = 5L;

		assertEquals(48.0, courseService.countSalary(currentUsername, courseId));
	}

	@Test
	@Order(23)
	public void updateCourseStatuses_whenAllLessonsAreSame_thenCourseStatusesAreEqualToLessonStatuses() {
		String currentUsername = "rav";
		getCourseTable().get(2L).setStatus(LessonStatus.IN_PROGRESS);
		getCourseTable().get(3L).setStatus(LessonStatus.IN_PROGRESS);

		getLessonTable().get(16L).setStatus(LessonStatus.NOT_STARTED);
		getLessonTable().get(17L).setStatus(LessonStatus.NOT_STARTED);
		getLessonTable().get(18L).setStatus(LessonStatus.NOT_STARTED);
		getLessonTable().get(19L).setStatus(LessonStatus.NOT_STARTED);
		getLessonTable().get(20L).setStatus(LessonStatus.NOT_STARTED);
		getLessonTable().get(21L).setStatus(LessonStatus.NOT_STARTED);
		getLessonTable().get(22L).setStatus(LessonStatus.NOT_STARTED);

		getLessonTable().get(6L).setStatus(LessonStatus.FINISHED);
		getLessonTable().get(7L).setStatus(LessonStatus.FINISHED);
		getLessonTable().get(8L).setStatus(LessonStatus.FINISHED);
		getLessonTable().get(9L).setStatus(LessonStatus.FINISHED);

		courseService.updateCourseStatuses(currentUsername, 2L);
		courseService.updateCourseStatuses(currentUsername, 3L);

		assertEquals(LessonStatus.NOT_STARTED, getCourseTable().get(3L).getStatus());
		assertEquals(LessonStatus.FINISHED, getCourseTable().get(2L).getStatus());
	}

	@Test
	@Order(24)
	public void updateCourseStatuses_whenCourseHasInProgressLessons_thenStatusIsInProgress() {
		String currentUsername = "rav";
		getCourseTable().get(2L).setStatus(LessonStatus.NOT_STARTED);
		getLessonTable().get(6L).setStatus(LessonStatus.IN_PROGRESS);
		getLessonTable().get(7L).setStatus(LessonStatus.NOT_STARTED);
		getLessonTable().get(8L).setStatus(LessonStatus.NOT_STARTED);
		getLessonTable().get(9L).setStatus(LessonStatus.NOT_STARTED);

		courseService.updateCourseStatuses(currentUsername, 2L);

		assertEquals(LessonStatus.IN_PROGRESS, getCourseTable().get(2L).getStatus());
	}

	@Test
	@Order(25)
	public void updateCourseStatuses_whenCourseHasBothStartedAndFinishedLessons_thenStatusIsInProgress() {
		String currentUsername = "rav";
		getCourseTable().get(2L).setStatus(LessonStatus.NOT_STARTED);
		getLessonTable().get(6L).setStatus(LessonStatus.FINISHED);
		getLessonTable().get(7L).setStatus(LessonStatus.NOT_STARTED);
		getLessonTable().get(8L).setStatus(LessonStatus.NOT_STARTED);
		getLessonTable().get(9L).setStatus(LessonStatus.NOT_STARTED);

		courseService.updateCourseStatuses(currentUsername, 2L);

		assertEquals(LessonStatus.IN_PROGRESS, getCourseTable().get(2L).getStatus());
	}

	@Test
	@Order(26)
	public void updateLessonStatusesInCourse_givenCurrentTime_thenChangeStatuses() {
		String currentUsername = "rav";
		LocalDateTime now = LocalDateTime.parse("12.11.2022 13:00", getDateTimeFormatter());
		Long courseId = 3L;
		courseService.updateLessonStatusesInCourse(currentUsername, now, courseId);

		for (Lesson lesson : getCourseDao().getById(courseId).getLessons()) {
			if (!LessonStatus.DENIED.equals(lesson.getStatus())) {
				if (lesson.getLessonDate().isAfter(now)) {
					assertEquals(LessonStatus.NOT_STARTED, lesson.getStatus());
				} else if (lesson.getLessonDate().equals(now) || lesson.getLessonDate().isBefore(now) && lesson.getLessonDate().plusMinutes(lesson.getDurability()).isAfter(now)) {
					assertEquals(LessonStatus.IN_PROGRESS, lesson.getStatus());
				} else {
					assertEquals(LessonStatus.FINISHED, lesson.getStatus());
				}
			}
		}
	}

	@Test
	@Order(27)
	public void denyCourse_givenCurrentUserIsTeacher_whenCourseDoesNotBelongToUser_thenAccessDeniedExceptionThrown() {
		String currentUsername = "nadya";
		Long courseId = 5L;
		for (Lesson lesson : getLessonTable().values()) {
			lesson.setStatus(LessonStatus.NOT_STARTED);
		}
		for (Course course : getCourseTable().values()) {
			course.setStatus(LessonStatus.NOT_STARTED);
		}

		assertThrows(AccessDeniedException.class, () -> courseService.denyCourse(currentUsername, courseId));
	}

	@Test
	@Order(28)
	public void denyCourse_whenCourseContainsInProgressLesson_thenDenyLessonExceptionThrown() {
		String currentUsername = "robby";
		Long courseId = 5L;
		getLessonTable().get(30L).setStatus(LessonStatus.IN_PROGRESS);

		assertThrows(DenyLessonException.class, () -> courseService.denyCourse(currentUsername, courseId));
	}

	@Test
	@Order(29)
	public void denyCourse_givenCurrentUserIsAdmin_whenAllDataIsCorrect_thenSuccess() {
		String currentUsername = "svetlana";
		Long courseId = 5L;
		getLessonTable().get(30L).setStatus(LessonStatus.FINISHED);

		courseService.denyCourse(currentUsername, courseId);

		assertEquals(LessonStatus.DENIED, getCourseTable().get(5L).getStatus());
	}

	@Test
	@Order(30)
	public void denyCourse_givenCurrentUserIsTeacher_whenCourseHasNotStartedLessons_thenLessonsAreDenied() {
		String currentUsername = "robby";
		Long courseId = 5L;
		getLessonTable().get(30L).setStatus(LessonStatus.FINISHED);
		getLessonTable().get(31L).setStatus(LessonStatus.NOT_STARTED);
		getLessonTable().get(32L).setStatus(LessonStatus.NOT_STARTED);
		getLessonTable().get(33L).setStatus(LessonStatus.NOT_STARTED);
		getLessonTable().get(34L).setStatus(LessonStatus.NOT_STARTED);
		getCourseTable().get(5L).setStatus(LessonStatus.NOT_STARTED);

		List<Long> message = courseService.denyCourse(currentUsername, courseId);

		assertNotNull(message);
		assertEquals(LessonStatus.FINISHED, getLessonTable().get(30L).getStatus());
		assertEquals(LessonStatus.DENIED, getLessonTable().get(31L).getStatus());
		assertEquals(LessonStatus.DENIED, getLessonTable().get(32L).getStatus());
		assertEquals(LessonStatus.DENIED, getLessonTable().get(33L).getStatus());
		assertEquals(LessonStatus.DENIED, getLessonTable().get(34L).getStatus());
		assertEquals(LessonStatus.DENIED, getCourseTable().get(5L).getStatus());
	}

	@Test
	@Order(31)
	public void denyCourse_givenCurrentUserIsTeacher_whenCourseDoesNotHaveNotStartedLessons_thenLessonsAreNotChanged() {
		String currentUsername = "robby";
		Long courseId = 5L;
		getLessonTable().get(30L).setStatus(LessonStatus.FINISHED);
		getLessonTable().get(31L).setStatus(LessonStatus.FINISHED);
		getLessonTable().get(32L).setStatus(LessonStatus.FINISHED);
		getLessonTable().get(33L).setStatus(LessonStatus.FINISHED);
		getLessonTable().get(34L).setStatus(LessonStatus.FINISHED);
		getCourseTable().get(5L).setStatus(LessonStatus.IN_PROGRESS);

		List<Long> message = courseService.denyCourse(currentUsername, courseId);

		assertNull(message);
		assertEquals(LessonStatus.FINISHED, getLessonTable().get(30L).getStatus());
		assertEquals(LessonStatus.FINISHED, getLessonTable().get(31L).getStatus());
		assertEquals(LessonStatus.FINISHED, getLessonTable().get(32L).getStatus());
		assertEquals(LessonStatus.FINISHED, getLessonTable().get(33L).getStatus());
		assertEquals(LessonStatus.FINISHED, getLessonTable().get(34L).getStatus());
		assertEquals(LessonStatus.DENIED, getCourseTable().get(5L).getStatus());
	}

	@Test
	@Order(32)
	public void denyCourse_whenCourseIsAlreadyDenied_thenDenyLessonExceptionThrown() {
		String currentUsername = "robby";
		Long courseId = 5L;
		getCourseTable().get(courseId).setStatus(LessonStatus.DENIED);

		assertThrows(DenyLessonException.class, () -> courseService.denyCourse(currentUsername, courseId));
	}

	@Test
	@Order(33)
	public void denyCourse_whenCourseIsAlreadyFinished_thenDenyLessonExceptionThrown() {
		String currentUsername = "robby";
		Long courseId = 5L;
		getCourseTable().get(courseId).setStatus(LessonStatus.FINISHED);

		assertThrows(DenyLessonException.class, () -> courseService.denyCourse(currentUsername, courseId));
	}

	@Test
	@Order(34)
	public void deleteCourse_whenCourseContainsInProgressLesson_thenDenyLessonExceptionThrown() {
		String currentUsername = "robby";
		Long courseId = 5L;
		getLessonTable().get(30L).setStatus(LessonStatus.IN_PROGRESS);
		getLessonTable().get(31L).setStatus(LessonStatus.NOT_STARTED);
		getLessonTable().get(32L).setStatus(LessonStatus.NOT_STARTED);
		getLessonTable().get(33L).setStatus(LessonStatus.NOT_STARTED);
		getLessonTable().get(34L).setStatus(LessonStatus.NOT_STARTED);
		getCourseTable().get(5L).setStatus(LessonStatus.IN_PROGRESS);

		assertThrows(DenyLessonException.class, () -> courseService.deleteCourse(currentUsername, courseId));
	}

	@Test
	@Order(35)
	public void deleteCourse_givenCurrentUserIsTeacher_whenCourseDoesNotBelongToUser_thenAccessDeniedExceptionThrown() {
		String currentUsername = "lisa";
		Long courseId = 5L;

		assertThrows(AccessDeniedException.class, () -> courseService.deleteCourse(currentUsername, courseId));
	}

	@Test
	@Order(36)
	public void deleteCourse_givenCurrentUserIsAdmin_whenCourseHasLessons_thenLessonsAreDeleted() {
		String currentUsername = "svetlana";
		Long courseId = 4L;

		List<Long> message = courseService.deleteCourse(currentUsername, courseId);

		assertNotNull(message);
		assertFalse(getLessonTable().containsKey(23L));
		assertFalse(getLessonTable().containsKey(24L));
		assertFalse(getLessonTable().containsKey(25L));
		assertFalse(getLessonTable().containsKey(26L));
		assertFalse(getLessonTable().containsKey(27L));
		assertFalse(getLessonTable().containsKey(28L));
		assertFalse(getLessonTable().containsKey(29L));
	}

	@Test
	@Order(37)
	public void deleteCourse_whenCourseIsNotFound_thenNotFoundExceptionThrown() {
		String currentUsername = "svetlana";
		Long courseId = 4L;

		assertThrows(NotFoundException.class, () -> courseService.deleteCourse(currentUsername, courseId));
	}
}