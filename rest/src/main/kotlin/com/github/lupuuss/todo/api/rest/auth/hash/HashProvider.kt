package com.github.lupuuss.todo.api.rest.auth.hash

interface HashProvider {

    fun generate(password: String): String

    fun check(plain: String, hash: String): Boolean
}