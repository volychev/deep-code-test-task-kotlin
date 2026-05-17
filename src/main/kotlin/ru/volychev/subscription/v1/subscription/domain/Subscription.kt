package ru.volychev.subscription.v1.subscription.domain

import ru.volychev.subscription.v1.service.domain.Service
import ru.volychev.subscription.v1.user.domain.User

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate

enum class SubscriptionStatus {
    ACTIVE,
    SUSPENDED,
    CANCELED,
    EXPIRED,
}

@Entity
@Table(name = "subscription")
class Subscription(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User = User(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    val service: Service = Service(),

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    var price: BigDecimal = BigDecimal.ZERO,

    @Column(name = "date_from", nullable = false)
    var dateFrom: LocalDate = LocalDate.now(),

    @Column(name = "date_to", nullable = false)
    var dateTo: LocalDate = LocalDate.now().plusMonths(1),

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: SubscriptionStatus = SubscriptionStatus.ACTIVE,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    override fun toString(): String {
        return "Subscription(id=$id, service=${service.name}, price=$price, status=$status)"
    }
}
