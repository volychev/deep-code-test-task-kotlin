package ru.volychev.subscription.v1.subscription.infrastructure

import ru.volychev.subscription.v1.service.application.ServiceService
import ru.volychev.subscription.v1.service.domain.Service
import ru.volychev.subscription.v1.subscription.application.CreateSubscriptionRequest
import ru.volychev.subscription.v1.subscription.application.SubscriptionService
import ru.volychev.subscription.v1.subscription.domain.Subscription
import ru.volychev.subscription.v1.subscription.domain.SubscriptionStatus
import ru.volychev.subscription.v1.user.application.UserService
import ru.volychev.subscription.v1.user.domain.User

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.LocalDate

@WebMvcTest(SubscriptionController::class)
class SubscriptionControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var subscriptionService: SubscriptionService

    @MockitoBean
    private lateinit var userService: UserService

    @MockitoBean
    private lateinit var serviceService: ServiceService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `getAllSubscriptions should return page`() {
        val user = User("Kirill").apply {
            id = 1L
        }
        val service = Service("Premier").apply {
            id = 1L
        }
        val subscription = Subscription(
            user = user,
            service = service,
            price = BigDecimal("9.99")
        ).apply {
            id = 1L
        }
        val subscriptionPage = PageImpl(listOf(subscription))

        whenever(subscriptionService.getAllSubscriptions(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            any()
        )).thenReturn(subscriptionPage)

        mockMvc.perform(get("/api/v1/subscriptions"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].id").value(1L))
            .andExpect(jsonPath("$.content[0].serviceName").value("Premier"))
    }

    @Test
    fun `createSubscription should return 201 when user and service exist`() {
        val userIdentifier = 1L
        val serviceIdentifier = 2L
        val user = User("Kirill").apply {
            id = userIdentifier
        }
        val service = Service("Premier").apply {
            id = serviceIdentifier
        }
        val price = BigDecimal("9.99")
        val request = CreateSubscriptionRequest(userIdentifier, serviceIdentifier, price, LocalDate.now(), LocalDate.now().plusMonths(1))
        val created = Subscription(
            user = user,
            service = service,
            price = price,
            dateFrom = request.dateFrom,
            dateTo = request.dateTo
        ).apply {
            id = 10L
        }

        whenever(userService.getUserById(any())).thenReturn(user)
        whenever(serviceService.getServiceById(any())).thenReturn(service)
        whenever(subscriptionService.createSubscription(any(), any(), any(), any(), any())).thenReturn(created)

        mockMvc.perform(post("/api/v1/subscriptions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(10L))
            .andExpect(jsonPath("$.price").value(9.99))
    }

    @Test
    fun `suspendSubscription should return updated subscription`() {
        val subscriptionIdentifier = 1L
        val user = User("Kirill").apply {
            id = 1L
        }
        val service = Service("Premier").apply {
            id = 1L
        }
        val suspended = Subscription(
            user = user,
            service = service
        ).apply {
            id = subscriptionIdentifier
            status = SubscriptionStatus.SUSPENDED
        }

        whenever(subscriptionService.suspendSubscription(any())).thenReturn(suspended)

        mockMvc.perform(patch("/api/v1/subscriptions/$subscriptionIdentifier/suspend"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("SUSPENDED"))
    }
}
