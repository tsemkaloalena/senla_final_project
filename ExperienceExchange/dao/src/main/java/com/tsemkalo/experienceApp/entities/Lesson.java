package com.tsemkalo.experienceApp.entities;

import com.tsemkalo.experienceApp.enums.LessonStatus;
import com.tsemkalo.experienceApp.enums.OnlineType;
import com.tsemkalo.experienceApp.exceptions.IncorrectDataException;
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
	@Column
	private String theme;

	@Column
	private String subject;

	@OneToOne
	@JoinColumn(name = "teacher_id")
	private User teacher;

	@ManyToOne
	@JoinColumn(name = "course_id")
	private Course course;

	@Column
	@Enumerated(EnumType.STRING)
	private LessonStatus status;

	@Column
	private String address;

	@Column
	private Boolean individual;

	@Column
	private Integer maxStudentsNumber;

	@Column
	private String description;

	@Column
	private Integer cost;

	@Column
	private Integer durability;

	@Column
	private LocalDateTime lessonDate;

	@Column
	private Integer fixedSalary;

	@Column
	private Integer unfixedSalary;

	@Column
	private Double averageRating;

	@OneToMany(mappedBy = "lesson", cascade = CascadeType.REMOVE)
	private List<Subscription> subscriptions;

	@OneToMany(mappedBy = "lesson", cascade = CascadeType.REMOVE)
	private List<Review> reviews;

	@Transient
	private Integer freePlacesLeft;

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
		if (maxStudentsNumber == null || subscriptions == null) {
			return null;
		}
		freePlacesLeft = maxStudentsNumber - subscriptions.size();
		return freePlacesLeft;
	}

	public Double getSalary() {
		int income = getSubscriptions().size() * getCost();
		if (income == 0) {
			return null;
		}
		if (getUnfixedSalary() != null) {
			return (double) (income * getUnfixedSalary()) / 100;
		}
		if (getFixedSalary() != null && income > getFixedSalary()) {
			return (double) getFixedSalary();
		}
		return null;
	}

	public void countAverageRating() {
		if (reviews.isEmpty()) {
			averageRating = null;
		} else {
			averageRating = reviews.stream().map(Review::getRating).mapToDouble(Integer::doubleValue).sum() / reviews.size();
		}
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
