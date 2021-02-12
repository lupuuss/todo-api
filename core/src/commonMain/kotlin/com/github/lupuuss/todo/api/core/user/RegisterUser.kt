package com.github.lupuuss.todo.api.core.user

import kotlinx.serialization.Serializable

@Serializable
data class RegisterUser(
    val login: String,
    val email: String,
    val password: String,
)