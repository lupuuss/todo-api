package com.github.lupuuss.todo.api.core

data class AuthOk(
    val user: User,
    val token: String
)