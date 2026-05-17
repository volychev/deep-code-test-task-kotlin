package ru.volychev.subscription.v1.user.infrastructure

import ru.volychev.subscription.v1.user.domain.User

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByName(name: String): User?
}
