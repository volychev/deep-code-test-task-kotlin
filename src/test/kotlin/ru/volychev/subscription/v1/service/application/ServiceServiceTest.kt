package ru.volychev.subscription.v1.service.application

import ru.volychev.subscription.v1.service.domain.Service
import ru.volychev.subscription.v1.service.infrastructure.ServiceRepository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class ServiceServiceTest {
    @Mock
    private lateinit var serviceRepository: ServiceRepository

    @InjectMocks
    private lateinit var serviceService: ServiceService

    @Test
    fun `getAllServices should return page from repository`() {
        val pageRequest = PageRequest.of(0, 20)
        val firstService = Service("Premier")
        val secondService = Service("Disney+")
        val servicePage = PageImpl(listOf(firstService, secondService))

        whenever(serviceRepository.findAll(any<Pageable>()))
            .thenReturn(servicePage)

        val resultPage = serviceService.getAllServices(pageRequest)

        assertEquals(2, resultPage.totalElements)
        assertEquals("Premier", resultPage.content[0].name)
        assertEquals("Disney+", resultPage.content[1].name)
        verify(serviceRepository).findAll(pageRequest)
    }

    @Test
    fun `getServiceById should return service when found`() {
        val serviceIdentifier = 1L
        val service = Service("Premier").apply {
            id = serviceIdentifier
        }

        whenever(serviceRepository.findById(any()))
            .thenReturn(Optional.of(service))

        val resultService = serviceService.getServiceById(serviceIdentifier)

        assertNotNull(resultService)
        assertEquals(serviceIdentifier, resultService?.id)
        assertEquals("Premier", resultService?.name)
    }

    @Test
    fun `createService should save and return new service when name is unique`() {
        val serviceName = "Premier"
        val serviceDescription = "Premium video streaming service"

        whenever(serviceRepository.findByName(any()))
            .thenReturn(null)

        whenever(serviceRepository.save(any<Service>()))
            .thenAnswer {
                it.getArgument(0)
            }

        val createdService = serviceService.createService(serviceName, serviceDescription)

        assertNotNull(createdService)
        assertEquals(serviceName, createdService.name)
        assertEquals(serviceDescription, createdService.description)
        verify(serviceRepository).findByName(serviceName)
        verify(serviceRepository).save(any<Service>())
    }

    @Test
    fun `updateService should only update provided fields`() {
        val serviceIdentifier = 1L
        val originalService = Service("Premier", "Basic plan description").apply {
            id = serviceIdentifier
        }

        whenever(serviceRepository.findById(any()))
            .thenReturn(Optional.of(originalService))

        val updatedService = serviceService.updateService(serviceIdentifier, "Premier Pro", null)

        assertNotNull(updatedService)
        assertEquals("Premier Pro", updatedService?.name)
        assertEquals("Basic plan description", updatedService?.description)
    }

    @Test
    fun `deleteService should call repository delete`() {
        val serviceIdentifier = 1L

        serviceService.deleteService(serviceIdentifier)

        verify(serviceRepository).deleteById(serviceIdentifier)
    }
}
