package ru.volychev.subscription.v1.subscription.infrastructure

import ru.volychev.subscription.v1.subscription.domain.Subscription
import ru.volychev.subscription.v1.subscription.domain.SubscriptionStatus

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface SubscriptionRepository : JpaRepository<Subscription, Long>, JpaSpecificationExecutor<Subscription> {
    fun findAllByUserIdAndStatus(userId: Long, status: SubscriptionStatus): List<Subscription>

    @Query("SELECT s FROM Subscription s WHERE s.status = :status AND s.dateTo < :date")
    fun findExpiredSubscriptions(status: SubscriptionStatus, date: LocalDate): List<Subscription>
}
