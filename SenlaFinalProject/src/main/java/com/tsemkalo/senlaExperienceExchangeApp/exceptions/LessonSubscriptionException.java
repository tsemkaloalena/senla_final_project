package com.tsemkalo.senlaExperienceExchangeApp.exceptions;

public class LessonSubscriptionException extends RuntimeException {
	public LessonSubscriptionException(String message) {
		super("During subscription an error appeared: " + message);
	}
}
