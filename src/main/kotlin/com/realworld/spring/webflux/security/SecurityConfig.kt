package com.realworld.spring.webflux.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
        webFilter: AuthenticationWebFilter?,
        endpointsConfig: Customizer<AuthorizeExchangeSpec>
    ): SecurityWebFilterChain = http.authorizeExchange(endpointsConfig)
        .addFilterAt(webFilter, SecurityWebFiltersOrder.AUTHENTICATION)
        .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
        .cors(ServerHttpSecurity.CorsSpec::disable)
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
        .logout(ServerHttpSecurity.LogoutSpec::disable)
        .build();

    @Bean
    fun endpointsConfig() = Customizer<AuthorizeExchangeSpec> { http ->
        http
            .pathMatchers(HttpMethod.POST, "/api/users", "/api/users/login").permitAll()
            .pathMatchers(HttpMethod.GET, "/api/profiles/**").permitAll()
            .pathMatchers(HttpMethod.GET, "/api/articles/**").permitAll()
            .pathMatchers(HttpMethod.GET, "/api/tags/**").permitAll()
            .anyExchange().authenticated()
    }
}

