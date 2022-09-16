package com.tsemkalo.senlaExperienceExchangeApp.exceptions;

public class SecurityConfigurationException extends RuntimeException {
	public SecurityConfigurationException(String message) {
		super("During security configuration an error was caught: " + message);
	}
}
