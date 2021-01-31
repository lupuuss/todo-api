package com.github.lupuuss.todo.api.rest.auth.hash

import org.mindrot.jbcrypt.BCrypt

class BCryptHashProvider : HashProvider {

    override fun generate(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    override fun check(plain: String, hash: String): Boolean {
        return BCrypt.checkpw(plain, hash)
    }
}