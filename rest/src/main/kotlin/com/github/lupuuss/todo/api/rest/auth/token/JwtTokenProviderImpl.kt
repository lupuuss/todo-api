package com.github.lupuuss.todo.api.rest.auth.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.concurrent.TimeUnit

class JwtTokenProviderImpl(
    private val audience: String,
    private val issuer: String,
    private val secret: String
) : JwtTokenProvider {

    private val algorithm = Algorithm.HMAC256(secret)

    override fun generateToken(claims: Map<String, String>, time: Int, unit: TimeUnit): String  {
        var jwt = JWT.create()

        for ((claimName, claimValue) in claims) {

            jwt = jwt.withClaim(claimName, claimValue)
        }

        return jwt
            .withAudience(audience)
            .withIssuer(issuer)
            .sign(algorithm)
    }

    override fun verifyToken(token: String): Map<String, String> {
        return try {
            JWT.require(algorithm)
                .build()
                .verify(token)
                .claims.map { (key, claim) -> key to claim.asString() }
                .toMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }
}