package com.github.lupuuss.todo.api.core.user

import kotlinx.serialization.Serializable

@Serializable
data class Credentials(
    val login: String,
    val password: String,
)