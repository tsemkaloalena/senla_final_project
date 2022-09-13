package com.tsemkalo.experienceApp.requestControllers;

import com.tsemkalo.experienceApp.LessonService;
import com.tsemkalo.experienceApp.ScheduleItemDto;
import com.tsemkalo.experienceApp.mappers.ScheduleItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.tsemkalo.experienceApp.PermissionsForController.READ;

/**
 * Common controller for lessons and courses
 */
@Slf4j
@RestController
@RequestMapping
public class CatalogController {
	@Autowired
	private LessonService lessonService;

	@Autowired
	private ScheduleItemMapper scheduleItemMapper;

	/**
	 * @return Directory of knowledge areas by topics and sections
	 */
	@GetMapping("/themes")
	public ResponseEntity<Object> getThemes() {
		return new ResponseEntity<>(lessonService.getThemes(), HttpStatus.FOUND);
	}

	/**
	 * @return Schedule of future and current lessons with date, theme and subject
	 */
	@PreAuthorize(READ)
	@GetMapping("/schedule")
	public List<ScheduleItemDto> getSchedule() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return lessonService.getSchedule(authentication.getName()).stream().map(scheduleItemMapper::toDto).collect(Collectors.toList());
	}
}
