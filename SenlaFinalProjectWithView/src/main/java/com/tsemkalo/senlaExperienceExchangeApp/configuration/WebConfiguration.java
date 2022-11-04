package com.tsemkalo.senlaExperienceExchangeApp.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan(value = "com.tsemkalo.senlaExperienceExchangeApp.controller")
public class WebConfiguration {

}