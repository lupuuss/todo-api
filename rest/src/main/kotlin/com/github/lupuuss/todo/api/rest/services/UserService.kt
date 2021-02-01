package com.github.lupuuss.todo.api.rest.services

import com.github.lupuuss.todo.api.core.user.User
import com.github.lupuuss.todo.api.rest.repository.user.UserRepository
import com.github.lupuuss.todo.api.rest.services.exception.ItemNotFoundException
import com.github.lupuuss.todo.api.rest.utils.mapping.mapToDomain

class UserService(
    private val repository: UserRepository,
) {

    fun getUser(login: String): User {

        return repository
            .findUserByLogin(login)
            ?.mapToDomain()
            ?: throw ItemNotFoundException("login", login)
    }
}