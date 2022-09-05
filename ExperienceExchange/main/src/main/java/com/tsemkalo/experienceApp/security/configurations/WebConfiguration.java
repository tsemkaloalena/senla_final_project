package com.tsemkalo.experienceApp.security.configurations;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(value = "com.tsemkalo.experienceApp.requestController")
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {
}