package com.github.lupuuss.todo.api.core.user

data class NewUser(
    val login: String,
    val password: String,
    val email: String,
    val active: Boolean,
    val role: User.Role,
)