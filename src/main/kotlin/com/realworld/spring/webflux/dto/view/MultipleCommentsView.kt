package com.realworld.spring.webflux.dto.view

data class MultipleCommentsView(val comments: List<CommentView> = emptyList())

fun List<CommentView>.toMultipleCommentsView() = MultipleCommentsView(this)