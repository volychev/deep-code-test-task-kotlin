package ru.volychev.subscription.v1.service.infrastructure

import ru.volychev.subscription.v1.service.domain.Service

import org.springframework.data.jpa.repository.JpaRepository

interface ServiceRepository : JpaRepository<Service, Long> {
    fun findByName(name: String): Service?
}
