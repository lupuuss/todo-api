package com.github.lupuuss.todo.api.core

data class User(
    val id: String,
    val login: String,
    val active: Boolean,
    val role: Role,
) {
    enum class Role {
        USER, ADMIN
    }

    fun isAdmin() = role == Role.ADMIN
}