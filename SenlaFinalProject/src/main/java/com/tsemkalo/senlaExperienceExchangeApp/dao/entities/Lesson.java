package com.tsemkalo.senlaExperienceExchangeApp.dao.entities;

import com.tsemkalo.senlaExperienceExchangeApp.enums.LessonStatus;
import com.tsemkalo.senlaExperienceExchangeApp.enums.OnlineType;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.IncorrectDataException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "lessons")
@NoArgsConstructor
@Getter
@Setter
public class Lesson extends AbstractEntity {
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
	 * Teacher field
	 */
	@OneToOne
	@JoinColumn(name = "teacher_id")
	private User teacher;

	/**
	 * Course field
	 */
	@ManyToOne
	@JoinColumn(name = "course_id")
	private Course course;

	/**
	 * Lesson status enum field
	 */
	@Column
	@Enumerated(EnumType.STRING)
	private LessonStatus status;

	/**
	 * Address field
	 */
	@Column
	private String address;

	/**
	 * Field for lesson type designation (is it group or individual)
	 */
	@Column
	private Boolean individual;

	/**
	 * Max students number field
	 * If it is null then the lesson doesn't have capacity restrictions
	 */
	@Column
	private Integer maxStudentsNumber;

	/**
	 * Description field
	 */
	@Column
	private String description;

	/**
	 * Cost field
	 */
	@Column
	private Integer cost;

	/**
	 * Field for holding lesson durability in minutes
	 */
	@Column
	private Integer durability;

	/**
	 * Date field for holding lesson start time
	 */
	@Column
	private LocalDateTime lessonDate;

	/**
	 * Field for holding fixed salary for current lesson for all students
	 */
	@Column
	private Integer fixedSalary;

	/**
	 * Field for holding unfixed salary for current lesson in percents, so final salary is unfixed salary, multiplied by cost
	 */
	@Column
	private Integer unfixedSalary;

	/**
	 * Field for holding average rating among reviews
	 */
	@Column
	private Double averageRating;

	/**
	 * List for holding all subscribed students
	 */
	@OneToMany(mappedBy = "lesson", cascade = CascadeType.REMOVE)
	private List<Subscription> subscriptions;

	/**
	 * List for holding all reviews
	 */
	@OneToMany(mappedBy = "lesson", cascade = CascadeType.REMOVE)
	private List<Review> reviews;

	/**
	 * Field for holding amount of free car places left
	 * If it is null then the lesson doesn't have capacity restrictions
	 */
	@Transient
	private Integer freePlacesLeft;

	/**
	 * Constructor - new lesson creating with given values
	 *
	 * @param theme             theme of the lesson
	 * @param subject           subject of the lesson
	 * @param teacher           teacher and owner of the lesson
	 * @param course            course, to which the lesson belongs
	 * @param status            is the lesson started, finished and etc
	 * @param address           address of the lesson, if null, then the lesson is online
	 * @param individual        is the lesson group or individual
	 * @param maxStudentsNumber maximal amount of students
	 * @param description       curriculum information
	 * @param cost              price for one lesson
	 * @param durability        lesson durability in minutes
	 * @param lessonDate        lesson start time
	 * @param fixedSalary       salary for current lesson for all students
	 * @param unfixedSalary     salary for current lesson in percents, so final salary is unfixed salary, multiplied by cost
	 * @param averageRating     average rating among reviews
	 */
	public Lesson(String theme, String subject, User teacher, Course course, LessonStatus status, String address, Boolean individual, Integer maxStudentsNumber, String description, Integer cost, Integer durability, LocalDateTime lessonDate, Integer fixedSalary, Integer unfixedSalary, Double averageRating) {
		this.theme = theme;
		this.subject = subject;
		this.teacher = teacher;
		this.course = course;
		this.status = status;
		this.address = address;
		this.individual = individual;
		this.maxStudentsNumber = maxStudentsNumber;
		this.description = description;
		this.cost = cost;
		this.durability = durability;
		this.lessonDate = lessonDate;
		this.fixedSalary = fixedSalary;
		this.unfixedSalary = unfixedSalary;
		this.averageRating = averageRating;
	}

	public OnlineType getOnline() {
		if (address == null) {
			return OnlineType.ONLINE;
		}
		return OnlineType.OFFLINE;
	}

	public void setOnline() {
		address = null;
	}

	public Integer getFreePlacesLeft() {
		if (getMaxStudentsNumber() == null || getSubscriptions() == null) {
			return null;
		}
		this.freePlacesLeft = getMaxStudentsNumber() - getSubscriptions().size();
		return this.freePlacesLeft;
	}

	public void setIndividual(Boolean individual) {
		if (individual && getSubscriptions() != null) {
			if (getSubscriptions().size() > 1) {
				throw new IncorrectDataException("There are already too many students subscribed to this lesson. You can not make this lesson individual.");
			}
			setMaxStudentsNumber(1);
		} else {
			if (getMaxStudentsNumber() != null && getMaxStudentsNumber() <= 1) {
				setMaxStudentsNumber(null);
			}
		}
		this.individual = individual;
	}

	public void setMaxStudentsNumber(Integer maxStudentsNumber) {
		if (maxStudentsNumber != null && maxStudentsNumber > 1) {
			this.individual = false;
		}
		this.maxStudentsNumber = maxStudentsNumber;
	}

	@Override
	public String toString() {
		String lessonForm;
		if (getIndividual()) {
			lessonForm = "Individual lessons";
		} else {
			lessonForm = "Group lesson";
		}
		String lessonAddress = "";
		if (getOnline() == OnlineType.OFFLINE) {
			lessonAddress = "Address: " + getAddress() + "\n";
		}
		String freePlacesLeft = "";
		if (getFreePlacesLeft() != null) {
			freePlacesLeft = "Free places left: " + getFreePlacesLeft() + "\n";
		}
		return "Lesson number " + this.getId() + "\n" +
				"Theme: " + this.getTheme() + "\n" +
				"Subject: " + this.getSubject() + "\n" +
				"Teacher: " + this.getTeacher().getName() + " " + this.getTeacher().getSurname() + "\n" +
				"Status: " + this.getStatus().name() + "\n" +
				"Way of doing the lesson: " + getOnline() + "\n" +
				lessonAddress +
				"Form of the lesson: " + lessonForm + "\n" +
				"Cost: " + getCost() + "\n" +
				"Durability: " + getDurability() + "\n" +
				"Date: " + getLessonDate() + "\n" +
				freePlacesLeft +
				"Description: " + this.getDescription();
	}
}
