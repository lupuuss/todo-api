package com.github.lupuuss.todo.api.core.user

import kotlinx.serialization.Serializable

@Serializable
data class NewUser(
    val login: String,
    val password: String,
    val email: String,
    val active: Boolean,
    val role: User.Role,
)