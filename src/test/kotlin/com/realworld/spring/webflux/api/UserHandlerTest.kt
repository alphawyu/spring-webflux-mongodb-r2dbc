package com.realworld.spring.webflux.api

import com.ninjasquad.springmockk.MockkBean
import com.realworld.spring.webflux.dto.User
import com.realworld.spring.webflux.dto.request.UpdateUserRequest
import com.realworld.spring.webflux.dto.request.UserAuthenticationRequest
import com.realworld.spring.webflux.dto.request.UserRegistrationRequest
import com.realworld.spring.webflux.dto.view.ProfileView
import com.realworld.spring.webflux.dto.view.UserView
import com.realworld.spring.webflux.security.SecurityTest.TestController
import com.realworld.spring.webflux.service.article.ArticleService
import com.realworld.spring.webflux.service.user.UserService
import com.realworld.spring.webflux.user.UserSession
import com.realworld.spring.webflux.user.UserSessionProvider
import helpers.ImportAppSecurity
import helpers.SupportTestConfig
import io.mockk.coEvery
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.security.config.Customizer
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.json.JsonCompareMode
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest
@ContextConfiguration(classes = [SupportTestConfig::class])
@ImportAppSecurity
@Import(
    RouterConfig::class,
    UserHandler::class,
    ArticleHandler::class,
    UserHandlerTest.Configuration::class
)
class UserHandlerTest(@Autowired val webTestClient: WebTestClient) {
    @MockkBean
    private lateinit var mockUserService: UserService

    @MockkBean
    private lateinit var mockArticleService: ArticleService

    @MockkBean
    private lateinit var mockUserSessionProvider: UserSessionProvider

    val expUserView = UserView(
        email = "t@t.com",
        token = "randomString",
        username = "testUserName",
        bio = null,
        image = null
    )
    val expUserViewStr = """
                {"user":{"email":"t@t.com","token":"randomString","image": null ,"username":"testUserName","bio":null}}
            """
    val expUser = User(
        email = "t@t.com",
        username = "testUserName",
        encodedPassword = "",
        bio = null
    )

    @Nested
    inner class UserUnitTests {
        @BeforeEach
        fun setup() {

        }

        @Test
        @DisplayName("test post /users/signup .. {UserRegistrationRequest} - happy path")
        fun test_signup() {
            val regReq = UserWrapper<UserRegistrationRequest>(
                content = UserRegistrationRequest(
                    email = "t@t.com",
                    username = "testUserName",
                    password = "tpw",
                )
            )
            coEvery { mockUserService.signup(any()) } returns expUserView
            webTestClient.post().uri("/api/users")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(regReq)
                .exchange()
                .expectStatus().isCreated
                .expectBody()
                .json(
                    expUserViewStr, JsonCompareMode.STRICT
                )
        }

        @Test
        @DisplayName("test post /users/login .. {UserAuthenticationRequest} - happy path")
        fun test_login() {
            val regReq = UserWrapper(
                content = UserAuthenticationRequest(
                    email = "t@t.com",
                    password = "tpw",
                )
            )
            coEvery { mockUserService.login(any()) } returns expUserView
            webTestClient.post().uri("/api/users/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(regReq)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .json(expUserViewStr, JsonCompareMode.STRICT)
        }

        @Test
        @DisplayName("test get /user - happy path")
        fun test_getCurrentUser() {
            coEvery { mockUserService.signup(any()) } returns expUserView
            coEvery { mockUserSessionProvider.getCurrentUserSessionOrFail() } returns UserSession(
                user = expUser,
                token = ""
            )
            webTestClient.get().uri("/api/user")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .json(
                    """
                {"user":{"email":"t@t.com","token":"","image": null ,"username":"testUserName","bio":null}}
            """, JsonCompareMode.STRICT
                )
        }

        @Test
        @DisplayName("put /user .. {UpdateUserRequest} - happy path")
        fun test_updateUser() {
            val regReq = UserWrapper(
                content = UpdateUserRequest(
                    email = "t@t.com",
                    password = "tpw",
                )
            )
            coEvery { mockUserService.updateUser(any(), any()) } returns expUserView
            coEvery { mockUserSessionProvider.getCurrentUserSessionOrFail() } returns UserSession(
                user = expUser,
                token = ""
            )
            webTestClient.put().uri("/api/user")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(regReq)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .json(expUserViewStr, JsonCompareMode.STRICT)
        }
    }


    @Nested
    inner class ProfileUnitTests {
        val expProfileView = ProfileView(
            username = "testUserName",
            bio = null,
            image = null,
            following = false
        )
        val expProfileViewStr = """
                {"profile":{"username":"testUserName","bio":null,"image":null,"following":false}}
            """

        @BeforeEach
        fun setup() {
            coEvery { mockUserSessionProvider.getCurrentUserOrNull() } returns expUser
            coEvery { mockUserSessionProvider.getCurrentUserOrFail() } returns expUser
        }

        @Test
        @DisplayName("test get /profiles/{username} - happy path")
        fun test_getProfile() {
            coEvery { mockUserService.getProfile(any(), any()) } returns expProfileView
            webTestClient.get().uri("/api/profiles/testUsername")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .json(expProfileViewStr, JsonCompareMode.STRICT)
        }

        @Test
        @DisplayName("test post /profiles/{username}/follow - happy path")
        fun test_follow() {
            coEvery { mockUserService.follow(any(), any()) } returns expProfileView
            webTestClient.post().uri("/api/profiles/testUsername/follow")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .json(expProfileViewStr, JsonCompareMode.STRICT)
        }

        @Test
        @DisplayName("test delete /profiles/{username}/follow - happy path")
        fun test_unfollow() {
            coEvery { mockUserService.unfollow(any(), any()) } returns expProfileView
            webTestClient.delete().uri("/api/profiles/testUsername/follow")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .json(expProfileViewStr, JsonCompareMode.STRICT)
        }

    }


    @TestConfiguration
    internal class Configuration {
        @Bean
        fun testController() = TestController()

        @Bean
        @Primary
        fun testEndpointsConfig() = Customizer<AuthorizeExchangeSpec> { http ->
            http.pathMatchers("/**").permitAll()
        }
    }
}

