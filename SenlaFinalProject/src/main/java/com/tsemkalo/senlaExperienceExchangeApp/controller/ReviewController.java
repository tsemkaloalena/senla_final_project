package com.tsemkalo.senlaExperienceExchangeApp.controller;

import com.tsemkalo.senlaExperienceExchangeApp.dto.ReviewDto;
import com.tsemkalo.senlaExperienceExchangeApp.dto.mappers.ReviewMapper;
import com.tsemkalo.senlaExperienceExchangeApp.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.PermissionsForController.DELETE_REVIEW;
import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.PermissionsForController.READ;
import static com.tsemkalo.senlaExperienceExchangeApp.configuration.security.PermissionsForController.WRITE_REVIEW;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {
	@Autowired
	private ReviewService reviewService;

	@Autowired
	private ReviewMapper reviewMapper;

	/**
	 * @param id id of the lesson for which reviews should be returned
	 * @return reviews written about given lesson
	 */
	@PreAuthorize(READ)
	@GetMapping("/lessons/{id}")
	public List<ReviewDto> getLessonReviews(@PathVariable Long id) {
		return reviewService.getLessonReviews(id).stream().map(reviewMapper::toDto).collect(Collectors.toList());
	}

	/**
	 * @param id id of the course for which lesson reviews should be returned
	 * @return list of reviews written about lessons which belong to given course
	 */
	@PreAuthorize(READ)
	@GetMapping("/courses/{id}/lessons")
	public List<ReviewDto> getCourseLessonsReviews(@PathVariable Long id) {
		return reviewService.getCourseLessonsReviews(id).stream().map(reviewMapper::toDto).collect(Collectors.toList());
	}

	/**
	 * @param id id of the course for which reviews should be returned
	 * @return reviews written about given course
	 */
	@PreAuthorize(READ)
	@GetMapping("/courses/{id}")
	public List<ReviewDto> getCourseReviews(@PathVariable Long id) {
		return reviewService.getCourseReviews(id).stream().map(reviewMapper::toDto).collect(Collectors.toList());
	}

	/**
	 * @param reviewDto review data: rating, review text, lesson or course id (review can be written about only one item)
	 * @return feedback success message
	 */
	@PreAuthorize(WRITE_REVIEW)
	@PutMapping
	public ResponseEntity<Object> createReview(@RequestBody ReviewDto reviewDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Long reviewId = reviewService.createReview(authentication.getName(), reviewMapper.toEntity(reviewDto));
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("message",  "Thank you! Review number " + reviewId + " is successfully created.");
		return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
	}

	/**
	 * @param id        id of the edited review
	 * @param reviewDto reviewDto data that should be edited
	 * @return edited review data
	 */
	@PreAuthorize(WRITE_REVIEW)
	@PostMapping("/{id}")
	public ResponseEntity<Object> editReview(@PathVariable Long id, @RequestBody ReviewDto reviewDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		reviewDto.setId(id);
		reviewService.editReview(authentication.getName(), reviewMapper.toEntity(reviewDto));
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("message",  "Your review number " + reviewDto.getId() + " is successfully edited.");
		return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
	}

	/**
	 * @param id id of the review that should be deleted
	 * @return successful deletion message
	 */
	@PreAuthorize(DELETE_REVIEW)
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteReview(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		reviewService.deleteReview(authentication.getName(), id);
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("message",  "Review with id " + id + " is successfully deleted.");
		return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
	}
}
