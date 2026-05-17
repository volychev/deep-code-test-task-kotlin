package ru.volychev.subscription.v1.user.application

import ru.volychev.subscription.v1.user.domain.User
import ru.volychev.subscription.v1.user.infrastructure.UserRepository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service as SpringService
import org.springframework.transaction.annotation.Transactional

@SpringService
class UserService(
    private val userRepository: UserRepository,
) {
    fun getAllUsers(pageable: Pageable): Page<User> {
        return userRepository.findAll(pageable)
    }

    fun getUserById(userIdentifier: Long): User? {
        return userRepository.findByIdOrNull(userIdentifier)
    }

    @Transactional
    fun createUser(userName: String): User {
        if (userRepository.findByName(userName) != null) {
            throw IllegalArgumentException("User already exists")
        }
        return userRepository.save(User(userName))
    }

    @Transactional
    fun updateUserNameById(userIdentifier: Long, newName: String?): User? {
        val user = userRepository.findByIdOrNull(userIdentifier) ?: return null
        
        newName?.let {
            user.name = it
        }
        
        return user
    }

    @Transactional
    fun deleteUserById(userIdentifier: Long) {
        userRepository.deleteById(userIdentifier)
    }
}
