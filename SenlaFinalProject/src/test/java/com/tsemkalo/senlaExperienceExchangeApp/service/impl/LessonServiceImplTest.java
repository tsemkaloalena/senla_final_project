package com.tsemkalo.senlaExperienceExchangeApp.service.impl;

import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Course;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Lesson;
import com.tsemkalo.senlaExperienceExchangeApp.enums.LessonStatus;
import com.tsemkalo.senlaExperienceExchangeApp.enums.OnlineType;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.DenyLessonException;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.IncorrectDataException;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.LessonSubscriptionException;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
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
public class LessonServiceImplTest extends AbstractServiceTest {
	@InjectMocks
	private LessonServiceImpl lessonService = new LessonServiceImpl();

	@BeforeAll
	public void init() {
		addCourse(3L, "IT", "Technical Support Specialist (IBM)", LessonStatus.NOT_STARTED, OnlineType.ONLINE, null, getUserTable().get(3L), true, 77, LocalDateTime.parse("05.11.2022 12:00", getDateTimeFormatter()), LocalDateTime.parse("05.11.2022 12:00", getDateTimeFormatter()), "Launch your rewarding new career in tech. This program will prepare you with job-ready skills valued by employers in as little as 3 months. No degree or prior experience needed to get started.", null);
		addCourse(4L, "Business", "Financial markets", LessonStatus.NOT_STARTED, OnlineType.ONLINE, null, getUserTable().get(4L), false, 105, LocalDateTime.parse("10.11.2022 13:00", getDateTimeFormatter()), LocalDateTime.parse("22.11.2022 13:00", getDateTimeFormatter()), "An overview of the ideas, methods, and institutions that permit human society to manage risks and foster enterprise.  Emphasis on financially-savvy leadership skills. Description of practices today and analysis of prospects for the future. Introduction to risk management and behavioral finance principles to understand the real-world functioning of securities, insurance, and banking industries.  The ultimate goal of this course is using such industries effectively and towards a better society.", null);
		addCourse(5L, "Business", "Brand Management: Aligning Business, Brand and Behaviour", LessonStatus.NOT_STARTED, OnlineType.OFFLINE, "Senate House, Malet St, London WC1E 7HU, Great Britain", getUserTable().get(5L), false, 60, LocalDateTime.parse("26.11.2022 12:00", getDateTimeFormatter()), LocalDateTime.parse("24.12.2022 12:00", getDateTimeFormatter()), "Professor Nader Tavassoli of London Business School contrasts traditional approaches to branding - where brands are a visual identity and a promise to customers - to brands as a customer experience delivered by the entire organisation. The course offers a brand workout for your own brands, as well as guest videos from leading branding professionals.", null);
	}

	@Test
	@Order(1)
	public void addLesson_givenCurrentUserIsAdmin_whenDataIsCorrect_thanSuccess() {
		String currentUsername = "svetlana";
		int lessonsNumber = getLessonTable().size();
		Lesson lesson = new Lesson("IT", "Google Cloud Fundamentals: Core Infrastructure", getUserTable().get(6L), null, LessonStatus.NOT_STARTED, null, false, null, "Google Cloud Fundamentals: Core Infrastructure introduces important concepts and terminology for working with Google Cloud. Through videos and hands-on labs, this course presents and compares many of Google Cloud's computing and storage services, along with important resource and policy management tools.", 13, 90, LocalDateTime.parse("10.11.2022 13:20", getDateTimeFormatter()), 38, 85, null);

		lessonService.addLesson(currentUsername, lesson);

		assertEquals(lessonsNumber + 1, getLessonTable().size());
		assertNotNull(lesson.getId());
		assertEquals(getLessonTable().get(lesson.getId()), lesson);
	}

	@Test
	@Order(2)
	public void addLesson_givenCurrentUserIsTeacher_whenSetTeacherIsNotEqualToCurrent_thenAccessDeniedExceptionThrown() {
		String currentUsername = "robby";
		Integer lessonsNumber = getLessonTable().size();
		Lesson lesson = new Lesson("IT", "Crash Course on Python", getUserTable().get(2L), null, LessonStatus.NOT_STARTED, null, false, null, "Google Cloud Fundamentals: Core Infrastructure introduces important concepts and terminology for working with Google Cloud. Through videos and hands-on labs, this course presents and compares many of Google Cloud's computing and storage services, along with important resource and policy management tools.", 15, 90, LocalDateTime.parse("12.11.2022 13:20", getDateTimeFormatter()), 38, 85, null);

		assertThrows(AccessDeniedException.class, () -> lessonService.addLesson(currentUsername, lesson));

		assertEquals(lessonsNumber, getLessonTable().size());
		assertNull(lesson.getId());
		assertFalse(getLessonTable().containsValue(lesson));
	}

