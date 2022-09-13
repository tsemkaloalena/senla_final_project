package com.tsemkalo.experienceApp.requestControllers;

import com.tsemkalo.experienceApp.CourseDto;
import com.tsemkalo.experienceApp.CourseService;
import com.tsemkalo.experienceApp.LessonDto;
import com.tsemkalo.experienceApp.enums.LessonStatus;
import com.tsemkalo.experienceApp.mappers.CourseMapper;
import com.tsemkalo.experienceApp.mappers.LessonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import java.util.List;
import java.util.stream.Collectors;

import static com.tsemkalo.experienceApp.PermissionsForController.ADD;
import static com.tsemkalo.experienceApp.PermissionsForController.DELETE;
import static com.tsemkalo.experienceApp.PermissionsForController.EDIT;
import static com.tsemkalo.experienceApp.PermissionsForController.GET_SALARY;
import static com.tsemkalo.experienceApp.PermissionsForController.READ;
import static com.tsemkalo.experienceApp.PermissionsForController.SUBSCRIBE;

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
	 * @param status         filter by status (is the course started, finished and etc)
	 * @param teacherName    filter by teacher name
	 * @param teacherSurname filter by teacher surname
	 * @param theme          filter by course theme
	 * @param minRating      find courses with rating not less than given
	 * @param online         find online / offline courses
	 * @param address        filter by address
	 * @param free           find only free courses
	 * @param minCost        find courses with cost not less than given
	 * @param maxCost        find courses with cost not greater than given
	 * @param numberOfPlaces find courses with number of places left not less than given
	 * @param individual     find individual / group courses
	 * @param from           find courses starting after given date
	 * @param to             find courses finishing before given date
	 * @return filtered list of courses
	 */
	@PreAuthorize(READ)
	@GetMapping
	public List<CourseDto> getCourses(@RequestParam(required = false) LessonStatus status,
									  @RequestParam(required = false) String teacherName,
									  @RequestParam(required = false) String teacherSurname,
									  @RequestParam(required = false) String theme,
									  @RequestParam(required = false) String minRating,
									  @RequestParam(required = false) Boolean online,
									  @RequestParam(required = false) String address,
									  @RequestParam(required = false) Boolean free,
									  @RequestParam(required = false) Integer minCost,
									  @RequestParam(required = false) Integer maxCost,
									  @RequestParam(required = false) Integer numberOfPlaces,
									  @RequestParam(required = false) Boolean individual,
									  @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm") LocalDateTime from,
									  @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm") LocalDateTime to) {
		List<Predicate> predicates = new ArrayList<>();
		if (teacherName != null && teacherSurname != null) {
			predicates.add(courseService.getTeacherPredicate(teacherName, teacherSurname));
		}
		if (theme != null) {
			predicates.add(courseService.getThemePredicate(theme));
		}
		if (minRating != null) {
			predicates.add(courseService.getRatingPredicate(Double.valueOf(minRating)));
		}
		if (address != null || online != null) {
			predicates.add(courseService.getOnlineOfflinePredicate(online, address));
		}
		if (free != null || minCost != null || maxCost != null) {
			predicates.add(courseService.getCostPredicate(free, minCost, maxCost));
		}
		if (individual != null) {
			predicates.add(courseService.getIndividualPredicate(individual));
		}
		predicates.add(courseService.getStatusPredicate(status));
		if (from != null || to != null) {
			predicates.add(courseService.getDatePredicate(from, to));
		}
		if (numberOfPlaces != null) {
			predicates.add(courseService.getFreePlacesLeftPredicate(numberOfPlaces));
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
	public String denyCourse(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String courseStatus = courseService.denyCourse(authentication.getName(), id);
		if (courseStatus == null) {
			courseStatus = "No lessons belonged to this course are denied";
		}
		return "Course with id " + id + " is denied, you can begin it later.\n" + courseStatus;
	}

	/**
	 * @param id id of the course that should be deleted
	 * @return message about deleted lessons that belonged to given course
	 */
	@PreAuthorize(DELETE)
	@DeleteMapping("/{id}")
	public String deleteCourse(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String courseStatus = courseService.deleteCourse(authentication.getName(), id);
		if (courseStatus == null) {
			courseStatus = "No lessons belonged to this course are deleted";
		}
		return "Course with id " + id + " is deleted\n" + courseStatus;
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
	public String subscribeCourse(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (courseService.subscribe(authentication.getName(), id)) {
			return "You are subscribed to course with id " + id;
		}
		return "Subscription was failed. You were already subscribed to course with id " + id;
	}

	/**
	 * @param id id of the course to which the student should be unsubscribed
	 * @return message about success of canceling subscription
	 */
	@PreAuthorize(SUBSCRIBE)
	@PostMapping("/{id}/unsubscribe")
	public String unsubscribeCourse(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (courseService.unsubscribe(authentication.getName(), id)) {
			return "You are unsubscribed to course with id " + id;
		}
		return "Canceling subscription was failed. You were not subscribed to the course with id " + id;
	}

	/**
	 * @param id id of the course for which the teacher's salary should be counted
	 * @return message about counted salary
	 */
	@PreAuthorize(GET_SALARY)
	@GetMapping("{id}/salary")
	public String countSalary(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Double salary = courseService.countSalary(authentication.getName(), id);
		if (salary == null) {
			return "You don't have a salary, too few students";
		}
		return "Your salary for the course with id " + id + " is " + salary;
	}

	/**
	 * @param id          id of the course whose status and lesson statuses should be updated
	 * @param currentTime time according to which the statuses should be updated
	 * @return message about success of status updates
	 */
	@PreAuthorize(EDIT)
	@PostMapping("/{id}/update_statuses")
	public String updateLessonStatuses(@PathVariable Long id, @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm") LocalDateTime currentTime) {
		// automatic update by matching with current date is supported
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		courseService.updateLessonStatusesInCourse(authentication.getName(), currentTime, id);
		courseService.updateCourseStatuses(authentication.getName(), id);
		return "Your lesson statuses are updated according to the current time";
	}
}
