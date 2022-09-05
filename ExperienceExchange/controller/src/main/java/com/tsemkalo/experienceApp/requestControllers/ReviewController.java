package com.tsemkalo.experienceApp.requestControllers;

import com.tsemkalo.experienceApp.ReviewDto;
import com.tsemkalo.experienceApp.ReviewService;
import com.tsemkalo.experienceApp.mappers.ReviewMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.List;
import java.util.stream.Collectors;

import static com.tsemkalo.experienceApp.enums.PermissionType.Authorities.DELETE_REVIEW;
import static com.tsemkalo.experienceApp.enums.PermissionType.Authorities.READ;
import static com.tsemkalo.experienceApp.enums.PermissionType.Authorities.WRITE_REVIEW;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {
	@Autowired
	private ReviewService reviewService;

	@Autowired
	private ReviewMapper reviewMapper;

	@PreAuthorize(READ)
	@GetMapping("/lessons/{id}")
	public List<ReviewDto> getLessonReviews(@PathVariable Long id) {
		return reviewService.getLessonReviews(id).stream().map(reviewMapper::toDto).collect(Collectors.toList());
	}

	@PreAuthorize(READ)
	@GetMapping("/courses/{id}/lessons")
	public List<ReviewDto> getCourseLessonsReviews(@PathVariable Long id) {
		return reviewService.getCourseLessonsReviews(id).stream().map(reviewMapper::toDto).collect(Collectors.toList());
	}

	@PreAuthorize(READ)
	@GetMapping("/courses/{id}")
	public List<ReviewDto> getCourseReviews(@PathVariable Long id) {
		return reviewService.getCourseReviews(id).stream().map(reviewMapper::toDto).collect(Collectors.toList());
	}

	@PreAuthorize(WRITE_REVIEW)
	@PutMapping
	public String createReview(@RequestBody ReviewDto reviewDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Long reviewId = reviewService.createReview(authentication.getName(), reviewMapper.toEntity(reviewDto));
		return "Thank you! Review number " + reviewId + " is successfully created.";
	}

	@PreAuthorize(WRITE_REVIEW)
	@PostMapping
	public String editReview(@RequestBody ReviewDto reviewDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		reviewService.editReview(authentication.getName(), reviewMapper.toEntity(reviewDto));
		return "Your review number " + reviewDto.getId() + " is successfully edited.";
	}

	@PreAuthorize(DELETE_REVIEW)
	@DeleteMapping
	public String deleteReview(@RequestParam Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		reviewService.deleteReview(authentication.getName(), id);
		return "Review with id " + id + " is successfully deleted.";
	}
}
