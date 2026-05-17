package ru.volychev.subscription.v1.subscription.application

import ru.volychev.subscription.v1.service.domain.Service
import ru.volychev.subscription.v1.subscription.domain.Subscription
import ru.volychev.subscription.v1.subscription.domain.SubscriptionStatus
import ru.volychev.subscription.v1.subscription.infrastructure.SubscriptionRepository
import ru.volychev.subscription.v1.user.domain.User

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class SubscriptionExpirationSchedulerTest {
    @Mock
    private lateinit var subscriptionRepository: SubscriptionRepository

    @InjectMocks
    private lateinit var subscriptionExpirationScheduler: SubscriptionExpirationScheduler

    @Test
    fun `expireSubscriptions should update status to EXPIRED for all returned subscriptions`() {
        val service = Service("Premier")
        val firstSubscription = Subscription(User("Kirill"), service, status = SubscriptionStatus.ACTIVE)
        val secondSubscription = Subscription(User("DeepCode"), service, status = SubscriptionStatus.ACTIVE)

        whenever(subscriptionRepository.findExpiredSubscriptions(
            anyOrNull(),
            anyOrNull()
        )).thenReturn(listOf(firstSubscription, secondSubscription))

        subscriptionExpirationScheduler.expireSubscriptions()

        assertEquals(SubscriptionStatus.EXPIRED, firstSubscription.status)
        assertEquals(SubscriptionStatus.EXPIRED, secondSubscription.status)
    }

    @Test
    fun `expireSubscriptions should do nothing if no expired subscriptions found`() {
        whenever(subscriptionRepository.findExpiredSubscriptions(
            anyOrNull(),
            anyOrNull()
        )).thenReturn(emptyList())

        subscriptionExpirationScheduler.expireSubscriptions()

        verify(subscriptionRepository).findExpiredSubscriptions(
            anyOrNull(),
            anyOrNull()
        )
    }
}
