package com.tsemkalo.senlaExperienceExchangeApp.controller;

import com.tsemkalo.senlaExperienceExchangeApp.dto.CourseDto;
import com.tsemkalo.senlaExperienceExchangeApp.dto.LessonDto;
import com.tsemkalo.senlaExperienceExchangeApp.dto.SearchFilterDto;
import com.tsemkalo.senlaExperienceExchangeApp.dto.mappers.CourseMapper;
import com.tsemkalo.senlaExperienceExchangeApp.dto.mappers.LessonMapper;
import com.tsemkalo.senlaExperienceExchangeApp.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.PermissionsForController.ADD;
import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.PermissionsForController.DELETE;
import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.PermissionsForController.EDIT;
import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.PermissionsForController.GET_SALARY;
import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.PermissionsForController.READ;
import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.PermissionsForController.SUBSCRIBE;

@Slf4j
@RestController
@RequestMapping("/courses")
public class CourseController {
	@Autowired
	private CourseService courseService;

	@Autowired
	private CourseMapper courseMapper;

	@Autowired
	private LessonMapper lessonMapper;

	/**
	 * Finding and filtering courses
	 *
	 * @param searchFilterDto filter parameters:
	 *                        status - filter by status (is the course started, finished and etc),
	 *                        teacherName - filter by teacher name,
	 *                        teacherSurname - filter by teacher surname,
	 *                        theme - filter by course theme,
	 *                        minRating - find courses with rating not less than given,
	 *                        online - find online / offline courses,
	 *                        address - filter by address,
	 *                        free - find only free courses,
	 *                        minCost - find courses with cost not less than given,
	 *                        maxCost - find courses with cost not greater than given,
	 *                        numberOfPlaces - find courses with number of places left not less than given,
	 *                        individual - find individual / group courses,
	 *                        from - find courses starting after given date,
	 *                        to - find courses finishing before given date
	 * @return filtered list of courses
	 */
	@GetMapping
	public List<CourseDto> getCourses(@RequestBody SearchFilterDto searchFilterDto) {
		List<Predicate> predicates = new ArrayList<>();
		if (searchFilterDto.getTeacherName() != null && searchFilterDto.getTeacherSurname() != null) {
			predicates.add(courseService.getTeacherPredicate(searchFilterDto.getTeacherName(), searchFilterDto.getTeacherSurname()));
		}
		if (searchFilterDto.getTheme() != null) {
			predicates.add(courseService.getThemePredicate(searchFilterDto.getTheme()));
		}
		if (searchFilterDto.getMinRating() != null) {
			predicates.add(courseService.getRatingPredicate(searchFilterDto.getMinRating()));
		}
		if (searchFilterDto.getAddress() != null || searchFilterDto.getOnline() != null) {
			predicates.add(courseService.getOnlineOfflinePredicate(searchFilterDto.getOnline(), searchFilterDto.getAddress()));
		}
		if (searchFilterDto.getFree() != null || searchFilterDto.getMinCost() != null || searchFilterDto.getMaxCost() != null) {
			predicates.add(courseService.getCostPredicate(searchFilterDto.getFree(), searchFilterDto.getMinCost(), searchFilterDto.getMaxCost()));
		}
		if (searchFilterDto.getIndividual() != null) {
			predicates.add(courseService.getIndividualPredicate(searchFilterDto.getIndividual()));
		}
		predicates.add(courseService.getStatusPredicate(searchFilterDto.getStatus()));
		if (searchFilterDto.getFrom() != null || searchFilterDto.getTo() != null) {
			predicates.add(courseService.getDatePredicate(searchFilterDto.getFrom(), searchFilterDto.getTo()));
		}
		if (searchFilterDto.getNumberOfPlaces() != null) {
			predicates.add(courseService.getFreePlacesLeftPredicate(searchFilterDto.getNumberOfPlaces()));
		}
		return courseService.getCourses(predicates).stream().map(courseMapper::toDto).collect(Collectors.toList());
	}

	/**
	 * @param id id of the course
	 * @return lessons which belong to current course
	 */
	@PreAuthorize(READ)
	@GetMapping("/{id}/lessons")
	public List<LessonDto> getCourseLessons(@PathVariable Long id) {
		return courseService.getCourseLessons(id).stream().map(lessonMapper::toDto).collect(Collectors.toList());
	}

