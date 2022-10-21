package com.realworld.spring.webflux.persistence.repository

import com.realworld.spring.webflux.exceptions.InvalidRequestException
import com.realworld.spring.webflux.persistence.OffsetBasedPageable
import com.realworld.spring.webflux.persistence.entity.ArticleEntity
import com.realworld.spring.webflux.persistence.sortBy
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

interface ArticleRepository : ReactiveMongoRepository<ArticleEntity, String>, ArticleManualRepository {
    companion object {
        val NEWEST_ARTICLE_SORT = sortBy(ArticleEntity::createdAt).descending()
    }

    fun findBySlug(slug: String): Mono<ArticleEntity>

    fun deleteBySlug(slug: String): Mono<ArticleEntity>

    fun findByAuthorIdIn(authorIds: List<Long>, pageable: Pageable): Flux<ArticleEntity>

    fun existsBySlug(slug: String): Mono<Boolean>
}

fun ArticleRepository.findBySlugOrFail(slug: String): Mono<ArticleEntity> = findBySlug(slug)
    .switchIfEmpty(InvalidRequestException("Article", "not found").toMono())

fun ArticleRepository.findBySlugOrNull(slug: String): Mono<ArticleEntity> = findBySlug(slug)
    .switchIfEmpty(InvalidRequestException("Article", "not found").toMono())


fun ArticleRepository.findNewestArticlesByAuthorIds(authorIds: List<Long>, offset: Long, limit: Int) =
    findByAuthorIdIn(
        authorIds,
        OffsetBasedPageable(limit = limit, offset = offset, sort = ArticleRepository.NEWEST_ARTICLE_SORT)
    )