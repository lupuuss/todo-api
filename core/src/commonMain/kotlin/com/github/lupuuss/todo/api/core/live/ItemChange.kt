package com.github.lupuuss.todo.api.core.live

data class ItemChange<T>(
    val id: String?,
    val opType: Operation,
    val item: T?
)