package com.github.lupuuss.todo.api.rest.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.lupuuss.todo.api.core.user.User
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

object JWTConfig {

    private lateinit var algorithm: Algorithm
    private lateinit var issuer: String
    private lateinit var realm: String
    private val expireTimeHours = 10L

    fun init(secret: String, issuer: String, realm: String) {
        algorithm = Algorithm.HMAC512(secret)
        this.issuer = issuer
        this.realm = realm
    }

    fun makeToken(user: User): String? {

        val date = Date.from(Instant.now().plus(expireTimeHours, ChronoUnit.HOURS))

        return JWT.create()
            .withClaim("login", user.login)
            .withClaim("role", user.role.name)
            .withIssuer(issuer)
            .withExpiresAt(date)
            .sign(algorithm)
    }


    fun buildVerifier() = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()
}