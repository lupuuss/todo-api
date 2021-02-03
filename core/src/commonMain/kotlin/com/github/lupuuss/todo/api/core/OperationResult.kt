package com.github.lupuuss.todo.api.core

data class OperationResult(val count: Long, val type: Type) {
    enum class Type {
        DELETE, UPDATE
    }
}