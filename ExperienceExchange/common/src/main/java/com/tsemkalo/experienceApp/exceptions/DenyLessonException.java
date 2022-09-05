package com.tsemkalo.experienceApp.exceptions;

public class DenyLessonException extends RuntimeException {
	public DenyLessonException(String item, String message) {
		super("Can not deny " + item + ": " + message);
	}
}