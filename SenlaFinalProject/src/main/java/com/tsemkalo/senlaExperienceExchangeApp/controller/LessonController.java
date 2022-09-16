package com.tsemkalo.senlaExperienceExchangeApp.controller;

import com.tsemkalo.senlaExperienceExchangeApp.dto.LessonDto;
import com.tsemkalo.senlaExperienceExchangeApp.dto.SearchFilterDto;
import com.tsemkalo.senlaExperienceExchangeApp.dto.mappers.LessonMapper;
import com.tsemkalo.senlaExperienceExchangeApp.service.LessonService;
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
import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.PermissionsForController.SUBSCRIBE;

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
	 *
	 * @param searchFilterDto filter parameters:
	 *                        status - filter by status (is the lesson started, finished and etc),
	 *                        teacherName - filter by teacher name,
	 *                        teacherSurname - filter by teacher surname,
	 *                        theme - filter by lesson theme,
	 *                        minRating - find lesson with rating not less than given,
	 *                        online - find online / offline lessons,
	 *                        address - filter by address,
	 *                        free - find only free lessons,
	 *                        minCost - find lessons with cost not less than given,
	 *                        maxCost - find lessons with cost not greater than given,
	 *                        numberOfPlaces - find lessons with number of places left not less than given,
	 *                        individual - find individual / group lessons,
	 *                        from - find lessons starting after given date,
	 *                        to - find lessons finishing before given date
	 * @return filtered list of lessons
	 */
	@GetMapping
	public List<LessonDto> getLessons(@RequestBody SearchFilterDto searchFilterDto) {
		List<Predicate> predicates = new ArrayList<>();
		if (searchFilterDto.getTeacherName() != null && searchFilterDto.getTeacherSurname() != null) {
			predicates.add(lessonService.getTeacherPredicate(searchFilterDto.getTeacherName(), searchFilterDto.getTeacherSurname()));
		}
		if (searchFilterDto.getTheme() != null) {
			predicates.add(lessonService.getThemePredicate(searchFilterDto.getTheme()));
		}
		if (searchFilterDto.getMinRating() != null) {
			predicates.add(lessonService.getRatingPredicate(searchFilterDto.getMinRating()));
		}
		if (searchFilterDto.getAddress() != null || searchFilterDto.getOnline() != null) {
			predicates.add(lessonService.getOnlineOfflinePredicate(searchFilterDto.getOnline(), searchFilterDto.getAddress()));
		}
		if (searchFilterDto.getFree() != null || searchFilterDto.getMinCost() != null || searchFilterDto.getMaxCost() != null) {
			predicates.add(lessonService.getCostPredicate(searchFilterDto.getFree(), searchFilterDto.getMinCost(), searchFilterDto.getMaxCost()));
		}
		if (searchFilterDto.getIndividual() != null) {
			predicates.add(lessonService.getIndividualPredicate(searchFilterDto.getIndividual()));
		}
		predicates.add(lessonService.getStatusPredicate(searchFilterDto.getStatus()));
		if (searchFilterDto.getFrom() != null || searchFilterDto.getTo() != null) {
			predicates.add(lessonService.getDatePredicate(searchFilterDto.getFrom(), searchFilterDto.getTo()));
		}
		if (searchFilterDto.getNumberOfPlaces() != null) {
			predicates.add(lessonService.getFreePlacesLeftPredicate(searchFilterDto.getNumberOfPlaces()));
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
	 * @param id        id of the edited lesson
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
	public ResponseEntity<Object> denyLesson(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		lessonService.denyLesson(authentication.getName(), id);
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("message",  "Lesson with id " + id + " is denied");
		return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
	}

	/**
	 * @param id id of the lesson that should be deleted
	 * @return successful deletion message
	 */
	@PreAuthorize(DELETE)
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteLesson(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		lessonService.deleteLesson(authentication.getName(), id);
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("message",  "Lesson with id " + id + " is deleted");
		return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
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
	public ResponseEntity<Object> subscribeLesson(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Map<String, Object> body = new LinkedHashMap<>();
		if (lessonService.subscribe(authentication.getName(), id)) {
			body.put("message",  "You are subscribed to lesson with id " + id);
		} else {
			body.put("message", "Subscription was failed. You were already subscribed to lesson with id " + id);
		}
		return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
	}

	/**
	 * @param id id of the lesson to which the student should be unsubscribed
	 * @return message about success of canceling subscription
	 */
	@PreAuthorize(SUBSCRIBE)
	@PostMapping("/{id}/unsubscribe")
	public ResponseEntity<Object> unsubscribeLesson(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Map<String, Object> body = new LinkedHashMap<>();
		if (lessonService.unsubscribe(authentication.getName(), id)) {
			body.put("message",  "You are unsubscribed to lesson with id " + id);
		} else {
			body.put("message", "Unsubscription was failed. You were not subscribed to the lesson with id " + id);
		}
		return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
	}

	/**
	 * @param id id of the lesson for which the teacher's salary should be counted
	 * @return message about counted salary
	 */
	@PreAuthorize(GET_SALARY)
	@GetMapping("/{id}/salary")
	public ResponseEntity<Object> countSalary(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Double salary = lessonService.countSalary(authentication.getName(), id);
		Map<String, Object> body = new LinkedHashMap<>();
		if (salary == null) {
			body.put("message",  "You don't have a salary, too few students");
		} else {
			body.put("message", "Your salary for the lesson with id " + id + " is " + salary);
		}
		return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
	}

	/**
	 * @param id          id of the lesson which status should be updated
	 * @param currentTime time according to which the status should be updated
	 * @return message about success of status update
	 */
	@PreAuthorize(EDIT)
	@PostMapping("/{id}/update_status")
	public ResponseEntity<Object> updateLessonStatuses(@PathVariable Long id, @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm") LocalDateTime currentTime) {
		// automatic update by matching with current date is supported
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		lessonService.updateLessonStatus(authentication.getName(), currentTime, id);
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("message",  "Your lesson status is updated according to the current time");
		return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
	}
}
