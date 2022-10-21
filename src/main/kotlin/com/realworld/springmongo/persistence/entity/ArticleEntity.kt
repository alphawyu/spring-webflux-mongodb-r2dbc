package com.realworld.springmongo.persistence.entity

import com.realworld.springmongo.persistence.entity.Comment
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.Instant

@Document
data class ArticleEntity(
    @Id val id: String,
    val title: String,
    val slug: String,
    val favoritesCount: Int = 0,
    val createdAt: Instant = Instant.now(),
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
    val description: String,
    val body: String,
    val authorId: Long,
    @Field("comments") val comments: List<Comment> = listOf(),
    @Field("tags") val tags: List<String> = listOf(),
)