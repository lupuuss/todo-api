package com.github.lupuuss.todo.api.core.live

import kotlinx.serialization.Serializable

@Serializable
enum class Operation {
    DELETE, DELETE_ALL, INSERT, UPDATE
}