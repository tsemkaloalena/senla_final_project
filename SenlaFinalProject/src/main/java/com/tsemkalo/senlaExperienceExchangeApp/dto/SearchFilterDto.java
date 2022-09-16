package com.tsemkalo.senlaExperienceExchangeApp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tsemkalo.senlaExperienceExchangeApp.enums.LessonStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class SearchFilterDto extends AbstractDto {
	private LessonStatus status;
	private String teacherName;
	private String teacherSurname;
	private String theme;
	private Double minRating;
	private Boolean online;
	private String address;
	private Boolean free;
	private Integer minCost;
	private Integer maxCost;
	private Integer numberOfPlaces;
	private Boolean individual;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
	private LocalDateTime from;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
	private LocalDateTime to;
}
