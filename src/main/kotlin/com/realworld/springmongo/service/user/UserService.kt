package com.realworld.springmongo.service.user

import com.realworld.springmongo.dto.request.UpdateUserRequest
import com.realworld.springmongo.dto.request.UserAuthenticationRequest
import com.realworld.springmongo.dto.request.UserRegistrationRequest
import com.realworld.springmongo.dto.view.*
import com.realworld.springmongo.dto.User
import com.realworld.springmongo.persistence.repository.UserDataService
import com.realworld.springmongo.user.UserSession
import org.springframework.stereotype.Component

@Component
class UserService(
    private val userDataService: UserDataService,
    private val securedUserService: SecuredUserService,
) {

    suspend fun signup(request: UserRegistrationRequest): UserView {
        return securedUserService.signup(request)
    }

    suspend fun login(request: UserAuthenticationRequest): UserView {
        return securedUserService.login(request)
    }

    suspend fun updateUser(request: UpdateUserRequest, userSession: UserSession): UserView {
        val (user, token) = userSession
        val userToSave = securedUserService.prepareUserForUpdate(request, user)
        val savedUser = userDataService.save(userToSave)
        return savedUser.toUserView(token)
    }

    suspend fun getProfile(username: String, viewer: User?): ProfileView =
        userDataService.findByUsernameOrFail(username).toProfileView(viewer)

    suspend fun follow(username: String, futureFollower: User): ProfileView {
        val userToFollow = userDataService.findByUsernameOrFail(username)
        futureFollower.follow(userToFollow)
        userDataService.save(futureFollower)
        return userToFollow.toFollowedProfileView()
    }

    suspend fun unfollow(username: String, follower: User): ProfileView {
        val userToUnfollow = userDataService.findByUsernameOrFail(username)
        follower.unfollow(userToUnfollow)
        userDataService.save(follower)
        return userToUnfollow.toUnfollowedProfileView()
    }
}