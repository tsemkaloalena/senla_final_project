package com.tsemkalo.senlaExperienceExchangeApp.service.impl;

import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Course;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Review;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.IncorrectDataException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReviewServiceImplTest extends AbstractServiceTest {
	@InjectMocks
	private ReviewServiceImpl reviewService = new ReviewServiceImpl();

	@BeforeAll
	public void init() {
		addSubscription(1L, 10L, 6L);
		addSubscription(2L, 10L, 7L);
		addSubscription(3L, 10L, 8L);
		addSubscription(4L, 10L, 9L);
		addSubscription(5L, 11L, 9L);
		addSubscription(6L, 12L, 9L);
	}

	@BeforeEach
	public void setupSubscriptionsReturn() {
		lenient().when(getSubscriptionDao().getSubscription(10L, 6L)).thenReturn(getSubscriptionTable().get(1L));
		lenient().when(getSubscriptionDao().getSubscription(10L, 7L)).thenReturn(getSubscriptionTable().get(2L));
		lenient().when(getSubscriptionDao().getSubscription(10L, 8L)).thenReturn(getSubscriptionTable().get(3L));
		lenient().when(getSubscriptionDao().getSubscription(10L, 9L)).thenReturn(getSubscriptionTable().get(4L));
		lenient().when(getSubscriptionDao().getSubscription(11L, 9L)).thenReturn(getSubscriptionTable().get(5L));
		lenient().when(getSubscriptionDao().getSubscription(12L, 9L)).thenReturn(getSubscriptionTable().get(6L));
		lenient().when(getSubscriptionDao().getSubscriptionCourses(10L)).thenReturn(new ArrayList<>(List.of(new Course[]{getCourseTable().get(2L)})));
	}

	@Test
	@Order(1)
	public void createReview_whenRatingIsNotSet_thenIncorrectDataExceptionThrown() {
		String currentUsername = "edik";
		Long lessonId = 9L;
		Review review = new Review(null, "I didn't remember anything", null, getLessonTable().get(lessonId), null);

		assertThrows(IncorrectDataException.class, () -> reviewService.createReview(currentUsername, review));

		assertTrue(getReviewTable().isEmpty());
	}

	@Test
	@Order(2)
	public void createReview_whenLessonOrCourseIdIsNotSet_thenIncorrectDataExceptionThrown() {
		String currentUsername = "edik";
		Review review = new Review(3, "I didn't remember anything", null, null, null);

		assertThrows(IncorrectDataException.class, () -> reviewService.createReview(currentUsername, review));

		assertTrue(getReviewTable().isEmpty());
	}

	@Test
	@Order(3)
	public void createReview_whenBothLessonAndCourseIdsAreSet_thenIncorrectDataExceptionThrown() {
		String currentUsername = "edik";
		Long lessonId = 9L;
		Long courseId = 2L;
		Review review = new Review(3, "I didn't remember anything", null, getLessonTable().get(lessonId), getCourseTable().get(courseId));

		assertThrows(IncorrectDataException.class, () -> reviewService.createReview(currentUsername, review));

		assertTrue(getReviewTable().isEmpty());
	}

	@Test
	@Order(4)
	public void createReview_givenLessonId_whenUserIsNotSubscribed_thenAccessDeniedExceptionThrown() {
		String currentUsername = "edik";
		Long lessonId = 6L;
		Review review = new Review(3, "I didn't remember anything", null, getLessonTable().get(lessonId), null);

		assertThrows(AccessDeniedException.class, () -> reviewService.createReview(currentUsername, review));

		assertTrue(getReviewTable().isEmpty());
	}

	@Test
	@Order(5)
	public void createReview_givenLessonId_whenAllDataIsCorrect_thenSuccess() {
		String currentUsername = "edik";
		Long lessonId = 9L;
		Review review = new Review(3, "I didn't remember anything", null, getLessonTable().get(lessonId), null);

		Long reviewId = reviewService.createReview(currentUsername, review);

		assertTrue(getReviewTable().containsKey(reviewId));
		assertEquals(3.0, getLessonTable().get(lessonId).getAverageRating());
		assertEquals(currentUsername, getReviewTable().get(reviewId).getUser().getUsername());
	}

	@Test
	@Order(6)
	public void createReview_givenCourseId_whenUserIsNotSubscribed_thenAccessDeniedExceptionThrown() {
		String currentUsername = "edik";
		Long courseId = 2L;
		Review review = new Review(5, "It was very interesting", null, null, getCourseTable().get(courseId));

		assertThrows(AccessDeniedException.class, () -> reviewService.createReview(currentUsername, review));
	}

	@Test
	@Order(7)
	public void createReview_givenCourseId_whenAllDataIsCorrect_thenSuccess() {
		String currentUsername = "marik";
		Long courseId = 2L;
		Review review = new Review(5, "It was very interesting", null, null, getCourseTable().get(courseId));

		Long reviewId = reviewService.createReview(currentUsername, review);

		assertTrue(getReviewTable().containsKey(reviewId));
	}

	@Test
	@Order(8)
	public void createReview_whenReviewIsCreated_thenRatingIsRecounted() {
		Long lessonId = 9L;
		Review review1 = new Review(4, "Thank you", getUserTable().get(10L), getLessonTable().get(lessonId), null);
		Review review2 = new Review(5, "It was nice to talk to teacher", getUserTable().get(12L), getLessonTable().get(lessonId), null);
		Review review3 = new Review(5, "A lot of new information", getUserTable().get(11L), getLessonTable().get(lessonId), null);

		Long reviewId1 = reviewService.createReview("marik", review1);
		Long reviewId2 = reviewService.createReview("derevo", review2);
		Long reviewId3 = reviewService.createReview("edik", review3);

		assertTrue(getReviewTable().containsKey(reviewId1));
		assertTrue(getReviewTable().containsKey(reviewId2));
		assertTrue(getReviewTable().containsKey(reviewId3));
		Double sum = 0.0;
		Integer k = 0;
		for (Review review : getReviewTable().values()) {
			if (review.getLesson() != null && lessonId.equals(review.getLesson().getId())) {
				sum += review.getRating();
				k++;
			}
		}
		assertEquals(sum / k, getLessonTable().get(lessonId).getAverageRating());
	}

	@Test
	@Order(9)
	public void editReview_whenIdIsNotSet_thenIncorrectDataExceptionThrown() {
		String currentUsername = "edik";
		Review editedReview = new Review();
		editedReview.setRating(3);

		assertThrows(IncorrectDataException.class, () -> reviewService.editReview(currentUsername, editedReview));
	}

	@Test
	@Order(10)
	public void editReview_whenReviewDoesNotBelongToUser_thenAccessDeniedExceptionThrown() {
		String currentUsername = "edik";
		Review editedReview = new Review();
		editedReview.setRating(3);
		editedReview.setId(4L);
		Integer oldRating = getReviewTable().get(4L).getRating();

		assertThrows(AccessDeniedException.class, () -> reviewService.editReview(currentUsername, editedReview));

		assertEquals(oldRating, getReviewTable().get(4L).getRating());
	}

	@Test
	@Order(11)
	public void editReview_whenAllDataIsCorrect_thenSuccess() {
		Long lessonId = 9L;
		Long ratingId = 5L;
		String currentUsername = "edik";
		Review editedReview = new Review();
		editedReview.setRating(3);
		editedReview.setId(ratingId);
		Integer oldRating = getReviewTable().get(ratingId).getRating();
		Double oldAverageRating = getLessonTable().get(lessonId).getAverageRating();

		reviewService.editReview(currentUsername, editedReview);

		assertNotEquals(oldRating, getReviewTable().get(ratingId).getRating());
		assertNotEquals(oldAverageRating, getLessonTable().get(lessonId).getAverageRating());
	}

	@Test
	@Order(12)
	public void deleteReview_whenReviewDoesNotBelongToUser_thenAccessDeniedExceptionThrown() {
		String currentUsername = "edik";
		Long reviewId = 4L;
		Long lessonId = 9L;
		Double oldAverageRating = getLessonTable().get(lessonId).getAverageRating();

		assertThrows(AccessDeniedException.class, () -> reviewService.deleteReview(currentUsername, reviewId));

		assertTrue(getReviewTable().containsKey(reviewId));
		assertEquals(oldAverageRating, getLessonTable().get(lessonId).getAverageRating());
	}

	@Test
	@Order(13)
	public void deleteReview_whenCurrentUserIsAdmin_thenSuccess() {
		String currentUsername = "svetlana";
		Long reviewId = 4L;
		Long lessonId = 9L;
		Double oldAverageRating = getLessonTable().get(lessonId).getAverageRating();

		reviewService.deleteReview(currentUsername, reviewId);

		assertFalse(getReviewTable().containsKey(reviewId));
		assertNotEquals(oldAverageRating, getLessonTable().get(lessonId).getAverageRating());
	}

	@Test
	@Order(14)
	public void deleteReview_whenCurrentUserIsEqualToReviewAuthor_thenSuccess() {
		String currentUsername = "edik";
		Long reviewId = 5L;
		Long lessonId = 9L;
		Double oldAverageRating = getLessonTable().get(lessonId).getAverageRating();

		reviewService.deleteReview(currentUsername, reviewId);

		assertFalse(getReviewTable().containsKey(reviewId));
		assertNotEquals(oldAverageRating, getLessonTable().get(lessonId).getAverageRating());
	}
}
