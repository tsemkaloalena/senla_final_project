package com.tsemkalo.senlaExperienceExchangeApp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewDto extends AbstractDto {
	private Integer rating;
	private String text;
	private Long userId;
	private Long lessonId;
	private Long courseId;

	public ReviewDto(Long id, Integer rating, String text, Long userId, Long lessonId, Long courseId) {
		this.id = id;
		this.rating = rating;
		this.text = text;
		this.userId = userId;
		this.lessonId = lessonId;
		this.courseId = courseId;
	}
}
