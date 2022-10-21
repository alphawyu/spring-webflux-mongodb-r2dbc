package com.realworld.springmongo.persistence.repository

import com.realworld.springmongo.persistence.entity.Tag
import com.realworld.springmongo.persistence.entity.toTag
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

interface TagRepository : ReactiveMongoRepository<Tag, String>

fun TagRepository.saveAllTags(tags: Iterable<String>): Mono<List<Tag>> = tags.toFlux()
    .flatMap { save(it.toTag()) }
    .onErrorContinue(DuplicateKeyException::class.java, ::ignoreException)
    .collectList()

private fun ignoreException(throwable: Throwable, obj: Any) {}