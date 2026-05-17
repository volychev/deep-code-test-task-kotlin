package ru.volychev.subscription.v1.user.infrastructure

import ru.volychev.subscription.v1.config.ApiPaths
import ru.volychev.subscription.v1.user.application.CreateUserRequest
import ru.volychev.subscription.v1.user.application.UpdateUserRequest
import ru.volychev.subscription.v1.user.application.UserDto
import ru.volychev.subscription.v1.user.application.UserService

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping(ApiPaths.USERS)
class UserController(
    private val userService: UserService,
) {
    @GetMapping
    fun getAllUsers(
        @PageableDefault(size = 25) pageable: Pageable
    ): Page<UserDto> {
        return userService.getAllUsers(pageable)
            .map {
                UserDto.fromDomain(it)
            }
    }

    @GetMapping("/{userIdentifier}")
    fun getUserById(
        @PathVariable userIdentifier: Long
    ): UserDto {
        val user = userService.getUserById(userIdentifier) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "User not found"
        )
        return UserDto.fromDomain(user)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(
        @Valid @RequestBody request: CreateUserRequest
    ): UserDto {
        try {
            val user = userService.createUser(request.name)
            return UserDto.fromDomain(user)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @PatchMapping("/{userIdentifier}")
    fun updateUserNameById(
        @PathVariable userIdentifier: Long,
        @Valid @RequestBody request: UpdateUserRequest,
    ): UserDto {
        val user = userService.updateUserNameById(userIdentifier, request.newName) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "User not found"
        )
        return UserDto.fromDomain(user)
    }

    @DeleteMapping("/{userIdentifier}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUserById(
        @PathVariable userIdentifier: Long
    ) {
        userService.deleteUserById(userIdentifier)
    }
}
