package com.realworld.spring.webflux.service.user

interface UserTokenProvider {
    fun getToken(userId: String): String
}