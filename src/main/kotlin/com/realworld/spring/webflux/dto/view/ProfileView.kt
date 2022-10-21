package com.realworld.spring.webflux.dto.view

data class ProfileView(
    val username: String,
    val bio: String?,
    val image: String?,
    val following: Boolean,
)
