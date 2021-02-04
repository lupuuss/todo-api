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

    fun isUserActive(id: String): Boolean {
        return userRepository
            .findUserById(id)
            ?.active
            ?: false
    }

    private fun getUserIfActive(id: String): User? {
        return userRepository.findUserById(id)
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

    fun validatePrincipal(id: String): Principal? {
        return getUserIfActive(id)?.let { UserPrincipal(it) }
    }
}