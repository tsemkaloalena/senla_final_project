package com.tsemkalo.experienceApp.requestControllers;

import com.tsemkalo.experienceApp.CourseService;
import com.tsemkalo.experienceApp.LessonService;
import com.tsemkalo.experienceApp.entities.Lesson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static com.tsemkalo.experienceApp.enums.PermissionType.Authorities.EDIT;
import static com.tsemkalo.experienceApp.enums.PermissionType.Authorities.READ;

@Slf4j
@RestController
@RequestMapping
public class CatalogController {
	@Autowired
	private CourseService courseService;

	@Autowired
	private LessonService lessonService;

	@PreAuthorize(READ)
	@GetMapping("/themes")
	public ResponseEntity<Object> getThemes() {
		return new ResponseEntity<>(lessonService.getThemes(), HttpStatus.FOUND);
	}

	@PreAuthorize(READ)
	@GetMapping("/schedule")
	public String getSchedule() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		StringBuilder schedule = new StringBuilder();
		schedule.append("Schedule:");
		List<Lesson> lessonList = lessonService.getSchedule(authentication.getName());
		if (lessonList.isEmpty()) {
			return "Your don't have any upcoming lessons";
		}
		for (Lesson lesson : lessonList) {
			schedule.append("\n");
			schedule.append(lesson.getLessonDate());
			schedule.append(" - ");
			schedule.append("Lesson ");
			schedule.append(lesson.getId());
			schedule.append(": ");
			schedule.append(lesson.getTheme());
			schedule.append(". ");
			schedule.append(lesson.getSubject());
		}
		return schedule.toString();
	}

	@PreAuthorize(EDIT)
	@PostMapping("/schedule/update_statuses")
	public String updateLessonStatuses(@RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm") LocalDateTime currentTime) {
		// automatic update by matching with current date is supported
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		lessonService.updateLessonStatuses(authentication.getName(), currentTime);
		courseService.updateCourseStatuses(authentication.getName());
		return "Your lesson statuses are updated according to the current time";
	}
}
