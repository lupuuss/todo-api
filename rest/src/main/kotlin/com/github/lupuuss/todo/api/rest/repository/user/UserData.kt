package com.github.lupuuss.todo.api.rest.repository.user

data class UserData(
    var _id: String? = null,
    var login: String,
    var email: String,
    var active: Boolean,
    var role: Role,
    var password: String
) {
    enum class Role {
        USER, ADMIN
    }
}