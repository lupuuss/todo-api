package com.github.lupuuss.todo.api.core

import kotlinx.serialization.Serializable

@Serializable
data class OperationResult(val count: Long, val type: Type) {
    @Serializable
    enum class Type {
        DELETE, UPDATE
    }
}