	@Test
	@Order(3)
	public void addLesson_givenCurrentUserIsTeacher_whenSetTeacherIsEqualToCurrent_thenSuccess() {
		String currentUsername = "amy";
		int lessonsNumber = getLessonTable().size();
		Lesson lesson = new Lesson("IT", "Crash Course on Python", getUserTable().get(2L), null, LessonStatus.NOT_STARTED, null, true, 1, "Google Cloud Fundamentals: Core Infrastructure introduces important concepts and terminology for working with Google Cloud. Through videos and hands-on labs, this course presents and compares many of Google Cloud's computing and storage services, along with important resource and policy management tools.", 15, 90, LocalDateTime.parse("12.11.2022 13:20", getDateTimeFormatter()), 38, 85, null);

		lessonService.addLesson(currentUsername, lesson);

		assertEquals(lessonsNumber + 1, getLessonTable().size());
		assertNotNull(lesson.getId());
		assertEquals(getLessonTable().get(lesson.getId()), lesson);
	}

	@Test
	@Order(4)
	public void addLesson_givenCurrentUserIsTeacher_whenTeacherIsNotSet_thenSuccess() {
		String currentUsername = "pasha";
		int lessonsNumber = getLessonTable().size();
		Lesson lesson = new Lesson("Bioinformatics", "Looking for hidden messages", getUserTable().get(7L), null, LessonStatus.NOT_STARTED, null, false, null, "This course begins a series of classes illustrating the power of computing in modern biology. Please join us on the frontier of bioinformatics to look for hidden messages in DNA without ever needing to put on a lab coat.", 16, 90, LocalDateTime.parse("12.11.2022 13:00", getDateTimeFormatter()), 38, 85, null);

		lessonService.addLesson(currentUsername, lesson);

		assertEquals(lessonsNumber + 1, getLessonTable().size());
		assertNotNull(lesson.getId());
		assertEquals(getLessonTable().get(lesson.getId()), lesson);
		assertEquals(getUserTable().get(7L), lesson.getTeacher());
	}

	@Test
	@Order(5)
	public void addLesson_whenLessonHasId_thenIncorrectDataExceptionThrown() {
		String currentUsername = "nadya";
		Integer lessonsNumber = getLessonTable().size();
		Lesson lesson = new Lesson("Bioinformatics", "Looking for hidden messages in DNA", getUserTable().get(7L), null, LessonStatus.NOT_STARTED, null, true, 1, "This course begins a series of classes illustrating the power of computing in modern biology. Please join us on the frontier of bioinformatics to look for hidden messages in DNA without ever needing to put on a lab coat.", 16, 90, LocalDateTime.parse("14.11.2022 13:20", getDateTimeFormatter()), 38, 85, null);
		lesson.setId(100L);

		assertThrows(IncorrectDataException.class, () -> lessonService.addLesson(currentUsername, lesson));

		assertEquals(lessonsNumber, getLessonTable().size());
		assertFalse(getLessonTable().containsKey(lesson.getId()));
		assertFalse(getLessonTable().containsValue(lesson));
	}

	@Test
	@Order(6)
	public void addLesson_whenCourseAndLessonHaveDifferentThemes_thenIncorrectDataExceptionThrown() {
		String currentUsername = "robby";
		Course course = getCourseTable().get(4L);
		Integer lessonsNumber = getLessonTable().size();
		Lesson lesson = new Lesson("IT", "Introduction to Technical Support", getUserTable().get(4L), course, LessonStatus.NOT_STARTED, null, false, null, "Get an insider’s view into IT Support work. Learn about IT Support roles and levels, the support escalation matrix, ticketing systems, common support tools, and remote support software. Then, hear about career opportunities and career pathways from experienced, expert Information Technology professionals.", 11, 90, LocalDateTime.parse("05.11.2022 12:00", getDateTimeFormatter()), 30, 60, null);

		assertThrows(IncorrectDataException.class, () -> lessonService.addLesson(currentUsername, lesson));

		assertEquals(lessonsNumber, getLessonTable().size());
		assertNull(lesson.getId());
		assertFalse(getLessonTable().containsValue(lesson));
	}

	@Test
	@Order(7)
	public void addLesson_whenCourseBelongsToOtherTeacher_thenAccessDeniedExceptionThrown() {
		String currentUsername = "rav";
		Course course = getCourseTable().get(4L);
		Integer lessonsNumber = getLessonTable().size();
		Lesson lesson = new Lesson("IT", "Introduction to Technical Support", getUserTable().get(3L), course, LessonStatus.NOT_STARTED, null, false, null, "Get an insider’s view into IT Support work. Learn about IT Support roles and levels, the support escalation matrix, ticketing systems, common support tools, and remote support software. Then, hear about career opportunities and career pathways from experienced, expert Information Technology professionals.", 11, 90, LocalDateTime.parse("05.11.2022 12:00", getDateTimeFormatter()), 30, 60, null);

		assertThrows(AccessDeniedException.class, () -> lessonService.addLesson(currentUsername, lesson));

		assertEquals(lessonsNumber, getLessonTable().size());
		assertNull(lesson.getId());
		assertFalse(getLessonTable().containsValue(lesson));
	}

