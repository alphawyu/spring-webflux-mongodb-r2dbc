package com.realworld.spring.webflux.service

import com.realworld.spring.webflux.dto.User
import com.realworld.spring.webflux.exceptions.InvalidRequestException
import com.realworld.spring.webflux.persistence.entity.UserEntity
import com.realworld.spring.webflux.persistence.repository.UserDataService
import com.realworld.spring.webflux.persistence.repository.UserRepository
import com.realworld.spring.webflux.security.JwtSigner
import com.realworld.spring.webflux.service.user.PasswordService
import com.realworld.spring.webflux.service.user.SecuredUserService
import com.realworld.spring.webflux.service.user.UserService
import com.realworld.spring.webflux.user.UserSession
import helpers.UserSamples
import helpers.coCatchThrowable
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import reactor.kotlin.core.publisher.toMono
import java.util.stream.Stream
import kotlin.String
import kotlin.test.assertEquals

internal class UserServiceTest {
    companion object {
        val passwordService = PasswordService()
        val userRepository = mockk<UserRepository>()
        val userDataService = UserDataService(userRepository)
        val securedUserService = SecuredUserService(
            userDataService = userDataService,
            passwordService = passwordService,
            userTokenProvider = JwtSigner(),
        )
        val userService = UserService(
            userDataService = userDataService,
            securedUserService = securedUserService,
        )

        @JvmStatic
        fun signUpTestDataProvider(): Stream<Arguments?> {
            return Stream.of(
                Arguments.of(true, false, "Email: already in use"),
                Arguments.of(false, true, "Username: already in use")
            )
        }
    }

    @Nested
    @DisplayName("Test user sign up")
    inner class UserServiceSignUp {
        @ParameterizedTest
        @MethodSource("com.realworld.spring.webflux.service.UserServiceTest#signUpTestDataProvider")
        fun `should throw error, when signup find duplicates`(iExistsEByEmail: Boolean, iExistsByUsername: Boolean, oErrMsg: String) {
            every { userRepository.existsByEmail(any()) } returns iExistsEByEmail.toMono()
            every { userRepository.existsByUsername(any()) } returns iExistsByUsername.toMono()

            val throwable = coCatchThrowable {
                userService.signup(UserSamples.sampleUserRegistrationRequest())
            }

            assertThat(throwable)
                .isInstanceOf(InvalidRequestException::class.java)
                .hasMessage(oErrMsg)
        }

        @Test
        fun `should return user view, when user successfully sign up`() = runTest() {
            val testUe = UserEntity(
                username = "un", encodedPassword = "epw", email = "t@t.com", id = 123
            )
            every { userRepository.existsByEmail(any()) } returns false.toMono()
            every { userRepository.existsByUsername(any()) } returns false.toMono()
            every { userRepository.save(any()) } returns testUe.toMono()

            val userView = userService.signup(UserSamples.sampleUserRegistrationRequest())

            assertNotNull(userView)
            assertEquals(userView.email, testUe.email)
            assertEquals(userView.username, testUe.username)
        }
    }

    @Nested
    @DisplayName("Test user login")
    inner class UserServiceLogin {
        @Test
        fun `should throw error when login with unregistered user`() {
            every { userRepository.findByEmail(any()) } returns emailNotFoundException().map { it.toUserEntity() }

            val throwable = coCatchThrowable { userService.login(UserSamples.sampleUserAuthenticationRequest()) }

            assertThat(throwable)
                .isInstanceOf(InvalidRequestException::class.java)
                .hasMessage("Email: not found")
        }

        @Test
        fun `should throw error when wrong password`() = runTest {
            every { userRepository.findByEmail(any()) } returns UserSamples.sampleUser().toUserEntity().toMono()

            val authRequest = UserSamples.sampleUserAuthenticationRequest().copy(password = "not default password")
            val throwable = coCatchThrowable { userService.login(authRequest) }

            assertThat(throwable)
                .isInstanceOf(InvalidRequestException::class.java)
                .hasMessage("Password: invalid")
        }
    }

    @Nested
    @DisplayName("Test user update")
    inner class UserServiceUpdateUser {
        @ParameterizedTest
        @MethodSource("com.realworld.spring.webflux.service.UserServiceTest#signUpTestDataProvider")
        fun `should throw error when update user find duplicates`(iExistsEByEmail: Boolean, iExistsByUsername: Boolean, oErrMsg: String) {
            every { userRepository.existsByEmail(any()) } returns iExistsEByEmail.toMono()
            every { userRepository.existsByUsername(any()) } returns iExistsByUsername.toMono()
            val user = UserSamples.sampleUser()
            every { userRepository.findById(any<Long>()) } returns user.toUserEntity().toMono()

            val updateRequest = UserSamples.sampleUpdateUserRequest()
            val throwable = coCatchThrowable { userService.updateUser(updateRequest, UserSession(user, "token")) }

            assertThat(throwable)
                .isInstanceOf(InvalidRequestException::class.java)
                .hasMessage(oErrMsg)
        }

//        @Test
//        fun `should throw error when update user with duplicate username`() {
//            every { userRepository.existsByEmail(any()) } returns false.toMono()
//            every { userRepository.existsByUsername(any()) } returns true.toMono()
//            val user = UserSamples.sampleUser()
//            every { userRepository.findById(any<Long>()) } returns user.toUserEntity().toMono()
//
//            val updateRequest = UserSamples.sampleUpdateUserRequest()
//            val throwable = coCatchThrowable { userService.updateUser(updateRequest, UserSession(user, "token")) }
//
//            assertThat(throwable)
//                .isInstanceOf(InvalidRequestException::class.java)
//                .hasMessage("Username: already in use")
//        }
    }
    private fun emailNotFoundException() = InvalidRequestException("Email", "not found").toMono<User>()
}