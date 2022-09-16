package com.tsemkalo.senlaExperienceExchangeApp.exceptions;

public class AuthenticationCredentialsReadingException extends RuntimeException {
	public AuthenticationCredentialsReadingException() {
		super("Reading your credentials was failed");
	}
}