	/**
	 * @param courseDto course data
	 * @return added to database course data
	 */
	@PreAuthorize(ADD)
	@PutMapping
	public CourseDto addCourse(@RequestBody CourseDto courseDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return courseMapper.toDto(courseService.addCourse(authentication.getName(), courseMapper.toEntity(courseDto)));
	}

	/**
	 * @param id        id of the edited course
	 * @param courseDto data that should be edited
	 * @return edited course data
	 */
	@PreAuthorize(EDIT)
	@PostMapping("/{id}")
	public CourseDto editCourse(@PathVariable Long id, @RequestBody CourseDto courseDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		courseDto.setId(id);
		return courseMapper.toDto(courseService.editCourse(authentication.getName(), courseMapper.toEntity(courseDto)));
	}

	/**
	 * @param id id of the course that should be denied
	 * @return message about denied lessons that belonged to given course
	 */
	@PreAuthorize(EDIT)
	@PostMapping("/{id}/deny")
	public ResponseEntity<Object> denyCourse(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		List<Long> courseStatus = courseService.denyCourse(authentication.getName(), id);
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("message", "Course with id \" + id + \" is denied, you can begin it later.");
		body.put("denied lessons", courseStatus);
		return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
	}

	/**
	 * @param id id of the course that should be deleted
	 * @return message about deleted lessons that belonged to given course
	 */
	@PreAuthorize(DELETE)
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteCourse(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		List<Long> courseStatus = courseService.deleteCourse(authentication.getName(), id);
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("message", "Course with id " + id + " is deleted");
		body.put("deleted lessons", courseStatus);
		return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
	}

	/**
	 * @return list of courses to which the student is subscribed
	 */
	@PreAuthorize(SUBSCRIBE)
	@GetMapping("/subscriptions")
	public List<CourseDto> getSubscriptions() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return courseService.getSubscriptions(authentication.getName()).stream().map(courseMapper::toDto).collect(Collectors.toList());
	}

	/**
	 * @param id id of the course to which the student should be subscribed
	 * @return message about subscription success
	 */
	@PreAuthorize(SUBSCRIBE)
	@PostMapping("/{id}/subscribe")
	public ResponseEntity<Object> subscribeCourse(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Map<String, Object> body = new LinkedHashMap<>();
		if (courseService.subscribe(authentication.getName(), id)) {
			body.put("message", "You are subscribed to course with id " + id);
		} else {
			body.put("message", "Subscription was failed. You were already subscribed to course with id " + id);
		}
		return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
	}

	/**
	 * @param id id of the course to which the student should be unsubscribed
	 * @return message about success of canceling subscription
	 */
	@PreAuthorize(SUBSCRIBE)
	@PostMapping("/{id}/unsubscribe")
	public ResponseEntity<Object> unsubscribeCourse(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Map<String, Object> body = new LinkedHashMap<>();
		if (courseService.unsubscribe(authentication.getName(), id)) {
			body.put("message", "You are unsubscribed to course with id " + id);
		} else {
			body.put("message", "Canceling subscription was failed. You were not subscribed to the course with id " + id);
		}
		return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
	}

	/**
	 * @param id id of the course for which the teacher's salary should be counted
	 * @return message about counted salary
	 */
	@PreAuthorize(GET_SALARY)
	@GetMapping("{id}/salary")
	public ResponseEntity<Object> countSalary(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Double salary = courseService.countSalary(authentication.getName(), id);
		Map<String, Object> body = new LinkedHashMap<>();
		if (salary == null) {
			body.put("message", "You don't have a salary, too few students");
		} else {
			body.put("message", "Your salary for the course with id " + id + " is " + salary);
		}
		return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
	}

	/**
	 * @param id          id of the course whose status and lesson statuses should be updated
	 * @param currentTime time according to which the statuses should be updated
	 * @return message about success of status updates
	 */
	@PreAuthorize(EDIT)
	@PostMapping("/{id}/update_statuses")
	public ResponseEntity<Object> updateLessonStatuses(@PathVariable Long id, @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm") LocalDateTime currentTime) {
		// automatic update by matching with current date is supported
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		courseService.updateLessonStatusesInCourse(authentication.getName(), currentTime, id);
		courseService.updateCourseStatuses(authentication.getName(), id);
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("message", "Your lesson statuses are updated according to the current time");
		return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
	}
}
