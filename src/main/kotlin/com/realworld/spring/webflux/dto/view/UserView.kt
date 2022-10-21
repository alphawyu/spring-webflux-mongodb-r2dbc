package com.realworld.spring.webflux.dto.view

import com.realworld.spring.webflux.dto.User
import com.realworld.spring.webflux.persistence.entity.UserEntity

data class UserView(
    val email: String,
    val token: String,
    val username: String,
    val bio: String?,
    val image: String?,
)

