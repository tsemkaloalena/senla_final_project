package com.tsemkalo.senlaExperienceExchangeApp.dao.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "reviews")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Review extends AbstractEntity {

	/**
	 * Field for holding rating from 1 to 5
	 */
	@Column
	private Integer rating;

	/**
	 * Field for holding text of the review, comment
	 */
	@Column
	private String text;


	/**
	 * Field for holding a user who wrote this review
	 */
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	/**
	 * Lesson about which the review is written (null if the review is about course)
	 */
	@ManyToOne
	@JoinColumn(name = "lesson_id")
	private Lesson lesson;

	/**
	 * Course about which the review is written (null if the review is about lesson)
	 */
	@ManyToOne
	@JoinColumn(name = "course_id")
	private Course course;
}
