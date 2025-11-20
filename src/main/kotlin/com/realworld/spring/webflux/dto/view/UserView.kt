package com.realworld.spring.webflux.dto.view

data class UserView(
    val email: String,
    val token: String,
    val username: String,
    val bio: String?,
    val image: String?,
)