	@Test
	@Order(8)
	public void addLesson_givenCourseIsIndividual_whenNewLessonIsGroup_thenIncorrectDataExceptionThrown() {
		String currentUsername = "rav";
		Course course = getCourseTable().get(3L);
		Integer lessonsNumber = getLessonTable().size();
		Lesson lesson = new Lesson("IT", "Introduction to Technical Support", getUserTable().get(3L), course, LessonStatus.NOT_STARTED, null, false, null, "Get an insider’s view into IT Support work. Learn about IT Support roles and levels, the support escalation matrix, ticketing systems, common support tools, and remote support software. Then, hear about career opportunities and career pathways from experienced, expert Information Technology professionals.", 11, 90, LocalDateTime.parse("05.11.2022 12:00", getDateTimeFormatter()), 30, 60, null);

		assertThrows(IncorrectDataException.class, () -> lessonService.addLesson(currentUsername, lesson));

		assertEquals(lessonsNumber, getLessonTable().size());
		assertNull(lesson.getId());
		assertFalse(getLessonTable().containsValue(lesson));
	}

	@Test
	@Order(9)
	public void addLesson_whenNewLessonDateIsLaterThanLastLessonInCourse_thenCourseFinishDateIsChanged() {
		String currentUsername = "rav";
		Course course = getCourseTable().get(3L);
		LocalDateTime oldCourseFinishDate = course.getFinishDate();
		Lesson lesson = new Lesson("IT", "Introduction to Technical Support", getUserTable().get(3L), course, LessonStatus.NOT_STARTED, null, true, 1, "Get an insider’s view into IT Support work. Learn about IT Support roles and levels, the support escalation matrix, ticketing systems, common support tools, and remote support software. Then, hear about career opportunities and career pathways from experienced, expert Information Technology professionals.", 11, 90, LocalDateTime.parse("05.11.2022 12:00", getDateTimeFormatter()), 30, 60, null);

		lessonService.addLesson(currentUsername, lesson);

		assertEquals(lesson.getLessonDate().plusMinutes(lesson.getDurability()), course.getFinishDate());
		assertNotEquals(oldCourseFinishDate, course.getFinishDate());
		assertTrue(getLessonTable().containsValue(lesson));
		assertEquals(getLessonTable().get(lesson.getId()), lesson);
	}

	@Test
	@Order(10)
	public void addLesson_givenCourseIsOnline_whenLessonIsOffline_thenCourseIsPartiallyOnline() {
		String currentUsername = "rav";
		Course course = getCourseTable().get(3L);
		OnlineType oldCourseOnlineType = course.getOnline();
		Lesson lesson = new Lesson("IT", "Introduction to Hardware and Operating Systems", getUserTable().get(3L), course, LessonStatus.NOT_STARTED, "Senate House, Malet St, London WC1E 7HU, Great Britain", true, 1, "If you're ready to enter the world of Information Technology (IT), you need job-ready skills. This course enables you to develop the skills to work with computer hardware and operating systems, and is your first step to prepare for all types of tech related careers that require IT Fundamental skills.", 11, 90, LocalDateTime.parse("12.11.2022 12:00", getDateTimeFormatter()), 30, 60, null);

		lessonService.addLesson(currentUsername, lesson);

		assertEquals(OnlineType.OFFLINE, lesson.getOnline());
		assertEquals(OnlineType.PARTIALLY_ONLINE, course.getOnline());
		assertNotEquals(oldCourseOnlineType, course.getOnline());
	}

	@Test
	@Order(11)
	public void editLesson_givenAllDataIsCorrect_whenSomeInfoIsEdited_thenSuccess() {
		String currentUsername = "pasha";
		Lesson editedLesson = new Lesson();
		editedLesson.setSubject("Looking for hidden messages in DNA");
		editedLesson.setFixedSalary(28);
		editedLesson.setMaxStudentsNumber(8);
		editedLesson.setId(12L);

		lessonService.editLesson(currentUsername, editedLesson);

		assertEquals(getLessonTable().get(12L).getSubject(), editedLesson.getSubject());
		assertEquals(getLessonTable().get(12L).getFixedSalary(), editedLesson.getFixedSalary());
		assertNotNull(getLessonTable().get(12L).getDescription());
		assertEquals(8, getLessonTable().get(12L).getMaxStudentsNumber());
		assertFalse(getLessonTable().get(12L).getIndividual());
	}

	@Test
	@Order(12)
	public void editLesson_givenCurrentUserIsTeacher_whenTeacherIsEdited_thenIncorrectDataExceptionThrown() {
		String currentUsername = "rav";
		Lesson editedLesson = new Lesson();
		editedLesson.setTeacher(getUserTable().get(5L));
		editedLesson.setId(14L);

		assertThrows(IncorrectDataException.class, () -> lessonService.editLesson(currentUsername, editedLesson));

		assertNotEquals(getUserTable().get(14L), getLessonTable().get(14L).getTeacher());
	}

