package ru.volychev.subscription.v1.subscription.application

import ru.volychev.subscription.v1.subscription.domain.SubscriptionStatus
import ru.volychev.subscription.v1.subscription.infrastructure.SubscriptionRepository

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class SubscriptionExpirationScheduler(
    private val subscriptionRepository: SubscriptionRepository,
) {
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    fun expireSubscriptions() {
        val expiredSubscriptions = subscriptionRepository.findExpiredSubscriptions(
            status = SubscriptionStatus.ACTIVE,
            date = LocalDate.now(),
        )

        expiredSubscriptions.forEach {
            it.status = SubscriptionStatus.EXPIRED
        }
    }
}