package com.realworld.springmongo.dto.request

import com.realworld.springmongo.persistence.entity.Comment
import javax.validation.constraints.NotBlank

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
