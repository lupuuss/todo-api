package com.github.lupuuss.todo.api.core.live

import kotlinx.serialization.Serializable

@Serializable
data class ItemChange<T>(
    val id: String?,
    val opType: Operation,
    val item: T?
)