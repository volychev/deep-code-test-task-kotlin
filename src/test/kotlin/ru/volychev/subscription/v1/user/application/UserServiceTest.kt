package ru.volychev.subscription.v1.user.application

import ru.volychev.subscription.v1.user.domain.User
import ru.volychev.subscription.v1.user.infrastructure.UserRepository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class UserServiceTest {
    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var userService: UserService

    @Test
    fun `getAllUsers should return page from repository`() {
        val pageRequest = PageRequest.of(0, 20)
        val firstUser = User("Kirill")
        val secondUser = User("DeepCode")
        val userPage = PageImpl(listOf(firstUser, secondUser))

        whenever(userRepository.findAll(any<Pageable>()))
            .thenReturn(userPage)

        val resultPage = userService.getAllUsers(pageRequest)

        assertEquals(2, resultPage.totalElements)
        assertEquals("Kirill", resultPage.content[0].name)
        assertEquals("DeepCode", resultPage.content[1].name)
        verify(userRepository).findAll(pageRequest)
    }

    @Test
    fun `getUserById should return user when found`() {
        val userIdentifier = 1L
        val user = User("Kirill").apply {
            id = userIdentifier
        }

        whenever(userRepository.findById(anyOrNull()))
            .thenReturn(Optional.of(user))

        val resultUser = userService.getUserById(userIdentifier)

        assertNotNull(resultUser)
        assertEquals(userIdentifier, resultUser?.id)
        assertEquals("Kirill", resultUser?.name)
    }

    @Test
    fun `getUserById should return null when not found`() {
        val userIdentifier = 1L

        whenever(userRepository.findById(anyOrNull()))
            .thenReturn(Optional.empty())

        val resultUser = userService.getUserById(userIdentifier)

        assertNull(resultUser)
    }

    @Test
    fun `createUser should save and return new user when name is unique`() {
        val userName = "Kirill"

        whenever(userRepository.findByName(anyOrNull()))
            .thenReturn(null)

        whenever(userRepository.save(any<User>()))
            .thenAnswer {
                it.getArgument(0)
            }

        val createdUser = userService.createUser(userName)

        assertNotNull(createdUser)
        assertEquals(userName, createdUser.name)
        verify(userRepository).findByName(userName)
        verify(userRepository).save(any<User>())
    }

    @Test
    fun `createUser should throw exception when user already exists`() {
        val userName = "Kirill"
        val existingUser = User(userName)

        whenever(userRepository.findByName(anyOrNull()))
            .thenReturn(existingUser)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            userService.createUser(userName)
        }

        assertEquals("User already exists", exception.message)
        verify(userRepository, never()).save(any<User>())
    }

    @Test
    fun `updateUserNameById should update name when user exists`() {
        val userIdentifier = 1L
        val user = User("Kirill").apply {
            id = userIdentifier
        }
        val updatedName = "Kirill DeepCode"

        whenever(userRepository.findById(anyOrNull()))
            .thenReturn(Optional.of(user))

        val updatedUser = userService.updateUserNameById(userIdentifier, updatedName)

        assertNotNull(updatedUser)
        assertEquals(updatedName, updatedUser?.name)
    }

    @Test
    fun `deleteUserById should call repository delete`() {
        val userIdentifier = 1L

        userService.deleteUserById(userIdentifier)

        verify(userRepository).deleteById(userIdentifier)
    }
}
