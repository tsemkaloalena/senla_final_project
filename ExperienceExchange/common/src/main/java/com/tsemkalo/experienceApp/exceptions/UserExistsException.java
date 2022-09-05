package com.tsemkalo.experienceApp.exceptions;

public class UserExistsException extends RuntimeException {
	public UserExistsException(String name) {
		super("User " + name + " already exists");
	}
}