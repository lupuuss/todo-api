package com.github.lupuuss.todo.api.rest.repository.user

data class UserData(
    val _id: String? = null,
    val login: String,
    val email: String,
    val active: Boolean,
    val role: Role,
    val password: String
) {
    enum class Role {
        USER, ADMIN
    }
}