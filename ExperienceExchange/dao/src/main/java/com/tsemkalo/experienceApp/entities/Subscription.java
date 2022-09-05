package com.tsemkalo.experienceApp.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "subscriptions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Subscription extends AbstractEntity {
	@ManyToOne
	@JoinColumn(name = "student_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "lesson_id")
	private Lesson lesson;
}
