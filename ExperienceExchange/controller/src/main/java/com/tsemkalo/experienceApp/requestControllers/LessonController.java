package com.tsemkalo.experienceApp.requestControllers;

import com.tsemkalo.experienceApp.LessonDto;
import com.tsemkalo.experienceApp.LessonService;
import com.tsemkalo.experienceApp.enums.LessonStatus;
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
@RequestMapping("/lessons")
public class LessonController {
	@Autowired
	private LessonService lessonService;

	@Autowired
	private LessonMapper lessonMapper;

	/**
	 * Finding and filtering lessons
	 * @param status filter by status (is the course started, finished and etc)
	 * @param teacherName filter by teacher name
	 * @param teacherSurname filter by teacher surname
	 * @param theme filter by lesson theme
	 * @param minRating find lessons with rating not less than given
	 * @param online find online / offline lessons
	 * @param address filter by address
	 * @param free find only free lessons
	 * @param minCost find lessons with cost not less than given
	 * @param maxCost find lessons with cost not greater than given
	 * @param numberOfPlaces find lessons with number of places left not less than given
	 * @param individual find individual / group lessons
	 * @param from find lessons starting after given date
	 * @param to find lessons finishing before given date
	 * @return filtered list of lessons
	 */
	@PreAuthorize(READ)
	@GetMapping
	public List<LessonDto> getLessons(@RequestParam(required = false) LessonStatus status,
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
			predicates.add(lessonService.getTeacherPredicate(teacherName, teacherSurname));
		}
		if (theme != null) {
			predicates.add(lessonService.getThemePredicate(theme));
		}
		if (minRating != null) {
			predicates.add(lessonService.getRatingPredicate(Double.valueOf(minRating)));
		}
		if (address != null || online != null) {
			predicates.add(lessonService.getOnlineOfflinePredicate(online, address));
		}
		if (free != null || minCost != null || maxCost != null) {
			predicates.add(lessonService.getCostPredicate(free, minCost, maxCost));
		}
		if (individual != null) {
			predicates.add(lessonService.getIndividualPredicate(individual));
		}
		predicates.add(lessonService.getStatusPredicate(status));
		if (from != null || to != null) {
			predicates.add(lessonService.getDatePredicate(from, to));
		}
		if (numberOfPlaces != null) {
			predicates.add(lessonService.getFreePlacesLeftPredicate(numberOfPlaces));
		}
		predicates.add(lessonService.getSingleLessonsPredicate());
		return lessonService.getLessons(predicates).stream().map(lessonMapper::toDto).collect(Collectors.toList());
	}

	/**
	 * @param lessonDto lesson data
	 * @return added to database lesson data
	 */
	@PreAuthorize(ADD)
	@PutMapping
	public LessonDto addLesson(@RequestBody LessonDto lessonDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return lessonMapper.toDto(lessonService.addLesson(authentication.getName(), lessonMapper.toEntity(lessonDto)));
	}

	/**
	 * @param id id of the edited lesson
	 * @param lessonDto data that should be edited
	 * @return edited lesson data
	 */
	@PreAuthorize(EDIT)
	@PostMapping("/{id}")
	public LessonDto editLesson(@PathVariable Long id, @RequestBody LessonDto lessonDto) {
		lessonDto.setId(id);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return lessonMapper.toDto(lessonService.editLesson(authentication.getName(), lessonMapper.toEntity(lessonDto)));
	}

	/**
	 * @param id id of the lesson that should be denied
	 * @return cancellation success message
	 */
	@PreAuthorize(EDIT)
	@PostMapping("/{id}/deny")
	public String denyLesson(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		lessonService.denyLesson(authentication.getName(), id);
		return "Lesson with id " + id + " is denied";
	}

	/**
	 * @param id id of the lesson that should be deleted
	 * @return successful deletion message
	 */
	@PreAuthorize(DELETE)
	@DeleteMapping("/{id}")
	public String deleteLesson(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		lessonService.deleteLesson(authentication.getName(), id);
		return "Lesson with id " + id + " is deleted";
	}

	/**
	 * @return list of lessons to which the student is subscribed
	 */
	@PreAuthorize(SUBSCRIBE)
	@GetMapping("/subscriptions")
	public List<LessonDto> getSubscriptions() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return lessonService.getSubscriptions(authentication.getName()).stream().map(lessonMapper::toDto).collect(Collectors.toList());
	}

	/**
	 * @param id id of the lesson to which the student should be subscribed
	 * @return message about subscription success
	 */
	@PreAuthorize(SUBSCRIBE)
	@PostMapping("/{id}/subscribe")
	public String subscribeLesson(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (lessonService.subscribe(authentication.getName(), id)) {
			return "You are subscribed to lesson with id " + id;
		}
		return "Subscription was failed. You were already subscribed to lesson with id " + id;
	}

	/**
	 * @param id id of the lesson to which the student should be unsubscribed
	 * @return message about success of canceling subscription
	 */
	@PreAuthorize(SUBSCRIBE)
	@PostMapping("/{id}/unsubscribe")
	public String unsubscribeLesson(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (lessonService.unsubscribe(authentication.getName(), id)) {
			return "You are unsubscribed to lesson with id " + id;
		}
		return "Unsubscription was failed. You were not subscribed to the lesson with id " + id;
	}

	/**
	 * @param id id of the lesson for which the teacher's salary should be counted
	 * @return message about counted salary
	 */
	@PreAuthorize(GET_SALARY)
	@GetMapping("/{id}/salary")
	public String countSalary(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Double salary = lessonService.countSalary(authentication.getName(), id);
		if (salary == null) {
			return "You don't have a salary, too few students";
		}
		return "Your salary for the lesson with id " + id + " is " + salary;
	}

	/**
	 * @param id id of the lesson which status should be updated
	 * @param currentTime time according to which the status should be updated
	 * @return message about success of status update
	 */
	@PreAuthorize(EDIT)
	@PostMapping("/{id}/update_status")
	public String updateLessonStatuses(@PathVariable Long id, @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm") LocalDateTime currentTime) {
		// automatic update by matching with current date is supported
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		lessonService.updateLessonStatus(authentication.getName(), currentTime, id);
		return "Your lesson status is updated according to the current time";
	}
}
