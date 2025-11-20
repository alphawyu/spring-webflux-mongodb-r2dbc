package com.realworld.spring.webflux.api

import com.realworld.spring.webflux.dto.request.UpdateUserRequest
import com.realworld.spring.webflux.dto.request.UserAuthenticationRequest
import com.realworld.spring.webflux.dto.request.UserRegistrationRequest
import com.realworld.spring.webflux.service.user.UserService
import com.realworld.spring.webflux.user.UserSessionProvider
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class UserHandler(
    private val userService: UserService,
    private val userSessionProvider: UserSessionProvider
) {
    suspend fun signup(req: ServerRequest): ServerResponse {
        val body = req.awaitBody<UserWrapper<UserRegistrationRequest>>()
        return ServerResponse
            .status(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                userService.signup(body.content).toUserWrapper()
            )
    }

    suspend fun login(req: ServerRequest): ServerResponse {
        val body = req.awaitBody<UserWrapper<UserAuthenticationRequest>>()
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                userService.login(body.content).toUserWrapper()
            )
    }

    suspend fun getCurrentUser(req: ServerRequest): ServerResponse {
        val (user, token) = userSessionProvider.getCurrentUserSessionOrFail()
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                user.toUserView(token).toUserWrapper()
            )
    }

    suspend fun updateUser(req: ServerRequest): ServerResponse {
        val body = req.awaitBody<UserWrapper<UpdateUserRequest>>()
        val userContext = userSessionProvider.getCurrentUserSessionOrFail()
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                userService.updateUser(body.content, userContext).toUserWrapper()
            )
    }


    suspend fun getProfile(req: ServerRequest): ServerResponse {
        val currentUser = userSessionProvider.getCurrentUserOrNull()
        val username = req.pathVariable("username")
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                userService.getProfile(username, currentUser).toProfileWrapper()
            )
    }

    suspend fun follow(req: ServerRequest): ServerResponse {
        val currentUser = userSessionProvider.getCurrentUserOrFail()
        val username = req.pathVariable("username")
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                userService.follow(username, currentUser).toProfileWrapper()
            )
    }

    suspend fun unfollow(req: ServerRequest): ServerResponse {
        val currentUser = userSessionProvider.getCurrentUserOrFail()
        val username = req.pathVariable("username")
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                userService.unfollow(username, currentUser).toProfileWrapper()
            )
    }
}