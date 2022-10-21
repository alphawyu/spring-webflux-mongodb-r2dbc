package com.realworld.springmongo.dto.view

import com.realworld.springmongo.persistence.entity.Tag

data class TagListView(val tags: List<String> = emptyList())

fun List<Tag>.toTagListView() = TagListView(this.map(Tag::tagName))