	@Test
	@Order(13)
	public void editLesson_whenRatingIsEdited_thenIncorrectDataExceptionThrown() {
		String currentUsername = "rav";
		Lesson editedLesson = new Lesson();
		editedLesson.setAverageRating(5.0);
		editedLesson.setId(14L);

		assertThrows(IncorrectDataException.class, () -> lessonService.editLesson(currentUsername, editedLesson));

		assertNotEquals(5.0, getLessonTable().get(14L).getAverageRating());
	}

	private void fillLessons() {
		addLesson(15L, "IT", "Introduction to Software, Programming, and Databases", getUserTable().get(3L), getCourseTable().get(3L), LessonStatus.NOT_STARTED, null, true, 1, "There are many types of software and understanding software can be overwhelming. This course aims to help you understand more about the types of software and how to manage software from an information technology (IT) perspective. This course will help you understand the basics of software, cloud computing, web browsers, development and concepts of software, programming languages, and database fundamentals.", 11, 90, LocalDateTime.parse("19.11.2022 12:00", getDateTimeFormatter()), 30, 60, null);
		addLesson(16L, "IT", "Introduction to Networking and Storage", getUserTable().get(3L), getCourseTable().get(3L), LessonStatus.NOT_STARTED, null, true, 1, "Gain skills that keep users connected. Learn how to diagnose and repair basic networking and security problems.", 11, 90, LocalDateTime.parse("26.11.2022 12:00", getDateTimeFormatter()), 30, 60, null);
		addLesson(17L, "IT", "Introduction to Cybersecurity Essentials", getUserTable().get(3L), getCourseTable().get(3L), LessonStatus.NOT_STARTED, null, true, 1, "Build key skills needed to recognize common security threats and risks. Discover the characteristics of cyber-attacks and learn how organizations employ best practices to guard against them.", 11, 90, LocalDateTime.parse("03.12.2022 12:00", getDateTimeFormatter()), 30, 60, null);
		addLesson(18L, "IT", "Introduction to Cloud Computing", getUserTable().get(3L), getCourseTable().get(3L), LessonStatus.NOT_STARTED, null, true, 1, "Even though this course does not require any prior cloud computing or programming experience, by the end of the course, you will have created your own account on IBM Cloud and gained some hands-on experience by provisioning a cloud service and working with it.", 11, 90, LocalDateTime.parse("10.12.2022 12:00", getDateTimeFormatter()), 30, 60, null);
		addLesson(19L, "IT", "Technical Support Case Studies and Capstone", getUserTable().get(3L), getCourseTable().get(3L), LessonStatus.NOT_STARTED, null, true, 1, "This course gives you an opportunity to show what you’ve learned in the previous IT Technical Support professional certification courses, as well as helping you solidify abstract knowledge into applied skills you can use.", 11, 90, LocalDateTime.parse("17.12.2022 12:00", getDateTimeFormatter()), 30, 60, null);

		addLesson(20L, "Business", "Module 1", getUserTable().get(4L), getCourseTable().get(4L), LessonStatus.NOT_STARTED, null, false, null, "Welcome to the course! In this opening module, you will learn the basics of financial markets, insurance, and CAPM (Capital Asset Pricing Model). This module serves as the foundation of this course.", 15, 184, LocalDateTime.parse("10.11.2022 13:00", getDateTimeFormatter()), 28, null, null);
		addLesson(21L, "Business", "Module 2", getUserTable().get(4L), getCourseTable().get(4L), LessonStatus.NOT_STARTED, null, false, null, "In this next module, dive into some details of behavioral finance, forecasting, pricing, debt, and inflation.", 15, 139, LocalDateTime.parse("12.11.2022 13:00", getDateTimeFormatter()), null, 75, null);
		addLesson(22L, "Business", "Module 3", getUserTable().get(4L), getCourseTable().get(4L), LessonStatus.NOT_STARTED, null, false, null, "Stocks, bonds, dividends, shares, market caps; what are these? Who needs them? Why? Module 3 explores these concepts, along with corporation basics and some basic financial markets history.", 15, 137, LocalDateTime.parse("14.11.2022 13:00", getDateTimeFormatter()), 28, 75, null);
		addLesson(23L, "Business", "Module 4", getUserTable().get(4L), getCourseTable().get(4L), LessonStatus.NOT_STARTED, null, false, null, "Take a look into the recent past, exploring recessions, bubbles, the mortgage crisis, and regulation.", 15, 189, LocalDateTime.parse("16.11.2022 13:00", getDateTimeFormatter()), 28, 75, null);
		addLesson(24L, "Business", "Module 5", getUserTable().get(4L), getCourseTable().get(4L), LessonStatus.NOT_STARTED, null, false, null, "Options and bond markets are explored in module 5, important components of financial markets.", 15, 125, LocalDateTime.parse("18.11.2022 13:00", getDateTimeFormatter()), 28, 75, null);
		addLesson(25L, "Business", "Module 6", getUserTable().get(4L), getCourseTable().get(4L), LessonStatus.NOT_STARTED, null, false, null, "In module 6, Professor Shiller introduces investment banking, underwriting processes, brokers, dealers, exchanges, and new innovations in financial markets.", 15, 141, LocalDateTime.parse("20.11.2022 13:00", getDateTimeFormatter()), 28, 75, null);
		addLesson(26L, "Business", "Module 7", getUserTable().get(4L), getCourseTable().get(4L), LessonStatus.NOT_STARTED, null, false, null, "Professor Shiller's final module includes lectures about nonprofits and corporations, and your career in finance.", 15, 194, LocalDateTime.parse("22.11.2022 13:00", getDateTimeFormatter()), 28, 75, null);

		addLesson(27L, "Business", "Brand Purpose & Experience", getUserTable().get(5L), getCourseTable().get(5L), LessonStatus.NOT_STARTED, "Senate House, Malet St, London WC1E 7HU, Great Britain", false, null, "Welcome to Module 1! In this module, we'll cover the following topics: Traditional notions of branding; Changing market conditions for brands; A new approach to branding. As well as the lecture videos, you will also be learning through interviews with brand practitioners such as Bethany Koby, Director of Technology Will Save us and David Kershaw, CEO of M&C Saatchi. There are optional readings to supplement your understanding, a quiz with 7 questions to test your learning and a peer review assignment based on a task connected to this module.", 12, 71, LocalDateTime.parse("26.11.2022 12:00", getDateTimeFormatter()), 22, 80, null);
		addLesson(28L, "Business", "Brand Design & Delivery", getUserTable().get(5L), getCourseTable().get(5L), LessonStatus.NOT_STARTED, "Senate House, Malet St, London WC1E 7HU, Great Britain", false, null, "Welcome to Module 2! In this module, we'll cover the following topics:Brand experiences as the basis for differentiation; How to design brand experiences, as different from products and services; Pricing as a differentiating brand experience. As well as the lecture videos, you will also be learning through interviews with brand practitioner Hub van Bockel, an independent consultant and author and with Professor Bernd H. Schmitt of Columbia Business School. There are optional readings to supplement your understanding, a quiz with 6 questions to test your learning and a peer review assignment based on a task connected to this module.", 12, 98, LocalDateTime.parse("03.12.2022 12:00", getDateTimeFormatter()), 22, 80, null);
		addLesson(29L, "Business", "Brand Leadership and Alignment", getUserTable().get(5L), getCourseTable().get(5L), LessonStatus.NOT_STARTED, "Senate House, Malet St, London WC1E 7HU, Great Britain", false, null, "Welcome to Module 3! In this module, we'll cover the following topics: Aligning the strategies for business, brand and behaviour; Strategic brand portfolio alignment; Delivering global brand alignment. As well as the lecture videos, you will also be learning through interviews with practitioners, Ije Nwokorie, CEO of Wolff Olins, Helen Casey and Henk Viljoen, both of Old Mutual and Keith Weed, CMO of Unilever. There are optional readings to supplement your understanding, a quiz with 7 questions to test your learning and a peer review assignment based on a task connected to this module.", 12, 131, LocalDateTime.parse("10.12.2022 12:00", getDateTimeFormatter()), 22, 80, null);
		addLesson(30L, "Business", "Brand Practices & Engagement", getUserTable().get(5L), getCourseTable().get(5L), LessonStatus.NOT_STARTED, "Senate House, Malet St, London WC1E 7HU, Great Britain", false, null, "Welcome to Module 4! In this module, we'll cover the following topics:How to design human resource brand best-practices; A model for engaging employees with the brand; The ABCs of behavioural change. As well as the lecture videos, you will also be learning through interviews with practitioners Helen Edwards of Passionbrand, Richard Hytner of Saatchi & Saatchi, Tanya Truman and Nik Allebon of Lush. There are optional readings to supplement your understanding, a quiz with 5 questions to test your learning and a peer review assignment based on a task connected to this module.", 12, 129, LocalDateTime.parse("17.12.2022 12:00", getDateTimeFormatter()), 22, 80, null);
		addLesson(31L, "Business", "Brand Metrics & Returns", getUserTable().get(5L), getCourseTable().get(5L), LessonStatus.NOT_STARTED, "Senate House, Malet St, London WC1E 7HU, Great Britain", false, null, "Welcome to Module 5! In this module, we'll cover the following topics: How brands create value; Why brand valuation is not the same as the value brands create; How to design a strategic and cross-functional brand dashboard. As well as the lecture videos, you will also be learning through interviews with David Haigh, CEO of Brand Finance. There are optional readings to supplement your understanding, a quiz with 6 questions to test your learning and a peer review assignment which asks you to reflect on your learning throughout the course.", 12, 96, LocalDateTime.parse("24.12.2022 12:00", getDateTimeFormatter()), 22, 80, null);
	}

