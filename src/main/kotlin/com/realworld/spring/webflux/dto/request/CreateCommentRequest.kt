package com.realworld.spring.webflux.dto.request

import com.realworld.spring.webflux.persistence.entity.Comment
import jakarta.validation.constraints.NotBlank

data class CreateCommentRequest(
    @field:NotBlank
    val body: String,
) {
    fun toComment(commentId: String, authorId: Long) = Comment(
        id = commentId,
        authorId = authorId,
        body = body
    )
}
