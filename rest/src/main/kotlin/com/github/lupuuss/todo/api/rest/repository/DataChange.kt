package com.github.lupuuss.todo.api.rest.repository

data class DataChange<T>(
    val _id: String?,
    val type: Type,
    val data: T?
) {
    enum class Type {
        DELETE, INSERT, UPDATE, DELETE_ALL
    }
}