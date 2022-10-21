package com.realworld.spring.webflux.exceptions

class InvalidRequestException(val subject: String, val violation: String) : RuntimeException("$subject: $violation")