package com.tsemkalo.experienceApp.security.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.tsemkalo.experienceApp.entities.User;
import com.tsemkalo.experienceApp.exceptions.AuthorizationErrorException;
import com.tsemkalo.experienceApp.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

import static com.tsemkalo.experienceApp.SecurityConstants.HEADER_STRING;
import static com.tsemkalo.experienceApp.SecurityConstants.SECRET;
import static com.tsemkalo.experienceApp.SecurityConstants.TOKEN_PREFIX;

@Slf4j
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
	private UserServiceImpl userService;

	public JWTAuthorizationFilter(AuthenticationManager authenticationManager, UserServiceImpl userService) {
		super(authenticationManager);
		this.userService = userService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
		String header = request.getHeader(HEADER_STRING);
		if (header == null || !header.startsWith(TOKEN_PREFIX)) {
			try {
				chain.doFilter(request, response);
			} catch (IOException | ServletException exception) {
				log.error(Arrays.toString(exception.getStackTrace()));
				throw new AuthorizationErrorException(Arrays.toString(exception.getStackTrace()));
			}
			return;
		}

		UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		try {
			chain.doFilter(request, response);
		} catch (IOException | ServletException exception) {
			log.error(Arrays.toString(exception.getStackTrace()));
			throw new AuthorizationErrorException(Arrays.toString(exception.getStackTrace()));
		}
	}

	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
		String token = request.getHeader(HEADER_STRING);
		if (token != null) {
			String username = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
					.build()
					.verify(token.replace(TOKEN_PREFIX, ""))
					.getSubject();

			if (username != null) {
				User user = userService.loadUserByUsername(username);
				return new UsernamePasswordAuthenticationToken(username, null, user.getAuthorities());
			}
			return null;
		}
		return null;
	}
}
