package com.github.lupuuss.todo.api.core

import kotlinx.serialization.Serializable

@Serializable
data class Page<T>(
    val elements: List<T>,
    val pageNumber: Int,
    val pageSize: Int,
    val nextPage: Int?
)