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

import static com.tsemkalo.experienceApp.enums.PermissionType.Authorities.ADD;
import static com.tsemkalo.experienceApp.enums.PermissionType.Authorities.DELETE;
import static com.tsemkalo.experienceApp.enums.PermissionType.Authorities.EDIT;
import static com.tsemkalo.experienceApp.enums.PermissionType.Authorities.GET_SALARY;
import static com.tsemkalo.experienceApp.enums.PermissionType.Authorities.READ;
import static com.tsemkalo.experienceApp.enums.PermissionType.Authorities.SUBSCRIBE;

@Slf4j
@RestController
@RequestMapping("/lessons")
public class LessonController {
	@Autowired
	private LessonService lessonService;

	@Autowired
	private LessonMapper lessonMapper;

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

	@PreAuthorize(ADD)
	@PutMapping
	public LessonDto addLesson(@RequestBody LessonDto lessonDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return lessonMapper.toDto(lessonService.addLesson(authentication.getName(), lessonMapper.toEntity(lessonDto)));
	}

	@PreAuthorize(EDIT)
	@PostMapping("/{id}")
	public LessonDto editLesson(@RequestBody LessonDto lessonDto, @PathVariable Long id) {
		lessonDto.setId(id);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return lessonMapper.toDto(lessonService.editLesson(authentication.getName(), lessonMapper.toEntity(lessonDto)));
	}

	@PreAuthorize(EDIT)
	@PostMapping("/deny")
	public String denyLesson(@RequestParam Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		lessonService.denyLesson(authentication.getName(), id);
		return "Lesson with id " + id + " is denied";
	}

	@PreAuthorize(DELETE)
	@DeleteMapping
	public String deleteLesson(@RequestParam Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		lessonService.deleteLesson(authentication.getName(), id);
		return "Lesson with id " + id + " is deleted";
	}

	@PreAuthorize(SUBSCRIBE)
	@GetMapping("/subscriptions")
	public List<LessonDto> getSubscriptions() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return lessonService.getSubscriptions(authentication.getName()).stream().map(lessonMapper::toDto).collect(Collectors.toList());
	}

	@PreAuthorize(SUBSCRIBE)
	@PostMapping("/subscribe")
	public String subscribeLesson(@RequestParam Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (lessonService.subscribe(authentication.getName(), id)) {
			return "You are subscribed to lesson with id " + id;
		}
		return "Subscription was failed. You were already subscribed to lesson with id " + id;
	}

	@PreAuthorize(SUBSCRIBE)
	@PostMapping("/unsubscribe")
	public String unsubscribeLesson(@RequestParam Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (lessonService.unsubscribe(authentication.getName(), id)) {
			return "You are unsubscribed to lesson with id " + id;
		}
		return "Unsubscription was failed. You were not subscribed to the lesson with id " + id;
	}

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
}
