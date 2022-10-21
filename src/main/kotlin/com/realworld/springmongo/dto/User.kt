package com.realworld.springmongo.dto

import com.realworld.springmongo.dto.view.ProfileView
import com.realworld.springmongo.dto.view.UserView
import com.realworld.springmongo.persistence.entity.UserEntity
import org.springframework.data.mongodb.core.mapping.Document

@Document
class User(
    val id: Long? = null,
    val username: String,
    val encodedPassword: String,
    val email: String,
    val bio: String? = null,
    val image: String? = null,
    var followingIds: List<Long> = listOf(),
    var favoriteArticlesIds: List<String> = listOf(),
) {
    fun toUserEntity() = UserEntity(
        id = this.id,
        username = this.username,
        encodedPassword = this.encodedPassword,
        email = this.email,
        bio = this.bio,
        image = this.image,
        followingIdsStr = this.followingIds.joinToString(","),
        favoriteArticlesIdsStr = this.favoriteArticlesIds.joinToString(",")
    )


    fun follow(followerId: Long?) {
        if (followerId != null) {
            followingIds = followingIds.plus(followerId)
        }
    }

    fun follow(followee: User) = this.follow(followee.id)

    fun unfollow(followerId: Long?) {
        if (followerId != null) {
            followingIds = followingIds.minus(followerId)
        }
    }

    fun unfollow(user: User) = this.unfollow(user.id)

    fun favorite(article: Article) {
        article.incrementFavoritesCount()
        favoriteArticlesIds = favoriteArticlesIds.plus(article.articleEntity.id)
    }

    fun unfavorite(article: Article) {
        article.decrementFavoritesCount()
        favoriteArticlesIds = favoriteArticlesIds.minus(article.articleEntity.id)
    }

    fun isFollowing(followee: User) = followingIds.contains(followee.id)

    fun isFollower(user: User) = user.isFollowing(this)

    fun isFavoriteArticle(article: Article):
            Boolean = favoriteArticlesIds.contains(article.articleEntity.id)

    fun toProfileView(viewer: User? = null) = when (viewer) {
        null -> toUnfollowedProfileView()
        else -> toProfileViewForViewer(viewer)
    }

    fun toUnfollowedProfileView() = this.toProfileView(following = false)

    fun toFollowedProfileView() = this.toProfileView(following = true)

    fun toOwnProfileView() = this.toProfileViewForViewer(this)

    private fun toProfileView(following: Boolean) = ProfileView(
        username = this.username,
        bio = this.bio,
        image = this.image,
        following = following,
    )

    private fun toProfileViewForViewer(viewer: User) =
        this.toProfileView(following = viewer.isFollowing(followee = this))

    fun toUserView(token: String) = UserView(
        email = this.email,
        token = token,
        username = this.username,
        bio = this.bio,
        image = this.image,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "User(id='$id', username='$username', encodedPassword='$encodedPassword', email='$email', bio='$bio', image='$image', followingIds=$followingIds, favoriteArticlesIds=$favoriteArticlesIds)"
    }


}