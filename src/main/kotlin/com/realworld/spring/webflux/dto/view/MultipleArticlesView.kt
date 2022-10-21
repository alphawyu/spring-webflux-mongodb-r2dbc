package com.realworld.spring.webflux.dto.view

data class MultipleArticlesView(
    val articles: List<ArticleView> = emptyList(),
    val articlesCount: Int = 0,
)

fun List<ArticleView>.toMultipleArticlesView() = MultipleArticlesView(this, this.size)