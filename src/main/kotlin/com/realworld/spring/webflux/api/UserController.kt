package com.realworld.spring.webflux.api

import com.realworld.spring.webflux.dto.request.UpdateUserRequest
import com.realworld.spring.webflux.dto.request.UserAuthenticationRequest
import com.realworld.spring.webflux.dto.request.UserRegistrationRequest
import com.realworld.spring.webflux.dto.view.UserView
import com.realworld.spring.webflux.service.user.UserService
import com.realworld.spring.webflux.user.UserSessionProvider
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class UserController(private val userService: UserService, private val userSessionProvider: UserSessionProvider) {

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun signup(@RequestBody @Valid request: UserWrapper<UserRegistrationRequest>): UserWrapper<UserView> {
        return userService.signup(request.content).toUserWrapper()
    }

    @PostMapping("/users/login")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun login(@RequestBody @Valid request: UserWrapper<UserAuthenticationRequest>): UserWrapper<UserView> {
        return userService.login(request.content).toUserWrapper()
    }

    @GetMapping("/user")
    suspend fun getCurrentUser(): UserWrapper<UserView> {
        val (user, token) = userSessionProvider.getCurrentUserSessionOrFail()
        return user.toUserView(token).toUserWrapper()
    }

    @PutMapping("/user")
    suspend fun updateUser(@RequestBody @Valid request: UserWrapper<UpdateUserRequest>): UserWrapper<UserView> {
        val userContext = userSessionProvider.getCurrentUserSessionOrFail()
        return userService.updateUser(request.content, userContext).toUserWrapper()
    }

    @GetMapping("/profiles/{username}")
    suspend fun getProfile(@PathVariable username: String): ProfileWrapper {
        val currentUser = userSessionProvider.getCurrentUserOrNull()
        return userService.getProfile(username, currentUser).toProfileWrapper()
    }

    @PostMapping("/profiles/{username}/follow")
    suspend fun follow(@PathVariable username: String): ProfileWrapper {
        val currentUser = userSessionProvider.getCurrentUserOrFail()
        return userService.follow(username, currentUser).toProfileWrapper()
    }

    @DeleteMapping("/profiles/{username}/follow")
    suspend fun unfollow(@PathVariable username: String): ProfileWrapper {
        val currentUser = userSessionProvider.getCurrentUserOrFail()
        return userService.unfollow(username, currentUser).toProfileWrapper()
    }
}