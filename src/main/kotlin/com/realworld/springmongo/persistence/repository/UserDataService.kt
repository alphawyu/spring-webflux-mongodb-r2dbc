package com.realworld.springmongo.persistence.repository

import com.realworld.springmongo.dto.User
import com.realworld.springmongo.exceptions.InvalidRequestException
import com.realworld.springmongo.persistence.entity.ArticleEntity
import com.realworld.springmongo.persistence.entity.UserEntity
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Component
class UserDataService(
    private val userRepository: UserRepository,
) {
    @Transactional
    suspend fun save(user: User): User {
        System.out.println(user)
        val userEntity = userRepository.save(user.toUserEntity()).awaitSingle()
        return userEntity.toUser()
    }

    suspend fun findById(userId: Long): User {
        val userEntity = userRepository.findById(userId).awaitSingle()
        return toUser(userEntity = userEntity)
    }

    suspend fun findByEmail(email: String): User {
        val userEntity = userRepository.findByEmail(email).awaitSingle()
        return toUser(userEntity = userEntity)
    }

    suspend fun existsByEmail(email: String) = userRepository.existsByEmail(email)

    suspend fun existsByUsername(username: String) = userRepository.existsByUsername(username)

    suspend fun findByUsername(username: String): User? {
        val userEntity = userRepository.findByUsername(username).awaitSingleOrNull()
        return if (userEntity != null) toUser(userEntity = userEntity) else null
    }

    suspend fun findByUsernameOrFail(username: String): User {
        val userEntity = userRepository.findByUsername(username)
            .switchIfEmpty(Mono.error(InvalidRequestException("Username", "not found")))
            .awaitSingle()
        return toUser(userEntity = userEntity)
    }

    suspend fun findByEmailOrFail(email: String): User {
        val userEntity = userRepository.findByEmail(email)
            .switchIfEmpty(Mono.error(InvalidRequestException("Email", "not found")))
            .awaitSingle()
        return toUser(userEntity = userEntity)
    }

    suspend fun findAuthorByArticle(article: ArticleEntity): User {
        val userEntity = userRepository.findById(article.authorId)
            .switchIfEmpty(Mono.error(InvalidRequestException("Author", "not found")))
            .awaitSingle()
        return toUser(userEntity = userEntity)
    }

    private suspend fun toUser(userEntity: UserEntity): User {
        return userEntity.toUser()
    }
}