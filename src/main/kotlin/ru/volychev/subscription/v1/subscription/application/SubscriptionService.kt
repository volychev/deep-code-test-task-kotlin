package ru.volychev.subscription.v1.subscription.application

import ru.volychev.subscription.v1.service.domain.Service
import ru.volychev.subscription.v1.subscription.domain.Subscription
import ru.volychev.subscription.v1.subscription.domain.SubscriptionStatus
import ru.volychev.subscription.v1.subscription.infrastructure.SubscriptionRepository
import ru.volychev.subscription.v1.user.domain.User

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service as SpringService
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@SpringService
class SubscriptionService(
    private val subscriptionRepository: SubscriptionRepository,
) {
    fun getAllSubscriptions(
        userId: Long?,
        serviceId: Long?,
        status: SubscriptionStatus?,
        dateFrom: LocalDate?,
        dateTo: LocalDate?,
        pageable: Pageable
    ): Page<Subscription> {
        val specification = Specification<Subscription> { root, _, cb ->
            val predicates = mutableListOf<jakarta.persistence.criteria.Predicate>()

            userId?.let {
                predicates.add(cb.equal(root.get<User>("user").get<Long>("id"), it))
            }
            serviceId?.let {
                predicates.add(cb.equal(root.get<Service>("service").get<Long>("id"), it))
            }
            status?.let {
                predicates.add(cb.equal(root.get<SubscriptionStatus>("status"), it))
            }
            dateFrom?.let {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateFrom"), it))
            }
            dateTo?.let {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateTo"), it))
            }

            cb.and(*predicates.toTypedArray())
        }
        return subscriptionRepository.findAll(specification, pageable)
    }

    fun getSubscriptionById(subscriptionIdentifier: Long): Subscription? =
        subscriptionRepository.findByIdOrNull(subscriptionIdentifier)

    @Transactional
    fun createSubscription(
        user: User,
        service: Service,
        price: BigDecimal,
        dateFrom: LocalDate,
        dateTo: LocalDate
    ): Subscription {
        if (dateTo.isBefore(dateFrom)) {
            throw IllegalArgumentException("End date (dateTo) cannot be before start date (dateFrom)")
        }
        if (price < BigDecimal.ZERO) {
            throw IllegalArgumentException("Price cannot be negative")
        }
        return subscriptionRepository.save(Subscription(
            user = user,
            service = service,
            price = price,
            dateFrom = dateFrom,
            dateTo = dateTo,
            status = SubscriptionStatus.ACTIVE,
        ))
    }

    @Transactional
    fun suspendSubscription(subscriptionIdentifier: Long): Subscription? {
        val subscription = subscriptionRepository.findByIdOrNull(subscriptionIdentifier) ?: return null

        if (subscription.status == SubscriptionStatus.EXPIRED) {
            throw IllegalStateException("Cannot suspend an already expired subscription")
        }

        subscription.status = SubscriptionStatus.SUSPENDED
        return subscription
    }

    @Transactional
    fun cancelSubscription(subscriptionIdentifier: Long): Subscription? {
        val subscription = subscriptionRepository.findByIdOrNull(subscriptionIdentifier) ?: return null

        subscription.status = SubscriptionStatus.CANCELED
        return subscription
    }

    @Transactional
    fun activateSubscription(subscriptionIdentifier: Long): Subscription? {
        val subscription = subscriptionRepository.findByIdOrNull(subscriptionIdentifier) ?: return null
        val isDateExpired = subscription.dateTo.isBefore(LocalDate.now())

        if (subscription.status == SubscriptionStatus.EXPIRED || isDateExpired) {
            throw IllegalStateException("Cannot activate an expired subscription without renewing it")
        }

        subscription.status = SubscriptionStatus.ACTIVE
        return subscription
    }

    @Transactional
    fun renewSubscription(subscriptionIdentifier: Long, newDateTo: LocalDate): Subscription? {
        val subscription = subscriptionRepository.findByIdOrNull(subscriptionIdentifier) ?: return null

        if (newDateTo.isBefore(LocalDate.now()) || newDateTo.isBefore(subscription.dateTo)) {
            throw IllegalArgumentException("New expiration date must be in the future and after current expiration date")
        }

        subscription.dateTo = newDateTo
        subscription.status = SubscriptionStatus.ACTIVE
        return subscription
    }

    fun getActiveSubscriptionsByUserId(userId: Long): List<Subscription> {
        return subscriptionRepository.findAllByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
    }
}
