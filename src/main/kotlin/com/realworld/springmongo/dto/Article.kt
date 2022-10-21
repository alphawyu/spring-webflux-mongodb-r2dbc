package com.realworld.springmongo.dto

import com.realworld.springmongo.persistence.entity.ArticleEntity
import com.realworld.springmongo.persistence.entity.Comment
import com.realworld.springmongo.persistence.entity.UserEntity
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.Instant

@Document
class Article(
    var articleEntity: ArticleEntity,
) {
    companion object {
        fun toSlug(title: String) = title
            .lowercase()
            .replace("[&|\\uFE30-\\uFFA0’”\\s?,.]+".toRegex(), "-")
    }

    @LastModifiedDate
    var updatedAt: Instant = articleEntity.updatedAt
        private set(value) {
            articleEntity = articleEntity.copy(
                updatedAt = value
            )
        }

    var favoritesCount: Int = articleEntity.favoritesCount
        private set(value) {
            articleEntity = articleEntity.copy(
                favoritesCount = value
            )
        }

    val comments: List<Comment> get() = articleEntity.comments

    val tags: List<String> get() = articleEntity.tags

    var title: String = articleEntity.title
        set(value) {
            articleEntity = articleEntity.copy(
                title = value,
                slug = toSlug(value),
            )
        }

    fun incrementFavoritesCount() {
        articleEntity = articleEntity.copy(
            favoritesCount = articleEntity.favoritesCount + 1
        )
    }

    fun decrementFavoritesCount() {
        articleEntity = articleEntity.copy(
            favoritesCount = articleEntity.favoritesCount - 1
        )
    }

    fun addComment(comment: Comment) {
        articleEntity = articleEntity.addComment(comment)
    }

    fun deleteComment(comment: Comment) {
        articleEntity = articleEntity.deleteComment(comment)
    }

    fun hasTag(tag: String):
            Boolean = articleEntity.tags.contains(tag)


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Article
        return this.articleEntity.id === other.articleEntity.id
    }

    override fun hashCode(): Int {
        return articleEntity.id.hashCode()
    }

    override fun toString(): String {
        return articleEntity.toString()
    }
}

fun ArticleEntity.isAuthor(authorId: Long?): Boolean = this.authorId == authorId

fun ArticleEntity.isAuthor(user: User): Boolean = isAuthor(user.id)

fun ArticleEntity.addComment(comment: Comment): ArticleEntity {
    return this.copy(
        comments = this.comments.plus(comment)
    )
}

fun ArticleEntity.deleteComment(comment: Comment): ArticleEntity {
    return this.copy(
        comments = this.comments.minus(comment)
    )
}

fun ArticleEntity.findCommentById(commentId: String):
        Comment? = this.comments.firstOrNull { it.id == commentId }




