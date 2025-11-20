package com.realworld.spring.webflux.service

import com.realworld.spring.webflux.api.toUserWrapper
import com.realworld.spring.webflux.dto.User
import com.realworld.spring.webflux.dto.request.UpdateUserRequest
import com.realworld.spring.webflux.exceptions.InvalidRequestException
import com.realworld.spring.webflux.persistence.entity.UserEntity
import com.realworld.spring.webflux.persistence.repository.UserDataService
import com.realworld.spring.webflux.service.user.PasswordService
import com.realworld.spring.webflux.service.user.SecuredUserService
import com.realworld.spring.webflux.service.user.UserTokenProvider
import com.realworld.spring.webflux.user.UserSession
import helpers.UserSamples
import helpers.coCatchThrowable
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import reactor.kotlin.core.publisher.toMono
import kotlin.String
import kotlin.test.assertEquals

internal class SecuredUserServiceTest {
    companion object {
        val mockPswdSrvc = mockk<PasswordService>()
        val mockUsrTknPdr = mockk<UserTokenProvider>()
        val mockUsrDSrvc = mockk<UserDataService>()
        val securedUserService = SecuredUserService(mockUsrDSrvc, mockPswdSrvc, mockUsrTknPdr)
    }

    @Nested
    @DisplayName("Test SeciredUserService.prepareUserForUpdate ")
    inner class SecuredUserService_prepareUserForUpdate {
        @DisplayName("validate current user value fills that not provided by update request")
        @ParameterizedTest
        @CsvSource(
            "nBio,nImg,nPswd,nEPswd,nEmail,nUName,nBio,nImg,nEPswd,nEmail,nUName",
            ",,,nEPswd, , ,obio,oimg,oePswd,oem,oun",
            ",,,nEPswd,' ',' ',obio,oimg,oePswd,oem,oun",
            ",,,nEPswd,,,obio,oimg,oePswd,oem,oun",
            ",,,nEPswd,oem,oun,obio,oimg,oePswd,oem,oun",
        )
        fun test_noExceptin(iBio: String?, iImage: String?, iPswd: String?, iEPswd: String, iEmail: String?, iUName: String?,
                            oBio: String, oImage: String, oEPswd: String, oEmail: String, oUName: String) = runTest {
            every { mockPswdSrvc.encodePassword(any()) } returns iEPswd
            every { mockUsrDSrvc.existsByEmail(any()) } returns false.toMono()
            every { mockUsrDSrvc.existsByUsername(any()) } returns false.toMono()

            val req = UpdateUserRequest(
                bio = iBio,
                image = iImage,
                password = iPswd,
                email = iEmail,
                username = iUName
            )
            val user = User(
                id = 12345L,
                username = "oun",
                encodedPassword = "oePswd",
                email = "oem",
                bio = "obio",
                image = "oimg",
                followingIds = listOf(100L, 200L),
                favoriteArticlesIds = listOf("ofaid1", "ofaid2")
            )

            val eUsr = securedUserService.prepareUserForUpdate(req, user)

            assertEquals(eUsr.bio, oBio)
            assertEquals(eUsr.image, oImage)
            assertEquals(eUsr.encodedPassword, oEPswd)
            assertEquals(eUsr.email, oEmail)
            assertEquals(eUsr.username, oUName)
        }
    }
}