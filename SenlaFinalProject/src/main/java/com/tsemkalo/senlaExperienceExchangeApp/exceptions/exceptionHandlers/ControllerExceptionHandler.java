package com.tsemkalo.senlaExperienceExchangeApp.exceptions.exceptionHandlers;

import com.tsemkalo.senlaExperienceExchangeApp.exceptions.AuthenticationCredentialsReadingException;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.AuthorizationErrorException;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.DenyLessonException;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.IncorrectDataException;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.LessonSubscriptionException;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.MailSendingException;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.NotFoundException;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.SecurityConfigurationException;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.UserExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler({AuthenticationCredentialsReadingException.class, IOException.class, MailSendingException.class})
	public ResponseEntity<Object> handleInternalErrorException(RuntimeException exception, WebRequest request) {
		return makeResponseEntity(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({LessonSubscriptionException.class, IncorrectDataException.class, IllegalArgumentException.class, IllegalStateException.class})
	public ResponseEntity<Object> handleIllegalArgumentException(RuntimeException exception, WebRequest request) {
		return makeResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({NotFoundException.class})
	public ResponseEntity<Object> handleNotFoundException(RuntimeException exception, WebRequest request) {
		return makeResponseEntity(exception.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler({AuthorizationErrorException.class, AuthenticationException.class})
	public ResponseEntity<Object> handleUnauthorizedErrorException(RuntimeException exception, WebRequest request) {
		return makeResponseEntity(exception.getMessage(), HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler({UserExistsException.class, SecurityConfigurationException.class, AccessDeniedException.class})
	public ResponseEntity<Object> handleUserExistsException(RuntimeException exception, WebRequest request) {
		return makeResponseEntity(exception.getMessage(), HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler({DenyLessonException.class})
	public ResponseEntity<Object> handleDenyLessonException(RuntimeException exception, WebRequest request) {
		return makeResponseEntity(exception.getMessage(), HttpStatus.CONFLICT);
	}

	private ResponseEntity<Object> makeResponseEntity(String message, HttpStatus httpStatus) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", Calendar.getInstance());
		body.put("message", message);
		return new ResponseEntity<>(body, httpStatus);
	}
}
