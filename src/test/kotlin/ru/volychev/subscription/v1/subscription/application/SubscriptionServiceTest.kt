package ru.volychev.subscription.v1.subscription.application

import ru.volychev.subscription.v1.service.domain.Service
import ru.volychev.subscription.v1.subscription.domain.Subscription
import ru.volychev.subscription.v1.subscription.domain.SubscriptionStatus
import ru.volychev.subscription.v1.subscription.infrastructure.SubscriptionRepository
import ru.volychev.subscription.v1.user.domain.User

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class SubscriptionServiceTest {
    @Mock
    private lateinit var subscriptionRepository: SubscriptionRepository

    @InjectMocks
    private lateinit var subscriptionService: SubscriptionService

    @Test
    fun `getAllSubscriptions should return page from repository`() {
        val pageRequest = PageRequest.of(0, 20)
        val subscription = Subscription(User("Kirill"), Service("Premier"), BigDecimal("9.99"))
        val subscriptionPage = PageImpl(listOf(subscription))

        whenever(subscriptionRepository.findAll(any<Specification<Subscription>>(), eq(pageRequest)))
            .thenReturn(subscriptionPage)

        val resultPage = subscriptionService.getAllSubscriptions(null, null, null, null, null, pageRequest)

        assertEquals(1, resultPage.totalElements)
        assertEquals("Premier", resultPage.content[0].service.name)
    }

    @Test
    fun `createSubscription should save and return new subscription when dates and price are valid`() {
        val user = User("Kirill")
        val service = Service("Premier")
        val price = BigDecimal("9.99")
        val dateFrom = LocalDate.now()
        val dateTo = dateFrom.plusMonths(1)

        whenever(subscriptionRepository.save(any<Subscription>()))
            .thenAnswer {
                it.getArgument(0)
            }

        val createdSubscription = subscriptionService.createSubscription(user, service, price, dateFrom, dateTo)

        assertNotNull(createdSubscription)
        assertEquals(user, createdSubscription.user)
        assertEquals(service, createdSubscription.service)
        assertEquals(price, createdSubscription.price)
        assertEquals(SubscriptionStatus.ACTIVE, createdSubscription.status)
    }

    @Test
    fun `createSubscription should throw exception when price is negative`() {
        val user = User("Kirill")
        val service = Service("Premier")
        val price = BigDecimal("-1.0")

        val exception = assertThrows(IllegalArgumentException::class.java) {
            subscriptionService.createSubscription(user, service, price, LocalDate.now(), LocalDate.now().plusMonths(1))
        }

        assertEquals("Price cannot be negative", exception.message)
    }

    @Test
    fun `createSubscription should throw exception when dateTo is before dateFrom`() {
        val user = User("Kirill")
        val service = Service("Premier")
        val dateFrom = LocalDate.now()
        val dateTo = dateFrom.minusDays(1)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            subscriptionService.createSubscription(user, service, BigDecimal.ZERO, dateFrom, dateTo)
        }

        assertEquals("End date (dateTo) cannot be before start date (dateFrom)", exception.message)
    }

    @Test
    fun `suspendSubscription should change status to SUSPENDED when subscription is ACTIVE`() {
        val subscriptionIdentifier = 1L
        val activeSubscription = Subscription(User("Kirill"), Service("Premier")).apply {
            id = subscriptionIdentifier
        }

        whenever(subscriptionRepository.findById(any()))
            .thenReturn(Optional.of(activeSubscription))

        val suspendedSubscription = subscriptionService.suspendSubscription(subscriptionIdentifier)

        assertNotNull(suspendedSubscription)
        assertEquals(SubscriptionStatus.SUSPENDED, suspendedSubscription?.status)
    }

    @Test
    fun `activateSubscription should throw exception when status is EXPIRED`() {
        val subscriptionIdentifier = 1L
        val expiredSubscription = Subscription(
            User("Kirill"),
            Service("Premier"),
            status = SubscriptionStatus.EXPIRED,
            dateTo = LocalDate.now().plusDays(1)
        ).apply {
            id = subscriptionIdentifier
        }

        whenever(subscriptionRepository.findById(any()))
            .thenReturn(Optional.of(expiredSubscription))

        val exception = assertThrows(IllegalStateException::class.java) {
            subscriptionService.activateSubscription(subscriptionIdentifier)
        }

        assertEquals("Cannot activate an expired subscription without renewing it", exception.message)
    }

    @Test
    fun `renewSubscription should update dateTo and set status to ACTIVE`() {
        val subscriptionIdentifier = 1L
        val oldDateTo = LocalDate.now().plusDays(1)
        val newDateTo = LocalDate.now().plusMonths(2)
        val subscription = Subscription(
            User("Kirill"),
            Service("Premier"),
            dateTo = oldDateTo,
            status = SubscriptionStatus.EXPIRED
        ).apply {
            id = subscriptionIdentifier
        }

        whenever(subscriptionRepository.findById(any()))
            .thenReturn(Optional.of(subscription))

        val renewedSubscription = subscriptionService.renewSubscription(subscriptionIdentifier, newDateTo)

        assertNotNull(renewedSubscription)
        assertEquals(newDateTo, renewedSubscription?.dateTo)
        assertEquals(SubscriptionStatus.ACTIVE, renewedSubscription?.status)
    }
}
