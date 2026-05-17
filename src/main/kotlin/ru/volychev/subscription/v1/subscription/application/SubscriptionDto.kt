package ru.volychev.subscription.v1.subscription.application

import ru.volychev.subscription.v1.subscription.domain.Subscription
import ru.volychev.subscription.v1.subscription.domain.SubscriptionStatus

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate

data class SubscriptionDto(
    val id: Long?,
    val userId: Long?,
    val serviceId: Long?,
    val serviceName: String,
    val price: BigDecimal,
    val dateFrom: LocalDate,
    val dateTo: LocalDate,
    val status: SubscriptionStatus
) {
    companion object {
        fun fromDomain(subscription: Subscription): SubscriptionDto {
            return SubscriptionDto(
                id = subscription.id,
                userId = subscription.user.id,
                serviceId = subscription.service.id,
                serviceName = subscription.service.name,
                price = subscription.price,
                dateFrom = subscription.dateFrom,
                dateTo = subscription.dateTo,
                status = subscription.status
            )
        }
    }
}

data class CreateSubscriptionRequest(
    @field:NotNull
    var userId: Long,

    @field:NotNull
    var serviceId: Long,

    @field:NotNull
    @field:DecimalMin("0.0")
    var price: BigDecimal,

    @field:NotNull
    var dateFrom: LocalDate,

    @field:NotNull
    var dateTo: LocalDate
)

data class RenewSubscriptionRequest(
    @field:NotNull
    var newDateTo: LocalDate
)
