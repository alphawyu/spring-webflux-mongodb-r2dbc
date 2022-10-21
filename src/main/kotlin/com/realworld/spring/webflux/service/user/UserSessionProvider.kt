package com.realworld.spring.webflux.user

import com.realworld.spring.webflux.exceptions.InvalidRequestException
import com.realworld.spring.webflux.dto.User
import com.realworld.spring.webflux.persistence.repository.UserDataService
import com.realworld.spring.webflux.persistence.repository.UserRepository
import com.realworld.spring.webflux.security.TokenPrincipal
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component

@Component
class UserSessionProvider(private val userDataService: UserDataService) {

    suspend fun getCurrentUserOrNull(): User? = getCurrentUserSessionOrNull()?.user

    suspend fun getCurrentUserOrFail(): User = getCurrentUserSessionOrFail().user

    suspend fun getCurrentUserSessionOrFail() =
        getCurrentUserSessionOrNull() ?: throw InvalidRequestException("User", "current user is not login in")

    suspend fun getCurrentUserSessionOrNull(): UserSession? {
        val context = ReactiveSecurityContextHolder.getContext().awaitSingleOrNull() ?: return null
        val tokenPrincipal = context.authentication.principal as TokenPrincipal
        val user = userDataService.findById(tokenPrincipal.userId.toLong())
        return UserSession(user, tokenPrincipal.token)
    }
}

data class UserSession(
    val user: User,
    val token: String,
)