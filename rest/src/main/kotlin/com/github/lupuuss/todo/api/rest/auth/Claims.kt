package com.github.lupuuss.todo.api.rest.auth

import com.github.lupuuss.todo.api.core.User

data class Claims(
    val login: String,
    val role: User.Role
)