package com.realworld.spring.webflux.service.article

import com.realworld.spring.webflux.dto.*
import com.realworld.spring.webflux.dto.request.CreateArticleRequest
import com.realworld.spring.webflux.dto.request.CreateCommentRequest
import com.realworld.spring.webflux.dto.request.UpdateArticleRequest
import com.realworld.spring.webflux.dto.view.*
import com.realworld.spring.webflux.exceptions.InvalidRequestException
import com.realworld.spring.webflux.persistence.entity.*
import com.realworld.spring.webflux.persistence.repository.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Component
import java.util.*

@Component
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val tagRepository: TagRepository,
    private val userDataService: UserDataService,
) {

    suspend fun createArticle(request: CreateArticleRequest, author: User): ArticleView {

        val newSlug = Article.toSlug(request.title)
        if (articleRepository.existsBySlug(newSlug).awaitSingle()) {
            throw InvalidRequestException("Article", "already created")
        }
        val id = UUID.randomUUID().toString()
        val newArticle = request.toArticle(id, author.id!!)
        val savedArticleEntity = articleRepository.save(newArticle.articleEntity).awaitSingle()
        tagRepository.saveAllTags(savedArticleEntity.tags).awaitSingle()
        val authorProfileView = author.toOwnProfileView()
        return savedArticleEntity.toArticleView(authorProfileView, author)
    }

    suspend fun getArticle(slug: String, currentUser: User?): ArticleView {
        val article = articleRepository.findBySlugOrFail(slug).awaitSingle()
        val author = userDataService.findAuthorByArticle(article)
        return article.toArticleView(author, currentUser)
    }

    suspend fun updateArticle(request: UpdateArticleRequest, slug: String, author: User): ArticleView {
        val articleToUpdate = articleRepository.findBySlugOrFail(slug).awaitSingle()

        if (!articleToUpdate.isAuthor(author)) {
            throw InvalidRequestException("Article", "only author can update article")
        }

        val updatedArticalEntity = updateUser(request, articleToUpdate)

        val savedArticle = articleRepository.save(updatedArticalEntity).awaitSingle()
        return savedArticle.toAuthorArticleView(author)
    }

    suspend fun deleteArticle(slug: String, user: User) {
        val articleToDelete = articleRepository.findBySlugOrFail(slug).awaitSingle()

        if (!articleToDelete.isAuthor(user)) {
            throw InvalidRequestException("Article", "only author can delete own article")
        }

        articleRepository.deleteBySlug(slug).awaitSingle()
    }

    suspend fun findArticles(
        tag: String?,
        authorName: String?,
        favoritedByUser: String?,
        offset: Long,
        limit: Int,
        currentUser: User?,
    ): MultipleArticlesView {
        val favoritedBy = favoritedByUser?.let { userDataService.findByUsername(it) }
        val author = authorName?.let { userDataService.findByUsername(it) }
        return articleRepository.findNewestArticleFilteredBy(
            tag, author?.id,
            favoritedBy?.toUserEntity(), limit, offset
        ).asFlow()
            .map { mapToArticleView(it, currentUser) }
            .toList()
            .toMultipleArticlesView()
    }

    suspend fun feed(offset: Long, limit: Int, user: User): MultipleArticlesView {
        return articleRepository.findNewestArticlesByAuthorIds(user.followingIds, offset, limit).asFlow()
            .map { mapToArticleView(it, user) }
            .toList()
            .toMultipleArticlesView()
    }

    private fun updateUser(request: UpdateArticleRequest, articleToUpdate: ArticleEntity): ArticleEntity {
        if (request.body == null && request.description == null && request.title == null) {
            return articleToUpdate
        }
        return articleToUpdate.copy(
            body = request.body ?: articleToUpdate.body,
            description = request.description ?: articleToUpdate.description,
            title = request.title ?: articleToUpdate.title,
        )
    }

    suspend fun mapToArticleView(article: ArticleEntity, viewer: User?): ArticleView {
        val author = userDataService.findAuthorByArticle(article)
        return article.toArticleView(author, viewer)
    }

    suspend fun favoriteArticle(slug: String, currentUser: User): ArticleView {
        val article = articleRepository.findBySlugOrFail(slug).awaitSingle().let { Article(it) }
        currentUser.favorite(article)
        val savedUser = userDataService.save(currentUser)
        return mapToArticleView(article.articleEntity, savedUser)
    }

    suspend fun unfavoriteArticle(slug: String, currentUser: User): ArticleView {
        val articleEntity = articleRepository.findBySlugOrFail(slug).awaitSingle()
        currentUser.unfavorite(Article(articleEntity))
        val savedUser = userDataService.save(currentUser)
        return mapToArticleView(articleEntity, savedUser)
    }

    suspend fun getTags(): TagListView = tagRepository.findAll()
        .collectList()
        .awaitSingle()
        .toTagListView()

    suspend fun addComment(slug: String, content: CreateCommentRequest, currentUser: User): CommentView {
        val articleEntity = articleRepository.findBySlugOrFail(slug).awaitSingle()
        val comment = content.toComment(UUID.randomUUID().toString(), currentUser.id!!)
        val updatedArticleEntity = articleEntity.addComment(comment)
        articleRepository.save(updatedArticleEntity).awaitSingle()
        return comment.toCommentView(currentUser.toOwnProfileView())
    }

    suspend fun deleteComment(slug: String, commentId: String, currentUser: User) {
        val articleEntity = articleRepository.findBySlugOrFail(slug).awaitSingle()
        val comment = articleEntity.findCommentById(commentId) ?: return
        if (!comment.isAuthor(currentUser)) {
            throw InvalidRequestException("Comment", "only author can delete comment")
        }
        val updatedArticle = articleEntity.deleteComment(comment)
        articleRepository.save(updatedArticle).awaitSingle()
    }

    private suspend fun mapToCommentView(comment: Comment, viewer: User?): CommentView {
        val commentAuthor = userDataService.findById(comment.authorId)
        return comment.toCommentView(commentAuthor, viewer)
    }

    suspend fun getComments(slug: String, viewer: User?): MultipleCommentsView {
        val articleEntity = articleRepository.findBySlugOrFail(slug).awaitSingle()
        return articleEntity.comments.asFlow()
            .map { mapToCommentView(it, viewer) }
            .toList()
            .toMultipleCommentsView()
    }
}