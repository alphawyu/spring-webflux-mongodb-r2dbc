package com.realworld.springmongo.dto.view

data class ProfileView(
    val username: String,
    val bio: String?,
    val image: String?,
    val following: Boolean,
)
