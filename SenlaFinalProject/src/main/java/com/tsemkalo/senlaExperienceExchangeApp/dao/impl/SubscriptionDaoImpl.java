package com.tsemkalo.senlaExperienceExchangeApp.dao.impl;

import com.tsemkalo.senlaExperienceExchangeApp.dao.SubscriptionDao;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Course;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Lesson;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Subscription;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SubscriptionDaoImpl extends AbstractDaoImpl<Subscription> implements SubscriptionDao {
	@Override
	public Class<Subscription> getEntityClass() {
		return Subscription.class;
	}

	@Override
	public Subscription getSubscription(Long userId, Long lessonId) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Subscription> criteriaQuery = criteriaBuilder.createQuery(Subscription.class);
		Root<Subscription> rootEntry = criteriaQuery.from(Subscription.class);
		Predicate userPredicate = criteriaBuilder.equal(rootEntry.get("user").get("id"), userId);
		Predicate lessonPredicate = criteriaBuilder.equal(rootEntry.get("lesson").get("id"), lessonId);
		CriteriaQuery<Subscription> subscriptions = criteriaQuery.select(rootEntry)
				.where(
						criteriaBuilder.and(userPredicate, lessonPredicate)
				);
		if (entityManager.createQuery(subscriptions).getResultList().isEmpty()) {
			return null;
		}
		return entityManager.createQuery(subscriptions).getSingleResult();
	}

	@Override
	public List<Lesson> getSubscriptionLessons(Long userId) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Subscription> criteriaQuery = criteriaBuilder.createQuery(Subscription.class);
		Root<Subscription> rootEntry = criteriaQuery.from(Subscription.class);
		Predicate userPredicate = criteriaBuilder.equal(rootEntry.get("user").get("id"), userId);
		Predicate lessonPredicate = criteriaBuilder.isNull(rootEntry.get("lesson").get("course"));
		CriteriaQuery<Subscription> subscriptions = criteriaQuery.select(rootEntry)
				.where(
						criteriaBuilder.and(userPredicate, lessonPredicate)
				);
		return entityManager.createQuery(subscriptions).getResultList().stream()
				.map(Subscription::getLesson).collect(Collectors.toList());
	}

	@Override
	public List<Course> getSubscriptionCourses(Long userId) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Subscription> criteriaQuery = criteriaBuilder.createQuery(Subscription.class);
		Root<Subscription> rootEntry = criteriaQuery.from(Subscription.class);
		Predicate userPredicate = criteriaBuilder.equal(rootEntry.get("user").get("id"), userId);
		Predicate coursePredicate = criteriaBuilder.isNotNull(rootEntry.get("lesson").get("course"));
		CriteriaQuery<Subscription> subscriptions = criteriaQuery.select(rootEntry)
				.where(
						criteriaBuilder.and(userPredicate, coursePredicate)
				);
		return entityManager.createQuery(subscriptions).getResultList().stream()
				.map(subscription -> subscription.getLesson().getCourse()).distinct().collect(Collectors.toList());
	}
}