	@Test
	@Order(14)
	public void editLesson_givenCurrentUserIsAdmin_whenScheduleOverlapAppears_thenIncorrectDataExceptionThrown() {
		String currentUsername = "svetlana";
		fillLessons();
		Lesson editedLesson = new Lesson();
		editedLesson.setTeacher(getUserTable().get(5L));
		editedLesson.setId(16L);

		assertThrows(IncorrectDataException.class, () -> lessonService.editLesson(currentUsername, editedLesson));

		assertNotEquals(getUserTable().get(5L), getLessonTable().get(14L).getTeacher());
	}

	@Test
	@Order(15)
	public void editLesson_givenCurrentUserIsAdmin_whenAllDataIsCorrect_thenSuccess() {
		String currentUsername = "svetlana";
		Lesson editedLesson = new Lesson();
		editedLesson.setTeacher(getUserTable().get(4L));
		editedLesson.setId(10L);

		lessonService.editLesson(currentUsername, editedLesson);

		assertEquals(getLessonTable().get(10L).getTeacher(), getUserTable().get(4L));
	}

	@Test
	@Order(16)
	public void editLesson_givenCurrentUserIsTeacher_whenSetTeacherIsNotEqualToCurrent_thenAccessDeniedExceptionThrown() {
		String currentUsername = "nadya";
		Lesson editedLesson = new Lesson();
		editedLesson.setDescription("New description");
		editedLesson.setId(10L);

		assertThrows(AccessDeniedException.class, () -> lessonService.editLesson(currentUsername, editedLesson));
	}

