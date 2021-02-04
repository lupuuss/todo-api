package com.github.lupuuss.todo.api.rest.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.github.lupuuss.todo.api.core.user.Credentials
import com.github.lupuuss.todo.api.core.user.User
import com.github.lupuuss.todo.api.rest.auth.hash.HashProvider
import com.github.lupuuss.todo.api.rest.repository.user.UserRepository
import java.time.Duration
import java.time.Instant
import java.util.*

class JwtAuthManager(
    hash: HashProvider,
    userRepository: UserRepository,
    private val algorithm: Algorithm,
    private val issuer: String,
    private val realm: String,
    private val expire: Duration,
    private val refresh: Duration,
) : AuthManager(hash, userRepository) {

    val verifier: JWTVerifier
    get() = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    private fun expirationDate() = Date.from(Instant.now().plus(expire))

    fun loginJwt(credentials: Credentials): String? {
        val user = login(credentials) ?: return null

        return makeToken(user, expirationDate())
    }

    private fun makeToken(user: User, expireDate: Date): String? {
        return JWT.create()
            .withClaim("login", user.login)
            .withClaim("role", user.role.name)
            .withIssuer(issuer)
            .withExpiresAt(expireDate)
            .sign(algorithm)
    }

    fun refreshToken(token: String): String? {

        val decoded = try {
            JWT.decode(token)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        val login = decoded.claims["login"]?.asString() ?: return null

        val expired = Duration.between(Instant.now(), decoded.expiresAt.toInstant())

        if (expired > refresh) {
            return null
        }

        val user = getUserIfActive(login) ?: return null

        return makeToken(user, expirationDate())
    }

    fun verifyJwt(token: String): UserPrincipal? {
        val decoded = try {
            verifier.verify(token)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        return decoded
            ?.claims
            ?.get("login")
            ?.asString()
            ?.let { validatePrincipal(it) }
                as? UserPrincipal
    }
}