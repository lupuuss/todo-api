package com.github.lupuuss.todo.api.rest.auth

import com.github.lupuuss.todo.api.core.user.Credentials
import com.github.lupuuss.todo.api.core.user.User
import com.github.lupuuss.todo.api.rest.auth.hash.HashProvider
import com.github.lupuuss.todo.api.rest.repository.user.UserRepository
import com.github.lupuuss.todo.api.rest.utils.mapping.mapToDomain
import io.ktor.auth.*

open class AuthManager(
    private val hash: HashProvider,
    private val userRepository: UserRepository
) {

    fun isUserActive(login: String): Boolean {
        return userRepository
            .findUserByLogin(login)
            ?.active
            ?: false
    }

    fun getUserIfActive(login: String): User? {
        return userRepository.findUserByLogin(login)
            ?.takeIf { it.active }
            ?.mapToDomain()
    }

    fun login(credentials: Credentials): User? {

        return userRepository
            .findUserByLogin(credentials.login)
            ?.takeIf { it.active }
            ?.takeIf { hash.check(credentials.password, it.password) }
            ?.mapToDomain()
    }

    fun validatePrincipal(login: String): Principal? {
        return getUserIfActive(login)?.let { UserPrincipal(it) }
    }
}