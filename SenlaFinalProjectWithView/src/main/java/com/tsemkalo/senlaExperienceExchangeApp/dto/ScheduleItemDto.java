package com.tsemkalo.senlaExperienceExchangeApp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleItemDto extends AbstractDto {
	private String theme;
	private String subject;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
	private LocalDateTime lessonDate;

	public ScheduleItemDto(Long id, String theme, String subject, LocalDateTime lessonDate) {
		this.id = id;
		this.theme = theme;
		this.subject = subject;
		this.lessonDate = lessonDate;
	}
}
