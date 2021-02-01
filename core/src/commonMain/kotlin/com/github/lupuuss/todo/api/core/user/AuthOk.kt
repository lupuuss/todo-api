package com.github.lupuuss.todo.api.core.user

import com.github.lupuuss.todo.api.core.user.User

data class AuthOk(
    val user: User,
    val token: String
)