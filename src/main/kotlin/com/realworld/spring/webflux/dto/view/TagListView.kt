package com.realworld.spring.webflux.dto.view

import com.realworld.spring.webflux.persistence.entity.Tag

data class TagListView(val tags: List<String> = emptyList())

fun List<Tag>.toTagListView() = TagListView(this.map(Tag::tagName))