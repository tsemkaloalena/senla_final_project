package com.tsemkalo.senlaExperienceExchangeApp.configuration;

import com.tsemkalo.senlaExperienceExchangeApp.configuration.security.JWTAuthenticationFilter;
import com.tsemkalo.senlaExperienceExchangeApp.configuration.security.JWTAuthorizationFilter;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.SecurityConfigurationException;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.exceptionHandlers.CustomAccessDeniedHandler;
import com.tsemkalo.senlaExperienceExchangeApp.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.util.Arrays;

@Slf4j
@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Autowired
	private UserServiceImpl userService;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	@Qualifier("delegatedAuthenticationEntryPoint")
	private AuthenticationEntryPoint authenticationEntryPoint;

	@Override
	protected void configure(HttpSecurity http) {
		try {
			http.csrf().disable()
					.cors().and()
					.authorizeRequests()
					.antMatchers(HttpMethod.POST, "/user/sign_up", "/user/forgot_password", "/user/reset_password/*").permitAll()
					.antMatchers(HttpMethod.GET, "/themes", "/lessons", "/courses").permitAll()
					.anyRequest().authenticated()
					.and()
					.formLogin().permitAll()
					.and()
					.logout().permitAll()
					.and()
					.exceptionHandling()
					.accessDeniedHandler(accessDeniedHandler())
					.authenticationEntryPoint(authenticationEntryPoint)
					.and()
					.addFilter(new JWTAuthenticationFilter(authenticationManager()))
					.addFilter(new JWTAuthorizationFilter(authenticationManager(), userService))
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		} catch (Exception exception) {
			log.error(Arrays.toString(exception.getStackTrace()));
			throw new SecurityConfigurationException(Arrays.toString(exception.getStackTrace()));
		}
	}

	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) {
		try {
			authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
		} catch (Exception exception) {
			log.error(Arrays.toString(exception.getStackTrace()));
			throw new SecurityConfigurationException(Arrays.toString(exception.getStackTrace()));
		}
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return new CustomAccessDeniedHandler();
	}
}
