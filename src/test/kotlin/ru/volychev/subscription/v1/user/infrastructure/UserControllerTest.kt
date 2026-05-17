package ru.volychev.subscription.v1.user.infrastructure

import ru.volychev.subscription.v1.user.application.CreateUserRequest
import ru.volychev.subscription.v1.user.application.UpdateUserRequest
import ru.volychev.subscription.v1.user.application.UserService
import ru.volychev.subscription.v1.user.domain.User

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

@WebMvcTest(UserController::class)
class UserControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var userService: UserService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `getAllUsers should return page of users`() {
        val userList = listOf(User("Kirill").apply {
            id = 1L
        })
        val userPage = PageImpl(userList)

        whenever(userService.getAllUsers(any()))
            .thenReturn(userPage)

        mockMvc.perform(get("/api/v1/users"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].id").value(1L))
            .andExpect(jsonPath("$.content[0].name").value("Kirill"))
    }

    @Test
    fun `getUserById should return user when exists`() {
        val userIdentifier = 1L
        val user = User("Kirill").apply {
            id = userIdentifier
        }

        whenever(userService.getUserById(anyOrNull()))
            .thenReturn(user)

        mockMvc.perform(get("/api/v1/users/$userIdentifier"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(userIdentifier))
            .andExpect(jsonPath("$.name").value("Kirill"))
    }

    @Test
    fun `getUserById should return 404 when not found`() {
        val userIdentifier = 1L

        whenever(userService.getUserById(anyOrNull()))
            .thenReturn(null)

        mockMvc.perform(get("/api/v1/users/$userIdentifier"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `createUser should return 201 and created user`() {
        val userName = "Kirill"
        val createUserRequest = CreateUserRequest(userName)
        val createdUser = User(userName).apply {
            id = 1L
        }

        whenever(userService.createUser(anyOrNull()))
            .thenReturn(createdUser)

        mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createUserRequest)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value(userName))
    }

    @Test
    fun `updateUserNameById should return updated user`() {
        val userIdentifier = 1L
        val updatedName = "Kirill DeepCode"
        val updateUserRequest = UpdateUserRequest(updatedName)
        val updatedUser = User(updatedName).apply {
            id = userIdentifier
        }

        whenever(userService.updateUserNameById(anyOrNull(), anyOrNull()))
            .thenReturn(updatedUser)

        mockMvc.perform(patch("/api/v1/users/$userIdentifier")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateUserRequest)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value(updatedName))
    }

    @Test
    fun `deleteUserById should return 204`() {
        val userIdentifier = 1L

        mockMvc.perform(delete("/api/v1/users/$userIdentifier"))
            .andExpect(status().isNoContent)
    }
}
