package com.tsemkalo.senlaExperienceExchangeApp.dao.impl;

import com.tsemkalo.senlaExperienceExchangeApp.dao.ReviewDao;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Review;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Component
public class ReviewDaoImpl extends AbstractDaoImpl<Review> implements ReviewDao {
	@Override
	public Class<Review> getEntityClass() {
		return Review.class;
	}

	private List<Review> getReviews(String item, Long itemId) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Review> criteriaQuery = criteriaBuilder.createQuery(Review.class);
		Root<Review> rootEntry = criteriaQuery.from(Review.class);
		CriteriaQuery<Review> reviews = criteriaQuery.select(rootEntry)
				.where(criteriaBuilder.equal(
						rootEntry.get(item).get("id"), itemId
				));
		return entityManager.createQuery(reviews).getResultList();
	}

	@Override
	public List<Review> getLessonReviews(Long lessonId) {
		return getReviews("lesson", lessonId);
	}

	@Override
	public List<Review> getCourseReviews(Long courseId) {
		return getReviews("course", courseId);
	}

	@Override
	public List<Review> getCourseLessonsReviews(Long courseId) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Review> criteriaQuery = criteriaBuilder.createQuery(Review.class);
		Root<Review> rootEntry = criteriaQuery.from(Review.class);
		CriteriaQuery<Review> reviews = criteriaQuery.select(rootEntry)
				.where(criteriaBuilder.equal(
						rootEntry.get("lesson").get("course").get("id"), courseId)
				);
		return entityManager.createQuery(reviews).getResultList();
	}
}
