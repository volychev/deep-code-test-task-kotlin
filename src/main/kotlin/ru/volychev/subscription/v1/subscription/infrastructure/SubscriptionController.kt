package ru.volychev.subscription.v1.subscription.infrastructure

import ru.volychev.subscription.v1.config.ApiPaths
import ru.volychev.subscription.v1.service.application.ServiceService
import ru.volychev.subscription.v1.subscription.application.CreateSubscriptionRequest
import ru.volychev.subscription.v1.subscription.application.RenewSubscriptionRequest
import ru.volychev.subscription.v1.subscription.application.SubscriptionDto
import ru.volychev.subscription.v1.subscription.application.SubscriptionService
import ru.volychev.subscription.v1.subscription.domain.SubscriptionStatus
import ru.volychev.subscription.v1.user.application.UserService

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

@RestController
@RequestMapping(ApiPaths.SUBSCRIPTIONS)
class SubscriptionController(
    private val subscriptionService: SubscriptionService,
    private val userService: UserService,
    private val serviceService: ServiceService
) {
    @GetMapping
    fun getAllSubscriptions(
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false) serviceId: Long?,
        @RequestParam(required = false) status: SubscriptionStatus?,
        @RequestParam(required = false) dateFrom: LocalDate?,
        @RequestParam(required = false) dateTo: LocalDate?,
        @PageableDefault(size = 25) pageable: Pageable
    ): Page<SubscriptionDto> {
        val subscriptions = subscriptionService.getAllSubscriptions(
            userId,
            serviceId,
            status,
            dateFrom,
            dateTo,
            pageable
        )
        return subscriptions.map {
            SubscriptionDto.fromDomain(it)
        }
    }

    @GetMapping("/{subscriptionId}")
    fun getSubscriptionById(
        @PathVariable subscriptionId: Long
    ): SubscriptionDto {
        val subscription = subscriptionService.getSubscriptionById(subscriptionId) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Subscription not found"
        )
        return SubscriptionDto.fromDomain(subscription)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createSubscription(
        @Valid @RequestBody request: CreateSubscriptionRequest
    ): SubscriptionDto {
        val user = userService.getUserById(request.userId) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "User with ID ${request.userId} not found"
        )
        val service = serviceService.getServiceById(request.serviceId) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Service with ID ${request.serviceId} not found"
        )

        try {
            val subscription = subscriptionService.createSubscription(
                user,
                service,
                request.price,
                request.dateFrom,
                request.dateTo
            )
            return SubscriptionDto.fromDomain(subscription)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @PatchMapping("/{subscriptionId}/suspend")
    fun suspendSubscription(
        @PathVariable subscriptionId: Long
    ): SubscriptionDto {
        try {
            val subscription = subscriptionService.suspendSubscription(subscriptionId) ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Subscription not found"
            )
            return SubscriptionDto.fromDomain(subscription)
        } catch (e: IllegalStateException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @PatchMapping("/{subscriptionId}/cancel")
    fun cancelSubscription(
        @PathVariable subscriptionId: Long
    ): SubscriptionDto {
        try {
            val subscription = subscriptionService.cancelSubscription(subscriptionId) ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Subscription not found"
            )
            return SubscriptionDto.fromDomain(subscription)
        } catch (e: IllegalStateException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @PatchMapping("/{subscriptionId}/activate")
    fun activateSubscription(
        @PathVariable subscriptionId: Long
    ): SubscriptionDto {
        try {
            val subscription = subscriptionService.activateSubscription(subscriptionId) ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Subscription not found"
            )
            return SubscriptionDto.fromDomain(subscription)
        } catch (e: IllegalStateException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @PatchMapping("/{subscriptionId}/renew")
    fun renewSubscription(
        @PathVariable subscriptionId: Long,
        @Valid @RequestBody request: RenewSubscriptionRequest
    ): SubscriptionDto {
        try {
            val subscription = subscriptionService.renewSubscription(
                subscriptionId,
                request.newDateTo
            ) ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Subscription not found"
            )
            return SubscriptionDto.fromDomain(subscription)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @GetMapping("/user/{userId}/active")
    fun getActiveSubscriptionsByUserId(
        @PathVariable userId: Long
    ): List<SubscriptionDto> {
        if (userService.getUserById(userId) == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        }

        return subscriptionService.getActiveSubscriptionsByUserId(userId).map {
            SubscriptionDto.fromDomain(it)
        }
    }
}
