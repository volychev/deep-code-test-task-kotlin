package ru.volychev.subscription.v1.user.application

import ru.volychev.subscription.v1.user.domain.User

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserDto(
    val id: Long?,
    val name: String,
) {
    companion object {
        fun fromDomain(user: User): UserDto {
            return UserDto(
                id = user.id,
                name = user.name
            )
        }
    }
}

data class CreateUserRequest(
    @field:NotBlank
    @field:Size(max = 64)
    var name: String,
)

data class UpdateUserRequest(
    @field:NotBlank
    @field:Size(max = 64)
    var newName: String,
)
