package com.tsemkalo.senlaExperienceExchangeApp.exceptions;

public class NotFoundException extends RuntimeException {
	public NotFoundException(String object) {
		super(object + " is not found");
	}
}
