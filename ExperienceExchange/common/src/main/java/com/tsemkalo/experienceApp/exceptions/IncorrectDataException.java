package com.tsemkalo.experienceApp.exceptions;

public class IncorrectDataException extends RuntimeException {
	public IncorrectDataException(String object) {
		super("Incorrect data: " + object);
	}
}
