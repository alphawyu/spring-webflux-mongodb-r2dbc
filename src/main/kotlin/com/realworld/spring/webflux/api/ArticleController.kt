package com.realworld.spring.webflux.api

import com.realworld.spring.webflux.dto.request.CreateArticleRequest
import com.realworld.spring.webflux.dto.request.CreateCommentRequest
import com.realworld.spring.webflux.dto.request.UpdateArticleRequest
import com.realworld.spring.webflux.dto.view.*
import com.realworld.spring.webflux.service.article.ArticleService
import com.realworld.spring.webflux.user.UserSessionProvider
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ArticleController(private val articleService: ArticleService, private val userProvider: UserSessionProvider) {

    @PostMapping("/articles")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createArticle(@RequestBody request: ArticleWrapper<CreateArticleRequest>): ArticleWrapper<ArticleView> {
        val currentUser = userProvider.getCurrentUserOrFail()
        return articleService.createArticle(request.content, currentUser).toArticleWrapper()
    }

    @GetMapping("/articles")
    suspend fun getArticles(
        @RequestParam(value = "offset", defaultValue = "0") offset: Long = 0,
        @RequestParam(value = "limit", defaultValue = "20") limit: Int = 20,
        @RequestParam(value = "tag", required = false) tag: String? = null,
        @RequestParam(value = "favorited", required = false) favoritedByUser: String? = null,
        @RequestParam(value = "author", required = false) author: String? = null,
    ): MultipleArticlesView {
        val currentUser = userProvider.getCurrentUserOrNull()
        return articleService.findArticles(tag, author, favoritedByUser, offset, limit, currentUser)
    }

    @GetMapping("/articles/feed")
    suspend fun feed(
        @RequestParam(value = "offset", defaultValue = "0") offset: Long = 0,
        @RequestParam(value = "limit", defaultValue = "20") limit: Int = 20,
    ): MultipleArticlesView {
        val currentUser = userProvider.getCurrentUserOrFail()
        return articleService.feed(offset, limit, currentUser)
    }

    @GetMapping("/articles/{slug}")
    suspend fun getArticle(@PathVariable slug: String): ArticleWrapper<ArticleView> {
        val currentUser = userProvider.getCurrentUserOrNull()
        return articleService.getArticle(slug, currentUser).toArticleWrapper()
    }

    @PutMapping("/articles/{slug}")
    suspend fun updateArticle(
        @RequestBody request: ArticleWrapper<UpdateArticleRequest>,
        @PathVariable slug: String,
    ): ArticleWrapper<ArticleView> {
        val currentUser = userProvider.getCurrentUserOrFail()
        return articleService.updateArticle(request.content, slug, currentUser).toArticleWrapper()
    }

    @DeleteMapping("/articles/{slug}")
    suspend fun deleteArticle(@PathVariable slug: String) {
        val currentUser = userProvider.getCurrentUserOrFail()
        articleService.deleteArticle(slug, currentUser)
    }

    @PostMapping("/articles/{slug}/favorite")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun favoriteArticle(@PathVariable slug: String): ArticleWrapper<ArticleView> {
        val currentUser = userProvider.getCurrentUserOrFail()
        return articleService.favoriteArticle(slug, currentUser).toArticleWrapper()
    }

    @DeleteMapping("/articles/{slug}/favorite")
    suspend fun unfavoriteArticle(@PathVariable slug: String): ArticleWrapper<ArticleView> {
        val currentUser = userProvider.getCurrentUserOrFail()
        return articleService.unfavoriteArticle(slug, currentUser).toArticleWrapper()
    }

    @GetMapping("/articles/{slug}/comments")
    suspend fun getComments(@PathVariable slug: String): MultipleCommentsView {
        val currentUser = userProvider.getCurrentUserOrNull()
        return articleService.getComments(slug, currentUser)
    }

    @PostMapping("/articles/{slug}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun addComment(
        @PathVariable slug: String,
        @RequestBody request: CommentWrapper<CreateCommentRequest>,
    ): CommentWrapper<CommentView> {
        val currentUser = userProvider.getCurrentUserOrFail()
        return articleService.addComment(slug, request.content, currentUser).toCommentWrapper()
    }

    @DeleteMapping("/articles/{slug}/comments/{commentId}")
    suspend fun deleteComment(@PathVariable slug: String, @PathVariable commentId: String) {
        val currentUser = userProvider.getCurrentUserOrFail()
        articleService.deleteComment(slug, commentId, currentUser)
    }

    @GetMapping("/tags")
    suspend fun getTags(): TagListView = articleService.getTags()
}