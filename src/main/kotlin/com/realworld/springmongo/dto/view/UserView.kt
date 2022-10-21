package com.realworld.springmongo.dto.view

import com.realworld.springmongo.dto.User
import com.realworld.springmongo.persistence.entity.UserEntity

data class UserView(
    val email: String,
    val token: String,
    val username: String,
    val bio: String?,
    val image: String?,
)

