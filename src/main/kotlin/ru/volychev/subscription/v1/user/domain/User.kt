package ru.volychev.subscription.v1.user.domain

import ru.volychev.subscription.v1.subscription.domain.Subscription

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
    @Column(name = "name", nullable = false, length = 64)
    var name: String = "",
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val subscriptions: MutableList<Subscription> = mutableListOf()

    override fun toString(): String {
        return "User(id=$id, name='$name')"
    }
}
