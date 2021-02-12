package com.github.lupuuss.todo.api.core.user

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val login: String,
    val email: String,
    val active: Boolean,
    val role: Role,
) {
    enum class Role {
        USER, ADMIN
    }
}