package helpers

import com.realworld.spring.webflux.dto.Article
import com.realworld.spring.webflux.dto.request.CreateArticleRequest
import com.realworld.spring.webflux.persistence.entity.ArticleEntity
import java.time.Instant
import java.util.*

object ArticleSamples {

    const val DEFAULT_ARTICLE_BODY = "test article body"
    const val DEFAULT_ARTICLE_DESCRIPTION = "test article description"
    const val DEFAULT_ARTICLE_TITLE = "test article title"
    val DEFAULT_TAG_LIST = listOf("test_tag_1", "test_tag_2", "test_tag_3")

    fun sampleCreateArticleRequest(suffix: String = "") = CreateArticleRequest(
        body = DEFAULT_ARTICLE_BODY,
        description = DEFAULT_ARTICLE_DESCRIPTION,
        title = DEFAULT_ARTICLE_TITLE + suffix,
        tagList = DEFAULT_TAG_LIST,
    )

    fun sampleArticle(
        id: String = UUID.randomUUID().toString(),
        createdAt: Instant,
        updatedAt: Instant,
    ) = Article( articleEntity = ArticleEntity(
        id = id,
        body = DEFAULT_ARTICLE_BODY,
        description = DEFAULT_ARTICLE_DESCRIPTION,
        title = DEFAULT_ARTICLE_TITLE,
        slug = Article.toSlug(DEFAULT_ARTICLE_TITLE),
        createdAt = createdAt,
        updatedAt = updatedAt,
        authorId = UserSamples.SAMPLE_USER_ID
    ))
}