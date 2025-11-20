package com.realworld.spring.webflux.security

import helpers.ImportAppSecurity
import helpers.SupportTestConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpStatus
import org.springframework.security.config.Customizer
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import org.springframework.test.context.ContextConfiguration;

@WebFluxTest(controllers = [SecurityTest.TestController::class])
@ContextConfiguration(classes = [SupportTestConfig::class])
@ImportAppSecurity
@Import(SecurityTest.Configuration::class,)
internal class SecurityTest(
    @Autowired val client: WebTestClient,
    @Autowired val signer: JwtSigner,
) {

    @Test
    fun `should return 201`() {
        val status = client.get()
            .uri("/permitAll")
            .exchange()
            .expectBody<String>()
            .returnResult()
            .status
        assertThat(status).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `should return 401`() {
        val status = client.get()
            .uri("/authenticated")
            .exchange()
            .expectBody<String>()
            .returnResult()
            .status
        assertThat(status).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `should return user id`() {
        val userId = "1"
        val token = signer.generateToken(userId)
        val result = client.get()
            .uri("/authenticated")
            .header("Authorization", "Token $token")
            .exchange()
            .expectBody(TokenPrincipal::class.java)
            .returnResult()
        val status = result.status
        val body = result.responseBody!!
        assertThat(status).isEqualTo(HttpStatus.OK)
        assertThat(body.userId).isEqualTo(userId)
        assertThat(body.token).isEqualTo(token)
    }

    @TestConfiguration
    internal class Configuration {
        @Bean
        fun testController() = TestController()

        @Bean
        @Primary
        fun testEndpointsConfig() = Customizer<AuthorizeExchangeSpec> { http ->
            http.pathMatchers("/permitAll").permitAll()
                .pathMatchers("/authenticated").authenticated()
        }
    }

    @RestController
    internal class TestController {
        @GetMapping("/authenticated")
        fun token(@AuthenticationPrincipal principalMono: Mono<TokenPrincipal>): Mono<TokenPrincipal> {
            return principalMono
        }

        @GetMapping("/permitAll")
        fun free() {
        }
    }
}

