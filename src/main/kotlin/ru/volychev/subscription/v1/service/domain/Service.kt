package ru.volychev.subscription.v1.service.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "service")
class Service(
    @Column(name = "name", nullable = false, length = 64)
    var name: String = "",

    @Column(name = "description", length = 255)
    var description: String? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    override fun toString(): String {
        return "Service(id=$id, name='$name')"
    }
}
