package com.realworld.spring.webflux.persistence.repository

import com.realworld.spring.webflux.persistence.entity.UserEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono


interface UserRepository : ReactiveCrudRepository<UserEntity, Long> {
    fun findByEmail(email: String): Mono<UserEntity>

    fun existsByEmail(email: String): Mono<Boolean>

    fun existsByUsername(username: String): Mono<Boolean>

    fun findByUsername(username: String): Mono<UserEntity>
}

