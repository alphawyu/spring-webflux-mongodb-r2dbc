package com.realworld.springmongo.service.user

interface UserTokenProvider {
    fun getToken(userId: String): String
}