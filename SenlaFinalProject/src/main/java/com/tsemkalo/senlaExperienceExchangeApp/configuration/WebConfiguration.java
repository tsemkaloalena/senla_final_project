package com.tsemkalo.senlaExperienceExchangeApp.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(value = "com.tsemkalo.senlaExperienceExchangeApp.controller")
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {
}