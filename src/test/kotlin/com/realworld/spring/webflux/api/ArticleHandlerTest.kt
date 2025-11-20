package com.realworld.spring.webflux.api

import com.ninjasquad.springmockk.MockkBean
import com.realworld.spring.webflux.dto.User
import com.realworld.spring.webflux.dto.request.CreateArticleRequest
import com.realworld.spring.webflux.dto.request.CreateCommentRequest
import com.realworld.spring.webflux.dto.request.UpdateArticleRequest
import com.realworld.spring.webflux.dto.view.*
import com.realworld.spring.webflux.persistence.entity.Tag
import com.realworld.spring.webflux.security.SecurityTest.TestController
import com.realworld.spring.webflux.service.article.ArticleService
import com.realworld.spring.webflux.service.user.UserService
import com.realworld.spring.webflux.user.UserSessionProvider
import helpers.ImportAppSecurity
import helpers.SupportTestConfig
import io.mockk.coEvery
import io.mockk.coJustRun
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.security.config.Customizer
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.json.JsonCompareMode
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Instant


@WebFluxTest
@ContextConfiguration(classes = [SupportTestConfig::class])
@ImportAppSecurity
@Import(
    RouterConfig::class,
    UserHandler::class,
    ArticleHandler::class,
    ArticleHandlerTest.Configuration::class
)
class ArticleHandlerTest(@Autowired val webTestClient: WebTestClient) {
    @MockkBean
    private lateinit var mockUserService: UserService

    @MockkBean
    private lateinit var mockArticleService: ArticleService

    @MockkBean
    private lateinit var mockUserSessionProvider: UserSessionProvider
    val refTsStr = "2021-05-03T10:30:00.000Z"
    val expProfileView = ProfileView(
        username = "un",
        following = false,
    )
    val expArticleView = ArticleView(
        slug = "s",
        title = "t",
        description = "d",
        body = "b",
        tagList = listOf("tg1", "tg2"),
        createdAt = Instant.parse(refTsStr),
        updatedAt = Instant.parse(refTsStr),
        favorited = false,
        favoritesCount = 0,
        author = expProfileView
    )
    val expArticleViewStr = """
                {"article":
                    {"slug":"s",
                     "title":"t",
                     "description":"d",
                     "body":"b",
                     "tagList":["tg1","tg2"],
                     "createdAt":"2021-05-03T10:30:00Z",
                     "updatedAt":"2021-05-03T10:30:00Z",
                     "favorited":false,
                     "favoritesCount":0,
                     "author":
                        {"username":"un",
                         "bio":null,
                         "image":null,
                         "following":false
                        }
                    }
                }
            """
    val expMArticleView = listOf(expArticleView).toMultipleArticlesView()
    val expMArticleViewStr = """
        {"articles":
            [{
                "slug":"s",
                "title":"t",
                "description":"d",
                "body":"b",
                "tagList":["tg1","tg2"],
                "createdAt":"2021-05-03T10:30:00Z",
                "updatedAt":"2021-05-03T10:30:00Z",
                "favorited":false,
                "favoritesCount":0,
                "author":
                    {
                        "username":"un",
                        "bio":null,
                        "image":null,
                        "following":false
                    }
            }],
            "articlesCount":1
        }
    """
    val expUser = User(
        email = "t@t.com",
        username = "testUserName",
        encodedPassword = "",
    )

    @Nested
    inner class ArticleUnitTests {
        @BeforeEach
        fun setup() {
            coEvery { mockUserSessionProvider.getCurrentUserOrNull() } returns expUser
            coEvery { mockUserSessionProvider.getCurrentUserOrFail() } returns expUser
        }

        @Test
        @DisplayName("test post /articles .. {CreateArticleRequest} - happy path")
        fun test_createArticle() {
            val regReq = ArticleWrapper(
                content = CreateArticleRequest(
                    title = "t",
                    description = "d",
                    body = "b",
                    tagList = listOf("tg1", "tg2"),
                )
            )
            coEvery { mockArticleService.createArticle(any(), any()) } returns expArticleView
            webTestClient.post().uri("/api/articles")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(regReq)
                .exchange()
                .expectStatus().isCreated
                .expectBody()
                .json(
                    expArticleViewStr, JsonCompareMode.STRICT
                )
        }

        @DisplayName("test get /articles?offset=&limit=&tag=&favorited=&author= - happy path")
        @ParameterizedTest
        @CsvSource(
            "?tag=tg1",
            "?favorited=f2",
            "?author=testUser",
            "?tag=tg1&favorited=12&author=testUser",
            "?offset=1&limit=12&tag=tg1",
            "?limit=12&tag=tg1&favorited=f2",
        )
        fun test_getArticles(iQryStr: String) {
            coEvery {
                mockArticleService.findArticles(any(), any(), any(), any(), any(), any())
            } returns expMArticleView
            webTestClient.get().uri("/api/articles$iQryStr")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .json(
                    expMArticleViewStr, JsonCompareMode.STRICT
                )
        }

        @DisplayName("test get /articles/feed?offset=&limit= - happy path")
        @ParameterizedTest
        @CsvSource(
            "?offset=1&limit=12",
            "?offset=1",
            "?limit=12",
        )
        fun test_feed(iQryStr: String) {
            coEvery {
                mockArticleService.feed(any(), any(), any())
            } returns expMArticleView
            webTestClient.get().uri("/api/articles/feed$iQryStr")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .json(
                    expMArticleViewStr, JsonCompareMode.STRICT
                )
        }

        @Test
        @DisplayName("test get /articles/{slug} - happy path")
        fun test_getArticle() {
            coEvery { mockArticleService.getArticle(any(), any()) } returns expArticleView
            webTestClient.get().uri("/api/articles/s1")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .json(
                    expArticleViewStr, JsonCompareMode.STRICT
                )
        }

        @Test
        @DisplayName("test put /articles/{slug} .. {UpdateArticleRequest} - happy path")
        fun test_updateArticle() {
            val regReq = ArticleWrapper(
                content = UpdateArticleRequest(
                    title = "t",
                    description = "d",
                    body = "b",
                )
            )
            coEvery { mockArticleService.updateArticle(any(), any(), any()) } returns expArticleView
            webTestClient.put().uri("/api/articles/s1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(regReq)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .json(
                    expArticleViewStr, JsonCompareMode.STRICT
                )
        }

        @Test
        @DisplayName("test delete /articles/{slug} - happy path")
        fun test_deleteArticle() {
            coEvery { mockArticleService.deleteArticle(any(), any()) } returns Unit
            webTestClient.delete().uri("/api/articles/s1")
                .exchange()
                .expectStatus().isNoContent
                .expectBody()
                .isEmpty()
        }
    }

