package com.tsemkalo.senlaExperienceExchangeApp.dao.entities;

import com.tsemkalo.senlaExperienceExchangeApp.enums.LessonStatus;
import com.tsemkalo.senlaExperienceExchangeApp.enums.OnlineType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
public class Course extends AbstractEntity {
	/**
	 * Theme field
	 */
	@Column
	private String theme;

	/**
	 * Subject field
	 */
	@Column
	private String subject;

	/**
	 * Lesson status enum field
	 */
	@Column
	@Enumerated(EnumType.STRING)
	private LessonStatus status;

	@Column
	@Enumerated(EnumType.STRING)
	private OnlineType online;

	/**
	 * Address field
	 */
	@Column
	private String address;

	/**
	 * Teacher field
	 */
	@OneToOne
	@JoinColumn(name = "teacher_id")
	private User teacher;

	/**
	 * Field for lesson type designation (is it group or individual)
	 */
	@Column
	private Boolean individual;

	/**
	 * Cost field
	 */
	@Column
	private Integer cost;

	/**
	 * Field for holding date of the first lesson
	 */
	@Column
	private LocalDateTime startDate;

	/**
	 * Field for holding date of the last lesson
	 */
	@Column
	private LocalDateTime finishDate;

	/**
	 * Description field
	 */
	@Column
	private String description;

	/**
	 * Field for holding average rating among reviews
	 */
	@Column
	private Double averageRating;

	/**
	 * List for holding all lessons belonging to this course
	 */
	@OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
	@OrderBy("lesson_date asc")
	private List<Lesson> lessons;

	/**
	 * List for holding all reviews
	 */
	@OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
	private List<Review> reviews;

	/**
	 * Constructor - new lesson creating with given values
	 *
	 * @param theme         theme of the lesson
	 * @param subject       subject of the lesson
	 * @param status        is the course started, finished and etc
	 * @param online        is the course online or offline
	 * @param address       address of the course, if null, then the course is online
	 * @param teacher       teacher and owner of the course
	 * @param individual    is the course group or individual
	 * @param cost          summery cost for all lessons in this course
	 * @param startDate     date of the first lesson
	 * @param finishDate    date of the last lesson
	 * @param description   curriculum information
	 * @param averageRating average rating among reviews
	 */
	public Course(String theme, String subject, LessonStatus status, OnlineType online, String address, User teacher, Boolean individual, Integer cost, LocalDateTime startDate, LocalDateTime finishDate, String description, Double averageRating) {
		this.theme = theme;
		this.subject = subject;
		this.status = status;
		this.online = online;
		this.address = address;
		this.teacher = teacher;
		this.individual = individual;
		this.cost = cost;
		this.startDate = startDate;
		this.finishDate = finishDate;
		this.description = description;
		this.averageRating = averageRating;
	}

	public Integer getNumberOfLessons() {
		return getLessons().size();
	}

	public Integer getFreePlacesLeft() {
		if (getLessons().isEmpty()) {
			return null;
		}
		return getLessons().get(0).getFreePlacesLeft();
	}

	@Override
	public String toString() {
		String courseForm;
		if (getIndividual()) {
			courseForm = "Individual lessons";
		} else {
			courseForm = "Group lesson";
		}
		String freePlacesLeft = "";
		if (getFreePlacesLeft() != null) {
			freePlacesLeft = "Free places left: " + getFreePlacesLeft() + "\n";
		}
		return "Course number " + this.getId() + "\n" +
				"Theme: " + this.getTheme() + "\n" +
				"Subject: " + this.getSubject() + "\n" +
				"Status: " + this.getStatus().name() + "\n" +
				"Way of doing the course: " + this.getOnline() + "\n" +
				"Form of the course: " + courseForm + "\n" +
				"Full cost for course: " + this.getCost() + "\n" +
				"Number of lessons: " + this.getNumberOfLessons() + "\n" +
				"Start of the course: " + this.getStartDate() + "\n" +
				"Finish of the course: " + this.getFinishDate() + "\n" +
				freePlacesLeft +
				"Description: " + this.getDescription();
	}
}
