package com.github.lupuuss.todo.api.rest.auth.token

import java.util.concurrent.TimeUnit

interface JwtTokenProvider {

    fun verifyToken(token: String): Map<String, String>

    fun generateToken(claims: Map<String, String>, time: Int, unit: TimeUnit): String
}