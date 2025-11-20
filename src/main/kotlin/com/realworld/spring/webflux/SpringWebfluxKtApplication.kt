package com.realworld.spring.webflux

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@SpringBootApplication
@EnableReactiveMongoRepositories
class SpringWebfluxKtApplication

fun main(args: Array<String>) {
	runApplication<SpringWebfluxKtApplication>(*args)
}
