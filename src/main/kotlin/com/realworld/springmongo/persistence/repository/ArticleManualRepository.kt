package com.realworld.springmongo.persistence.repository

import com.realworld.springmongo.persistence.entity.ArticleEntity
import com.realworld.springmongo.persistence.whereProperty
import com.realworld.springmongo.persistence.entity.UserEntity
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Query
import reactor.core.publisher.Flux

interface ArticleManualRepository {
    fun findNewestArticleFilteredBy(
        tag: String?,
        authorId: Long?,
        favoritedBy: UserEntity?,
        limit: Int,
        offset: Long
    ): Flux<ArticleEntity>
}

class ArticleManualRepositoryImpl(private val mongoTemplate: ReactiveMongoTemplate) : ArticleManualRepository {

    override fun findNewestArticleFilteredBy(
        tag: String?,
        authorId: Long?,
        favoritedBy: UserEntity?,
        limit: Int,
        offset: Long,
    ): Flux<ArticleEntity> {
        val query = Query()
            .skip(offset)
            .limit(limit)
            .with(ArticleRepository.NEWEST_ARTICLE_SORT)

        tag?.let { query.addCriteria(tagsContains(it)) }
        authorId?.let { query.addCriteria(authorIdEquals(it)) }
        favoritedBy?.let { query.addCriteria(isFavoriteArticleByUser(it)) }

        return mongoTemplate.find(query)
    }

    private fun tagsContains(it: String) = whereProperty(ArticleEntity::tags).all(it)

    private fun authorIdEquals(it: Long) = whereProperty(ArticleEntity::authorId).`is`(it)

    private fun isFavoriteArticleByUser(it: UserEntity) =
        whereProperty(ArticleEntity::id).`in`(it.getFavoriteArticlesIds())
}
