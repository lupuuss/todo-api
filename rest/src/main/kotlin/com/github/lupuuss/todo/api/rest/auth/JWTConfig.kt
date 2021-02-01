package com.github.lupuuss.todo.api.rest.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.lupuuss.todo.api.core.User

object JWTConfig {

    private lateinit var algorithm: Algorithm
    private lateinit var issuer: String
    private lateinit var realm: String

    fun init(secret: String, issuer: String, realm: String) {
        algorithm = Algorithm.HMAC512(secret)
        this.issuer = issuer
        this.realm = realm
    }

    fun makeToken(user: User): String? {
        return JWT.create()
            .withClaim("login", user.login)
            .withClaim("role", user.role.name)
            .withIssuer(issuer)
            .sign(algorithm)
    }


    fun buildVerifier() = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()
}