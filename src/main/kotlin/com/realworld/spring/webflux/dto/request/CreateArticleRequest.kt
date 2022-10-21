package com.realworld.spring.webflux.dto.request

import com.realworld.spring.webflux.dto.Article
import com.realworld.spring.webflux.persistence.entity.ArticleEntity
import javax.validation.constraints.NotBlank

data class CreateArticleRequest(
    @field:NotBlank
    val title: String,
    val description: String,
    @field:NotBlank
    val body: String,
    val tagList: List<String> = emptyList(),
) {

    fun toArticle(id: String, authorId: Long) = Article(
        articleEntity = ArticleEntity(
            id = id,
            authorId = authorId,
            description = description,
            title = title,
            slug = Article.toSlug(title),
            body = body,
            tags = ArrayList(tagList)
        )
    )
}