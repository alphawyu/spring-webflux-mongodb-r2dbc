package com.realworld.spring.webflux.api

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RouterConfig(
    private val userHandler: UserHandler,
    private val articleHandler: ArticleHandler
) {
    @Bean
    fun apiRouter() = coRouter {
        "/api".nest {
            accept(APPLICATION_JSON).nest {
                "/users".nest {
                    contentType(APPLICATION_JSON).nest {
                        POST("", userHandler::signup)
                        POST("login", userHandler::login)
                    }
                }

                "/user".nest {
                    GET("", userHandler::getCurrentUser)
                    contentType(APPLICATION_JSON).nest {
                        PUT("", userHandler::updateUser)
                    }
                }

                "/profiles".nest {
                    GET("/{username}", userHandler::getProfile)
                    POST("/{username}/follow", userHandler::follow)
                    DELETE("/{username}/follow", userHandler::unfollow)
                }

                "/articles".nest {
                    GET("", articleHandler::getArticles)
                    GET("/feed", articleHandler::feed)
                    GET("/{slug}", articleHandler::getArticle)
                    DELETE("/{slug}", articleHandler::deleteArticle)
                    POST("/{slug}/favorite", articleHandler::favoriteArticle)
                    DELETE("/{slug}/favorite", articleHandler::unfavoriteArticle)
                    GET("/{slug}/comments", articleHandler::getComments)
                    DELETE("/{slug}/comments/{commentId}", articleHandler::deleteComment)
                    contentType(APPLICATION_JSON).nest {
                        POST("", articleHandler::createArticle)
                        PUT("/{slug}", articleHandler::updateArticle)
                        POST("/{slug}/comments", articleHandler::addComment)
                    }
                }

                GET("/tags", articleHandler::getTags)
            }
        }
    }
}
