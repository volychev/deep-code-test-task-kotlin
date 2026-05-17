package ru.volychev.subscription.v1.service.application

import ru.volychev.subscription.v1.service.domain.Service

import jakarta.validation.constraints.Size

data class ServiceDto(
    val id: Long?,
    val name: String,
    val description: String?
) {
    companion object {
        fun fromDomain(service: Service): ServiceDto {
            return ServiceDto(
                id = service.id,
                name = service.name,
                description = service.description
            )
        }
    }
}

data class CreateServiceRequest(
    @field:Size(min = 1, max = 64)
    var name: String,

    @field:Size(max = 255)
    var description: String?
)

data class UpdateServiceRequest(
    @field:Size(min = 1, max = 64)
    var newName: String?,

    @field:Size(max = 255)
    var newDescription: String?
)
