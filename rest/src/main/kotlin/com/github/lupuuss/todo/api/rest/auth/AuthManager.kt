package com.github.lupuuss.todo.api.rest.auth

import com.github.lupuuss.todo.api.core.Credentials
import com.github.lupuuss.todo.api.core.User
import com.github.lupuuss.todo.api.rest.auth.hash.HashProvider
import com.github.lupuuss.todo.api.rest.auth.token.JwtTokenProvider
import com.github.lupuuss.todo.api.rest.repository.user.UserData
import com.github.lupuuss.todo.api.rest.repository.user.UserRepository
import java.util.concurrent.TimeUnit

class AuthManager(
    private val hash: HashProvider,
    private val tokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository
    ) {

    fun validateCredentials(credentials: Credentials): Claims? {

        val user = userRepository.findUserByLogin(credentials.login)

        user ?: return null

        hash.check(credentials.password, user.password)

        return user.getClaims()
    }

    private fun UserData.getClaims(): Claims = Claims(login, role)

    fun makeToken(claims: Claims): String {
        return tokenProvider.generateToken(mapOf(
            "login" to claims.login,
            "role" to claims.role.name
        ), 30, TimeUnit.MINUTES)
    }

    fun verifyToken(token: String): Claims? {
        val claims = tokenProvider.verifyToken(token)

        if (claims.isEmpty()) return null

        if (!claims.containsKey("login") || !claims.containsKey("role")) return null

        return Claims(claims["login"]!!, User.Role.valueOf(claims["role"]!!))
    }
}
