package com.realworld.springmongo.service.user

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordService {
    private val encoder = BCryptPasswordEncoder()

    fun encodePassword(rowPassword: String): String = encoder.encode(rowPassword)

    fun matchesRawPasswordWithEncodedPassword(rawPassword: String, encodedPassword: String): Boolean =
        encoder.matches(rawPassword, encodedPassword)
}