	@Test
	@Order(17)
	public void editLesson_whenLessonIdIsNotSet_thenIncorrectDataExceptionThrown() {
		String currentUsername = "robby";
		Lesson editedLesson = new Lesson();
		editedLesson.setTheme("Some new theme");
		assertThrows(IncorrectDataException.class, () -> lessonService.editLesson(currentUsername, editedLesson));
	}

	@Test
	@Order(18)
	public void editLesson_whenNewLessonDateIsLaterThanLastLessonInCourse_thenCourseFinishDateIsChanged() {
		String currentUsername = "rav";
		Course course = getCourseTable().get(3L);
		LocalDateTime oldCourseFinishDate = course.getFinishDate();
		Lesson editedLesson = new Lesson();
		editedLesson.setDurability(60);
		editedLesson.setId(19L);

		lessonService.editLesson(currentUsername, editedLesson);

		assertEquals(getLessonTable().get(19L).getLessonDate().plusMinutes(getLessonTable().get(19L).getDurability()), course.getFinishDate());
		assertNotEquals(oldCourseFinishDate, course.getFinishDate());
	}

	@Test
	@Order(19)
	public void editLesson_whenLessonIsIndividual_thenMaxStudentNumberChanged() {
		String currentUsername = "svetlana";
		Lesson editedLesson = new Lesson();
		editedLesson.setIndividual(true);
		editedLesson.setId(10L);

		lessonService.editLesson(currentUsername, editedLesson);

		assertTrue(getLessonTable().get(10L).getIndividual());
		assertEquals(1, getLessonTable().get(10L).getMaxStudentsNumber());
	}

	@Test
	@Order(20)
	public void editLesson_whenSetMaxStudentNumber_thenTypeChangedToGroup() {
		String currentUsername = "svetlana";
		Lesson editedLesson = new Lesson();
		editedLesson.setMaxStudentsNumber(60);
		editedLesson.setId(10L);

		lessonService.editLesson(currentUsername, editedLesson);

		assertFalse(getLessonTable().get(10L).getIndividual());
		assertEquals(60, getLessonTable().get(10L).getMaxStudentsNumber());
	}

	@Test
	@Order(21)
	public void subscribe_whenAllDataIsCorrect_thenSuccess() {
		String currentUsername = "sashenka";
		Long lessonId = 11L;

		assertTrue(lessonService.subscribe(currentUsername, lessonId));

		assertEquals(11L, getSubscriptionTable().get(1L).getLesson().getId());
	}

	@Test
	@Order(22)
	public void subscribe_whenUserIsAlreadySubscribed_thenFalseReturned() {
		String currentUsername = "sashenka";
		Long lessonId = 11L;

		when(getSubscriptionDao().getSubscription(13L, 11L)).thenReturn(getSubscriptionTable().get(1L));

		assertFalse(lessonService.subscribe(currentUsername, lessonId));
	}

	@Test
	@Order(23)
	public void subscribe_whenNoFreePlacesLeft_thenLessonSubscriptionExceptionThrown() {
		String currentUsername = "derevo";
		Long lessonId = 11L;

		assertThrows(LessonSubscriptionException.class, () -> lessonService.subscribe(currentUsername, lessonId));

		assertEquals("sashenka", getSubscriptionTable().get(1L).getUser().getUsername());
		assertNotEquals("derevo", getSubscriptionTable().get(1L).getUser().getUsername());
	}

