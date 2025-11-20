package com.realworld.spring.webflux.api

import com.realworld.spring.webflux.dto.request.CreateArticleRequest
import com.realworld.spring.webflux.dto.request.CreateCommentRequest
import com.realworld.spring.webflux.dto.request.UpdateArticleRequest
import com.realworld.spring.webflux.dto.request.UserAuthenticationRequest
import com.realworld.spring.webflux.dto.view.*
import com.realworld.spring.webflux.service.article.ArticleService
import com.realworld.spring.webflux.user.UserSessionProvider
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import kotlin.jvm.optionals.getOrDefault

@Component
class ArticleHandler(
    private val articleService: ArticleService,
    private val userProvider: UserSessionProvider
) {

    suspend fun createArticle(req: ServerRequest): ServerResponse {
        val body = req.awaitBody<ArticleWrapper<CreateArticleRequest>>()
        val currentUser = userProvider.getCurrentUserOrFail()
        return ServerResponse
            .status(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                articleService.createArticle(body.content, currentUser).toArticleWrapper()
            )
    }

    suspend fun getArticles(req: ServerRequest): ServerResponse {
        val offset = req.queryParam("offset").getOrDefault("0").toLongOrNull()
        val limit = req.queryParam("limit").getOrDefault("20").toIntOrNull()
        val tag = req.queryParam("tag").orElse(null)
        val favoritedByUser = req.queryParam("favorited").orElse(null)
        val author = req.queryParam("author").orElse(null)
        val currentUser = userProvider.getCurrentUserOrNull()
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                articleService.findArticles(tag, author, favoritedByUser, offset?:0, limit?:20, currentUser)
            )
    }

    suspend fun feed(req: ServerRequest): ServerResponse {
        val offset = req.queryParam("offset").getOrDefault("0").toLongOrNull()
        val limit = req.queryParam("limit").getOrDefault("20").toIntOrNull()
        val currentUser = userProvider.getCurrentUserOrFail()
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                articleService.feed(offset?:0, limit?:20, currentUser)
            )
    }

    suspend fun getArticle(req: ServerRequest): ServerResponse {
        val slug = req.pathVariable("slug")
        val currentUser = userProvider.getCurrentUserOrFail()
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                articleService.getArticle(slug, currentUser).toArticleWrapper()
            )
    }

    suspend fun updateArticle(req: ServerRequest): ServerResponse {
        val body = req.awaitBody<ArticleWrapper<UpdateArticleRequest>>()
        val slug = req.pathVariable("slug")
        val currentUser = userProvider.getCurrentUserOrFail()
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                articleService.updateArticle(body.content, slug, currentUser).toArticleWrapper()
            )
    }

    suspend fun deleteArticle(req: ServerRequest): ServerResponse {
        val slug = req.pathVariable("slug")
        val currentUser = userProvider.getCurrentUserOrFail()
        articleService.deleteArticle(slug, currentUser)
        return ServerResponse.noContent().buildAndAwait()
    }

    suspend fun favoriteArticle(req: ServerRequest): ServerResponse {
        val slug = req.pathVariable("slug")
        val currentUser = userProvider.getCurrentUserOrFail()
        return ServerResponse
            .status(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                articleService.favoriteArticle(slug, currentUser).toArticleWrapper()
            )
    }

    suspend fun unfavoriteArticle(req: ServerRequest): ServerResponse {
        val slug = req.pathVariable("slug")
        val currentUser = userProvider.getCurrentUserOrFail()
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                articleService.unfavoriteArticle(slug, currentUser).toArticleWrapper()
            )
    }

    suspend fun getComments(req: ServerRequest): ServerResponse {
        val slug = req.pathVariable("slug")
        val currentUser = userProvider.getCurrentUserOrFail()
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                articleService.getComments(slug, currentUser)
            )
    }

    suspend fun addComment(req: ServerRequest): ServerResponse {
        val body = req.awaitBody<CommentWrapper<CreateCommentRequest>>()
        val slug = req.pathVariable("slug")
        val currentUser = userProvider.getCurrentUserOrFail()
        return ServerResponse
            .status(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                articleService.addComment(slug, body.content, currentUser).toCommentWrapper()
            )
    }

    suspend fun deleteComment(req: ServerRequest): ServerResponse {
        val slug = req.pathVariable("slug")
        val commentId = req.pathVariable("commentId")
        val currentUser = userProvider.getCurrentUserOrFail()
        articleService.deleteComment(slug, commentId, currentUser)
        return ServerResponse.noContent().buildAndAwait()
    }

    suspend fun getTags(req: ServerRequest): ServerResponse {
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                articleService.getTags()
            )
    }
}