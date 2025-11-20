package com.realworld.spring.webflux.dto.view

data class ProfileView(
    val username: String,
    val bio: String? = null,
    val image: String? = null,
    val following: Boolean,
)
