package com.tsemkalo.senlaExperienceExchangeApp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.tsemkalo.senlaExperienceExchangeApp.enums.LessonStatus;
import com.tsemkalo.senlaExperienceExchangeApp.enums.OnlineType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CourseDto extends AbstractDto {
	private String theme;
	private String subject;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@JsonSerialize(using = ToStringSerializer.class)
	private LessonStatus status;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@JsonSerialize(using = ToStringSerializer.class)
	private OnlineType online;

	private String address;
	private Long teacherId;
	private Boolean individual;
	private Integer cost;
	private Double averageRating;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
	private LocalDateTime startDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
	private LocalDateTime finishDate;

	private String description;

	public CourseDto(Long id, String theme, String subject, LessonStatus status, OnlineType online, String address, Long teacherId, Boolean individual, Integer cost, LocalDateTime startDate, LocalDateTime finishDate, String description, Double averageRating) {
		this.id = id;
		this.theme = theme;
		this.subject = subject;
		this.status = status;
		this.online = online;
		this.address = address;
		this.teacherId = teacherId;
		this.individual = individual;
		this.cost = cost;
		this.startDate = startDate;
		this.finishDate = finishDate;
		this.description = description;
		this.averageRating = averageRating;
	}
}
