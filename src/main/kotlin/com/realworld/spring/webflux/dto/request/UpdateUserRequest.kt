package com.realworld.spring.webflux.dto.request

import com.realworld.spring.webflux.validation.NotBlankOrNull
import jakarta.validation.constraints.Email

data class UpdateUserRequest(
    @field:Email
    @field:NotBlankOrNull
    val email: String? = null,
    @field:NotBlankOrNull
    val username: String? = null,
    @field:NotBlankOrNull
    val password: String? = null,
    val image: String? = null,
    val bio: String? = null,
)
