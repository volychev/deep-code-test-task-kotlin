package ru.volychev.subscription.v1.service.infrastructure

import ru.volychev.subscription.v1.config.ApiPaths
import ru.volychev.subscription.v1.service.application.CreateServiceRequest
import ru.volychev.subscription.v1.service.application.ServiceDto
import ru.volychev.subscription.v1.service.application.ServiceService
import ru.volychev.subscription.v1.service.application.UpdateServiceRequest

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
@RequestMapping(ApiPaths.SERVICES)
class ServiceController(
    private val serviceService: ServiceService
) {
    @GetMapping
    fun getAllServices(
        @PageableDefault(size = 25) pageable: Pageable
    ): Page<ServiceDto> {
        return serviceService.getAllServices(pageable)
            .map {
                ServiceDto.fromDomain(it)
            }
    }

    @GetMapping("/{serviceId}")
    fun getServiceById(
        @PathVariable serviceId: Long
    ): ServiceDto {
        val service = serviceService.getServiceById(serviceId) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Service not found"
        )
        return ServiceDto.fromDomain(service)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createService(
        @Valid @RequestBody request: CreateServiceRequest
    ): ServiceDto {
        try {
            val service = serviceService.createService(request.name, request.description)
            return ServiceDto.fromDomain(service)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @PatchMapping("/{serviceId}")
    fun updateService(
        @PathVariable serviceId: Long,
        @Valid @RequestBody request: UpdateServiceRequest
    ): ServiceDto {
        try {
            val service = serviceService.updateService(serviceId, request.newName, request.newDescription) ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Service not found"
            )
            return ServiceDto.fromDomain(service)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @DeleteMapping("/{serviceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteService(
        @PathVariable serviceId: Long
    ) {
        serviceService.deleteService(serviceId)
    }
}