	@Test
	@Order(24)
	public void subscribe_whenScheduleOverlapAppears_thenLessonSubscriptionExceptionThrown() {
		String currentUsername = "sashenka";
		Long lessonId = 12L;

		assertThrows(LessonSubscriptionException.class, () -> lessonService.subscribe(currentUsername, lessonId));

		assertEquals(1, getSubscriptionTable().size());
	}

	@Test
	@Order(25)
	public void unsubscribe_whenUserIsNotSubscribed_thanFalseReturned() {
		String currentUsername = "sashenka";
		Long lessonId = 12L;

		assertFalse(lessonService.unsubscribe(currentUsername, lessonId));
	}

	@Test
	@Order(26)
	public void unsubscribe_whenUserWasSubscribed_thenSuccess() {
		String currentUsername = "sashenka";
		Long lessonId = 11L;
		when(getSubscriptionDao().getSubscription(13L, lessonId)).thenReturn(getSubscriptionTable().get(1L));

		assertTrue(lessonService.unsubscribe(currentUsername, lessonId));

		assertFalse(getUserTable().get(13L).getSubscriptions().contains(getSubscriptionTable().get(1L)));
	}

	@Test
	@Order(27)
	public void getSchedule_givenCurrentUserIsStudent_whenUserHasNoSubscriptions_thenEmptyListReturned() {
		String currentUsername = "sashenka";

		assertTrue(lessonService.getSchedule(currentUsername).isEmpty());
	}

	@Test
	@Order(28)
	public void getSchedule_givenCurrentUserIsStudent_whenUserHasSubscriptions_thenListOfFutureLessonsReturned() {
		String currentUsername = "sashenka";
		lessonService.subscribe(currentUsername, 20L);
		lessonService.subscribe(currentUsername, 21L);
		lessonService.subscribe(currentUsername, 22L);
		lessonService.subscribe("derevo", 23L);
		lessonService.subscribe("derevo", 24L);

		List<Lesson> schedule = lessonService.getSchedule(currentUsername);

		assertEquals(3, schedule.size());
		assertTrue(schedule.contains(getLessonTable().get(20L)));
		assertTrue(schedule.contains(getLessonTable().get(21L)));
		assertTrue(schedule.contains(getLessonTable().get(22L)));
	}

	@Test
	@Order(29)
	public void getSchedule_givenCurrentUserIsTeacher_whenUserHasLessons_thenListOfFutureLessonsReturned() {
		String currentUsername = "robby";

		List<Lesson> schedule = lessonService.getSchedule(currentUsername);

		assertEquals(8, schedule.size());
		assertTrue(schedule.contains(getLessonTable().get(10L)));
		assertTrue(schedule.contains(getLessonTable().get(20L)));
		assertTrue(schedule.contains(getLessonTable().get(21L)));
		assertTrue(schedule.contains(getLessonTable().get(22L)));
		assertTrue(schedule.contains(getLessonTable().get(23L)));
		assertTrue(schedule.contains(getLessonTable().get(24L)));
		assertTrue(schedule.contains(getLessonTable().get(25L)));
		assertTrue(schedule.contains(getLessonTable().get(26L)));
	}

	@Test
	@Order(30)
	public void countSalary_whenLessonDoesNotBelongToUser_thenAccessDeniedExceptionThrown() {
		String currentUsername = "nadya";
		Long lessonId = 10L;

		assertThrows(AccessDeniedException.class, () -> lessonService.countSalary(currentUsername, lessonId));
	}

	@Test
	@Order(31)
	public void countSalary_whenLessonWithFixedSalaryBelongsToUserButSalaryIsBiggerThanIncome_thenNullReturned() {
		String currentUsername = "robby";
		Long lessonId = 20L;

		assertNull(lessonService.countSalary(currentUsername, lessonId));
	}

	@Test
	@Order(32)
	public void countSalary_whenLessonWithFixedSalaryBelongsToUserButSalaryIsSmallerThanIncome_thenSalaryCounted() {
		String currentUsername = "robby";
		Long lessonId = 20L;
		lessonService.subscribe("edik", lessonId);
		lessonService.subscribe("marik", lessonId);

		assertEquals(Double.valueOf(getLessonTable().get(lessonId).getFixedSalary()), lessonService.countSalary(currentUsername, lessonId));
	}

	@Test
	@Order(33)
	public void countSalary_whenLessonWithUnfixedSalaryBelongsToUser_() {
		String currentUsername = "robby";
		Long lessonId = 21L;
		lessonService.subscribe("derevo", lessonId);

		assertEquals((double) (getLessonTable().get(lessonId).getSubscriptions().size() * getLessonTable().get(lessonId).getCost() * getLessonTable().get(lessonId).getUnfixedSalary()) / 100, lessonService.countSalary(currentUsername, lessonId));
	}

