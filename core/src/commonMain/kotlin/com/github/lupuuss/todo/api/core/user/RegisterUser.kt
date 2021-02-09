package com.github.lupuuss.todo.api.core.user

data class RegisterUser(
    val login: String,
    val email: String,
    val password: String,
)