package com.realworld.spring.webflux.dto.request

import com.realworld.spring.webflux.dto.User
import com.realworld.spring.webflux.persistence.entity.UserEntity
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class UserRegistrationRequest(
    @field:NotBlank
    val username: String,
    @field:Email
    val email: String,
    @field:NotBlank
    val password: String,
) {
    fun toUser(encodedPassword: String) = User(
        encodedPassword = encodedPassword,
        email = email,
        username = username,
    )
}