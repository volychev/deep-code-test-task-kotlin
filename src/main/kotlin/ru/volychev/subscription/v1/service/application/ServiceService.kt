package ru.volychev.subscription.v1.service.application

import ru.volychev.subscription.v1.service.domain.Service
import ru.volychev.subscription.v1.service.infrastructure.ServiceRepository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service as SpringService
import org.springframework.transaction.annotation.Transactional

@SpringService
class ServiceService(
    private val serviceRepository: ServiceRepository
) {
    fun getAllServices(pageable: Pageable): Page<Service> {
        return serviceRepository.findAll(pageable)
    }

    fun getServiceById(serviceIdentifier: Long): Service? {
        return serviceRepository.findByIdOrNull(serviceIdentifier)
    }

    @Transactional
    fun createService(name: String, description: String?): Service {
        if (serviceRepository.findByName(name) != null) {
            throw IllegalArgumentException("Service with name '$name' already exists")
        }
        val service = Service(
            name = name,
            description = description
        )
        return serviceRepository.save(service)
    }

    @Transactional
    fun updateService(serviceIdentifier: Long, newName: String?, newDescription: String?): Service? {
        val service = serviceRepository.findByIdOrNull(serviceIdentifier) ?: return null

        newName?.let {
            service.name = it
        }
        newDescription?.let {
            service.description = it
        }
        return service
    }

    @Transactional
    fun deleteService(serviceIdentifier: Long) {
        serviceRepository.deleteById(serviceIdentifier)
    }
}
