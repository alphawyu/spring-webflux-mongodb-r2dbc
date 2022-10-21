package com.realworld.spring.webflux.persistence.entity

import com.realworld.spring.webflux.dto.User
import org.apache.commons.lang3.StringUtils
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("app_user")
data class UserEntity(
    @Id val id: Long?,
    val username: String,
    val encodedPassword: String,
    val email: String,
    val bio: String? = null,
    val image: String? = null,
    val followingIdsStr: String? = null,
    val favoriteArticlesIdsStr: String? = null,
) {
    fun toUser() = User(
        id = this.id,
        username = this.username,
        encodedPassword = this.encodedPassword,
        email = this.email,
        bio = this.bio,
        image = this.image,
        followingIds = stringToLongList(this.followingIdsStr),
        favoriteArticlesIds = this.favoriteArticlesIdsStr?.split(",") ?: listOf()
    )

    fun getFavoriteArticlesIds() =
        this.favoriteArticlesIdsStr?.split(",") ?: listOf()

    fun stringToLongList(inStr: String?): List<Long> {
        return if (inStr == null || inStr.isEmpty())
            return listOf()
        else
            inStr.split(",").map { it.toLong() }
    }
}