    @Nested
    inner class ArticleFavoriteUnitTests {
        @BeforeEach
        fun setup() {
            coEvery { mockUserSessionProvider.getCurrentUserOrFail() } returns expUser
        }

        @Test
        @DisplayName("test post /articles/{slug}/favorite - happy path")
        fun test_favoriteArticle() {
            val expFArticleView = expArticleView.copy(favorited = true)
            coEvery { mockArticleService.favoriteArticle("s1", any()) } returns expFArticleView
            webTestClient.post().uri("/api/articles/s1/favorite")
                .exchange()
                .expectStatus().isCreated
                .expectBody()
                .jsonPath("$.article.favorited").isEqualTo(true)
        }

        @Test
        @DisplayName("test delete /articles/{slug}/favorite - happy path")
        fun test_unfavoriteArticle() {
            val expFArticleView = expArticleView.copy(favorited = false)
            coEvery { mockArticleService.unfavoriteArticle("s1", any()) } returns expFArticleView
            webTestClient.delete().uri("/api/articles/s1/favorite")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.article.favorited").isEqualTo(false)
        }
    }

    @Nested
    inner class ArticleCommentsUnitTests {
        val expCommentView = CommentView(
            id = "1000",
            body = "b",
            createdAt = Instant.parse(refTsStr),
            updatedAt = Instant.parse(refTsStr),
            author = expProfileView
        )
        val expMCommentsViewStr = """
                {}
            """

        @BeforeEach
        fun setup() {
            coEvery { mockUserSessionProvider.getCurrentUserOrFail() } returns expUser
        }

        @Test
        @DisplayName("test get /articles/{slug}/comments - happy path")
        fun test_getComments() {
            coEvery {
                mockArticleService.getComments(
                    any(),
                    any()
                )
            } returns listOf(expCommentView).toMultipleCommentsView()
            webTestClient.get().uri("/api/articles/s1/comments")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .json(
                    """
                    {"comments":
                        [{"id":"1000",
                          "createdAt":"2021-05-03T10:30:00Z",
                          "updatedAt":"2021-05-03T10:30:00Z",
                          "body":"b",
                          "author":
                            {"username":"un",
                             "bio":null,
                             "image":null,
                             "following":false
                            }
                        }]
                     }""", JsonCompareMode.STRICT
                )
        }

        @Test
        @DisplayName("test post /articles/{slug}/comments .. {CreateCommentRequest} - happy path")
        fun test_addComment() {
            val regReq = CommentWrapper(
                content = CreateCommentRequest(
                    body = "b",
                )
            )
            coEvery { mockArticleService.addComment("s1", any(), any()) } returns expCommentView
            webTestClient.post().uri("/api/articles/s1/comments")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(regReq)
                .exchange()
                .expectStatus().isCreated
                .expectBody()
                .json(
                    """
                    {"comment":
                        {"id":"1000",
                         "createdAt":"2021-05-03T10:30:00Z",
                         "updatedAt":"2021-05-03T10:30:00Z",
                         "body":"b",
                         "author":
                            {"username":"un",
                             "bio":null,
                             "image":null,
                             "following":false
                            }
                        }
                    }""", JsonCompareMode.STRICT
                )
        }

        @Test
        @DisplayName("test delete /articles/{slug}/comments/{commentId} - happy path")
        fun test_deleteComment() {
            coJustRun { mockArticleService.deleteComment("s1", "cId", any()) }
            webTestClient.delete().uri("/api/articles/s1/comments/cId")
                .exchange()
                .expectStatus().isNoContent
                .expectBody()
                .isEmpty()
        }
    }

    @Nested
    inner class TagUnitTests {
        val expTag = Tag(
            id = "1000",
            tagName = "tn",
        )

        @Test
        @DisplayName("test get /tags - happy path")
        fun test_getTags() {
            coEvery { mockArticleService.getTags() } returns listOf(expTag).toTagListView()
            webTestClient.get().uri("/api/tags")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .json(
                    """
                    {"tags":
                        ["tn"]
                     }""", JsonCompareMode.STRICT
                )
        }
    }


    @TestConfiguration
    internal class Configuration {
        @Bean
        fun testController() = TestController()

        @Bean
        @Primary
        fun testEndpointsConfig() = Customizer<AuthorizeExchangeSpec> { http ->
            http.pathMatchers("/**").permitAll()
        }
    }
}

