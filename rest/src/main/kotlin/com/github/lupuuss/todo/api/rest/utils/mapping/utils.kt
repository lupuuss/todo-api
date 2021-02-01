package com.github.lupuuss.todo.api.rest.utils.mapping

import com.github.lupuuss.todo.api.core.User
import com.github.lupuuss.todo.api.rest.repository.user.UserData

fun UserData.mapToDomain() = User(
    _id!!,
    login,
    email,
    active,
    User.Role.valueOf(role.name)
)