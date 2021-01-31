package com.github.lupuuss.todo.api.rest.repository.user

import com.github.lupuuss.todo.api.core.User

data class UserData(
    val id: String? = null,
    val login: String,
    val active: Boolean,
    val role: User.Role,
    val password: String
)