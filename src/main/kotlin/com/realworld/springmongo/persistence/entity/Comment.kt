package com.realworld.springmongo.persistence.entity

import com.realworld.springmongo.dto.User
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant

class Comment(
    val id: String,
    var body: String,
    var authorId: Long,
    val createdAt: Instant = Instant.now(),
    updatedAt: Instant = Instant.now(),
) {

    @LastModifiedDate
    var updatedAt: Instant = updatedAt
        private set

    fun isAuthor(user: User) = authorId == user.id

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Comment

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Comment(id='$id', body='$body', authorId='$authorId', createdAt=$createdAt, updatedAt=$updatedAt)"
    }
}
