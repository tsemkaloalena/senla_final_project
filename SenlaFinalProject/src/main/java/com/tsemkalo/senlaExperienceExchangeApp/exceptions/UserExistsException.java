package com.tsemkalo.senlaExperienceExchangeApp.exceptions;

public class UserExistsException extends RuntimeException {
	public UserExistsException(String name) {
		super("User " + name + " already exists");
	}
}