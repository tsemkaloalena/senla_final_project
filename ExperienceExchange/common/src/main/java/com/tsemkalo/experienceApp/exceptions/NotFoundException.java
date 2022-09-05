package com.tsemkalo.experienceApp.exceptions;

public class NotFoundException extends RuntimeException {
	public NotFoundException(String object) {
		super(object + " is not found");
	}
}
