package com.tsemkalo.experienceApp.entities;

import com.tsemkalo.experienceApp.enums.LessonStatus;
import com.tsemkalo.experienceApp.enums.OnlineType;
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
	@Column
	private String theme;

	@Column
	private String subject;

	@Column
	@Enumerated(EnumType.STRING)
	private LessonStatus status;

	@Column
	@Enumerated(EnumType.STRING)
	private OnlineType online;

	@Column
	private String address;

	@OneToOne
	@JoinColumn(name = "teacher_id")
	private User teacher;

	@Column
	private Boolean individual;

	@Column
	private Integer cost;

	@Column
	private LocalDateTime startDate;

	@Column
	private LocalDateTime finishDate;

	@Column
	private String description;

	@Column
	private Double averageRating;

	@OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
	@OrderBy("lesson_date asc")
	private List<Lesson> lessons;

	@OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
	private List<Review> reviews;

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

	public void setOnline() {
		Boolean onlineExists = getLessons().stream().anyMatch(lesson -> OnlineType.ONLINE.equals(lesson.getOnline()));
		Boolean offlineExists = getLessons().stream().anyMatch(lesson -> OnlineType.OFFLINE.equals(lesson.getOnline()));
		if (onlineExists && offlineExists) {
			this.online = OnlineType.PARTLY_ONLINE;
		} else if (offlineExists) {
			this.online = OnlineType.OFFLINE;
		} else {
			this.online = OnlineType.ONLINE;
		}
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

	public Double getSalary() {
		if (getLessons().isEmpty() || getLessons().get(0).getSalary() == null) {
			return null;
		}
		return getLessons().stream().map(Lesson::getSalary).mapToDouble(Double::doubleValue).sum();
	}

	public void countAverageRating() {
		if (reviews.isEmpty()) {
			averageRating = null;
		} else {
			averageRating = reviews.stream().map(Review::getRating).mapToDouble(Integer::doubleValue).sum() / reviews.size();
		}
	}

	public void countCost() {
		if (getLessons().isEmpty()) {
			this.cost = null;
		} else {
			this.cost = getLessons().stream().map(Lesson::getCost).mapToInt(Integer::intValue).sum();
		}
	}

	public void countFinishDate() {
		if (!getLessons().isEmpty()) {
			setFinishDate(getLessons().stream().map(lesson -> lesson.getLessonDate().plusMinutes(lesson.getDurability())).max(LocalDateTime::compareTo).get());
		}
	}

	public void countStartDate() {
		if (!getLessons().isEmpty()) {
			setStartDate(getLessons().stream().map(Lesson::getLessonDate).min(LocalDateTime::compareTo).get());
		}
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
