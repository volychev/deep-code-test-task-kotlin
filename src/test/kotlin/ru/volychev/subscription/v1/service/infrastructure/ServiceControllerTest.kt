package ru.volychev.subscription.v1.service.infrastructure

import ru.volychev.subscription.v1.service.application.CreateServiceRequest
import ru.volychev.subscription.v1.service.application.ServiceService
import ru.volychev.subscription.v1.service.application.UpdateServiceRequest
import ru.volychev.subscription.v1.service.domain.Service

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ServiceController::class)
class ServiceControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var serviceService: ServiceService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `getAllServices should return page of services`() {
        val serviceList = listOf(Service("Premier").apply {
            id = 1L
        })
        val servicePage = PageImpl(serviceList)

        whenever(serviceService.getAllServices(any()))
            .thenReturn(servicePage)

        mockMvc.perform(get("/api/v1/services"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].id").value(1L))
            .andExpect(jsonPath("$.content[0].name").value("Premier"))
    }

    @Test
    fun `getServiceById should return service when exists`() {
        val serviceIdentifier = 1L
        val service = Service("Premier").apply {
            id = serviceIdentifier
        }

        whenever(serviceService.getServiceById(any()))
            .thenReturn(service)

        mockMvc.perform(get("/api/v1/services/$serviceIdentifier"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(serviceIdentifier))
            .andExpect(jsonPath("$.name").value("Premier"))
    }

    @Test
    fun `createService should return 201 and created service`() {
        val serviceName = "Premier"
        val serviceDescription = "Premium streaming service"
        val createServiceRequest = CreateServiceRequest(serviceName, serviceDescription)
        val createdService = Service(serviceName, serviceDescription).apply {
            id = 1L
        }

        whenever(serviceService.createService(any(), anyOrNull()))
            .thenReturn(createdService)

        mockMvc.perform(post("/api/v1/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createServiceRequest)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value(serviceName))
    }

    @Test
    fun `updateService should return updated service`() {
        val serviceIdentifier = 1L
        val updatedName = "Premier Pro"
        val updatedDescription = "Enhanced streaming plan"
        val updateServiceRequest = UpdateServiceRequest(updatedName, updatedDescription)
        val updatedService = Service(updatedName, updatedDescription).apply {
            id = serviceIdentifier
        }

        whenever(serviceService.updateService(anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(updatedService)

        mockMvc.perform(patch("/api/v1/services/$serviceIdentifier")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateServiceRequest)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value(updatedName))
    }

    @Test
    fun `deleteService should return 204`() {
        val serviceIdentifier = 1L
        mockMvc.perform(delete("/api/v1/services/$serviceIdentifier"))
            .andExpect(status().isNoContent)
    }
}
