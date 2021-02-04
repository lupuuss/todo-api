package com.github.lupuuss.todo.api.rest.auth

import com.github.lupuuss.todo.api.core.user.User
import io.ktor.auth.*

class UserPrincipal(private val user: User): Principal {

    val id: String
    get() = user.id

    val login: String
    get() = user.login

    val role: User.Role
    get() = user.role

    fun isInRole(role: User.Role) = user.role == role
}