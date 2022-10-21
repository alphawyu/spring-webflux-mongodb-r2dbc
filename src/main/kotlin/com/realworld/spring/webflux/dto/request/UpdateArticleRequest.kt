package com.realworld.spring.webflux.dto.request

import com.realworld.spring.webflux.validation.NotBlankOrNull

data class UpdateArticleRequest(
    @field:NotBlankOrNull
    var title: String?,
    @field:NotBlankOrNull
    var description: String?,
    @field:NotBlankOrNull
    var body: String?,
)
