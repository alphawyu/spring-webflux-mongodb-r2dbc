package com.realworld.spring.webflux.dto.view

import com.realworld.spring.webflux.dto.User
import com.realworld.spring.webflux.persistence.entity.Comment
import com.realworld.spring.webflux.persistence.entity.UserEntity
import java.time.Instant

data class CommentView(
    val id: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val body: String,
    val author: ProfileView,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommentView

        if (id != other.id) return false
        if (body != other.body) return false
        if (author != other.author) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + body.hashCode()
        result = 31 * result + author.hashCode()
        return result
    }
}

fun Comment.toCommentView(author: User, viewer: User? = null) = CommentView(
    id = this.id,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    body = this.body,
    author = author.toProfileView(viewer)
)

fun Comment.toCommentView(author: ProfileView) = CommentView(
    id = this.id,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    body = this.body,
    author = author
)