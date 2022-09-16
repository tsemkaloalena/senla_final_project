package com.tsemkalo.senlaExperienceExchangeApp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.tsemkalo.senlaExperienceExchangeApp.enums.LessonStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class LessonDto extends AbstractDto {
	private String theme;
	private String subject;
	private Long teacherId;
	private Long courseId;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@JsonSerialize(using = ToStringSerializer.class)
	private LessonStatus status;

	private String address;
	private Boolean individual;
	private Integer maxStudentsNumber;
	private String description;
	private Integer cost;
	private Integer durability;
	private Integer freePlacesLeft;
	private Double averageRating;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
	private LocalDateTime lessonDate;
	private Integer fixedSalary;
	private Integer unfixedSalary;

	public LessonDto(Long id, String theme, String subject, Long teacherId, Long courseId, LessonStatus status, String address, Boolean individual, Integer maxStudentsNumber, String description, Integer cost, Integer durability, LocalDateTime lessonDate, Integer fixedSalary, Integer unfixedSalary, Integer freePlacesLeft, Double averageRating) {
		this.id = id;
		this.theme = theme;
		this.subject = subject;
		this.teacherId = teacherId;
		this.courseId = courseId;
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
		this.freePlacesLeft = freePlacesLeft;
		this.averageRating = averageRating;
	}
}