	@Test
	@Order(34)
	public void updateLessonStatus_givenCurrentTime_thenChangeStatuses() {
		String currentUsername = "rav";
		LocalDateTime now = LocalDateTime.parse("12.11.2022 13:00", getDateTimeFormatter());
		Long lessonId = 8L;

		lessonService.updateLessonStatus(currentUsername, now, lessonId);

		assertEquals(LessonStatus.FINISHED, getLessonTable().get(lessonId).getStatus());
	}

	@Test
	@Order(35)
	public void denyLesson_givenCurrentUserIsTeacher_whenCourseDoesNotBelongToUser_thenAccessDeniedExceptionThrown() {
		String currentUsername = "rav";
		Long lessonId = 26L;

		assertThrows(AccessDeniedException.class, () -> lessonService.denyLesson(currentUsername, lessonId));
	}

	@Test
	@Order(36)
	public void denyLesson_whenLessonIsInProgress_thenDenyLessonExceptionThrown() {
		String currentUsername = "nadya";
		Long lessonId = 30L;
		getLessonTable().get(lessonId).setStatus(LessonStatus.IN_PROGRESS);

		assertThrows(DenyLessonException.class, () -> lessonService.denyLesson(currentUsername, lessonId));
	}

	@Test
	@Order(37)
	public void denyLesson_givenCurrentUserIsAdmin_whenAllDataIsCorrect_thenSuccess() {
		String currentUsername = "svetlana";
		Long lessonId = 30L;
		getLessonTable().get(lessonId).setStatus(LessonStatus.NOT_STARTED);

		lessonService.denyLesson(currentUsername, lessonId);

		assertEquals(LessonStatus.DENIED, getLessonTable().get(lessonId).getStatus());
		assertFalse(getCourseTable().get(5L).getLessons().contains(getLessonTable().get(lessonId)));
	}

	@Test
	@Order(38)
	public void denyLesson_whenLessonIsAlreadyDenied_thenDenyLessonExceptionThrown() {
		String currentUsername = "nadya";
		Long lessonId = 30L;

		assertThrows(DenyLessonException.class, () -> lessonService.denyLesson(currentUsername, lessonId));
	}

	@Test
	@Order(39)
	public void denyLesson_whenLessonIsAlreadyFinished_thenDenyLessonExceptionThrown() {
		String currentUsername = "nadya";
		Long lessonId = 30L;
		getLessonTable().get(lessonId).setStatus(LessonStatus.FINISHED);

		assertThrows(DenyLessonException.class, () -> lessonService.denyLesson(currentUsername, lessonId));
	}

	@Test
	@Order(40)
	public void deleteLesson_whenLessonIsInProgress_thenDenyLessonExceptionThrown() {
		String currentUsername = "nadya";
		Long lessonId = 30L;
		getLessonTable().get(30L).setStatus(LessonStatus.IN_PROGRESS);

		assertThrows(DenyLessonException.class, () -> lessonService.deleteLesson(currentUsername, lessonId));

		assertTrue(getLessonTable().containsKey(lessonId));
	}

	@Test
	@Order(41)
	public void deleteLesson_givenCurrentUserIsTeacher_whenLessonDoesNotBelongToUser_thenAccessDeniedExceptionThrown() {
		String currentUsername = "lisa";
		Long lessonId = 30L;

		assertThrows(AccessDeniedException.class, () -> lessonService.deleteLesson(currentUsername, lessonId));

		assertTrue(getLessonTable().containsKey(lessonId));
	}

	@Test
	@Order(42)
	public void deleteLesson_whenLessonIsDenied_thenSuccess() {
		String currentUsername = "nadya";
		Long lessonId = 30L;
		getLessonTable().get(lessonId).setStatus(LessonStatus.DENIED);

		lessonService.deleteLesson(currentUsername, lessonId);

		assertFalse(getLessonTable().containsKey(lessonId));
	}

	@Test
	@Order(43)
	public void deleteLesson_whenLessonIsNotStarted_thenSuccess() {
		String currentUsername = "nadya";
		Long lessonId = 29L;
		getLessonTable().get(lessonId).setStatus(LessonStatus.NOT_STARTED);

		lessonService.deleteLesson(currentUsername, lessonId);

		assertFalse(getLessonTable().containsKey(lessonId));
	}

	@Test
	@Order(44)
	public void deleteLesson_whenLessonIsFinished_thenSuccess() {
		String currentUsername = "svetlana";
		Long lessonId = 28L;
		getLessonTable().get(lessonId).setStatus(LessonStatus.FINISHED);

		lessonService.deleteLesson(currentUsername, lessonId);

		assertFalse(getLessonTable().containsKey(lessonId));
	}

	@Test
	@Order(45)
	public void deleteLesson_whenLessonIsNotFound_thenNotFoundExceptionThrown() {
		String currentUsername = "svetlana";
		Long lessonId = 30L;

		assertThrows(NotFoundException.class, () -> lessonService.deleteLesson(currentUsername, lessonId));
	}
}
