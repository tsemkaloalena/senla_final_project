package com.tsemkalo.senlaExperienceExchangeApp.configuration.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.User;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.AuthenticationCredentialsReadingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;

import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.SecurityConstants.EXPIRATION_TIME;
import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.SecurityConstants.HEADER_STRING;
import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.SecurityConstants.SECRET;
import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.SecurityConstants.TOKEN_PREFIX;

@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager authenticationManager;

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
		setFilterProcessesUrl("/user/login");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		User credentials = null;
		try {
			credentials = new ObjectMapper().readValue(request.getInputStream(), User.class);
		} catch (IOException exception) {
			log.error(Arrays.toString(exception.getStackTrace()));
			throw new AuthenticationCredentialsReadingException();
		}
		return authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						credentials.getUsername(),
						credentials.getPassword(),
						new ArrayList<>())
		);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
		String token = JWT.create()
				.withSubject(authentication.getName())
				.withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.sign(Algorithm.HMAC512(SECRET.getBytes()));

		response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
//		String body = authentication.getName() + " " + token;
//		response.getWriter().write(body);
//		response.getWriter().flush();
	}
}
