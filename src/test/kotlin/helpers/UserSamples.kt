package helpers

import com.realworld.spring.webflux.dto.User
import com.realworld.spring.webflux.dto.request.UpdateUserRequest
import com.realworld.spring.webflux.dto.request.UserAuthenticationRequest
import com.realworld.spring.webflux.dto.request.UserRegistrationRequest
import com.realworld.spring.webflux.service.user.PasswordService
import java.util.*


object UserSamples {
    const val SAMPLE_USERNAME = "Test username"
    const val SAMPLE_EMAIL = "testemail@gmail.com"
    const val SAMPLE_PASSWORD = "testpassword"
    val SAMPLE_USER_ID = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE
    private val passwordService = PasswordService()

    fun sampleUserRegistrationRequest() = UserRegistrationRequest(
        username = SAMPLE_USERNAME,
        email = SAMPLE_EMAIL,
        password = SAMPLE_PASSWORD,
    )

    fun sampleUserAuthenticationRequest() = UserAuthenticationRequest(
        email = SAMPLE_EMAIL,
        password = SAMPLE_PASSWORD,
    )

    fun sampleUser(passwordService: PasswordService) = User(
        id = SAMPLE_USER_ID,
        username = SAMPLE_USERNAME,
        email = SAMPLE_EMAIL,
        encodedPassword = passwordService.encodePassword(SAMPLE_PASSWORD),
        image = "test image url",
        bio = "test bio"
    )

    fun sampleUser() = sampleUser(passwordService)

    fun sampleUpdateUserRequest() = UpdateUserRequest(
        bio = "new bio",
        email = "newemail@gmail.com",
        image = "new image",
        username = "new username",
        password = "new password",
    )
}