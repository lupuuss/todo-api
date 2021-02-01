package com.github.lupuuss.todo.api.rest.config

import io.ktor.config.*

class Config private constructor(config: ApplicationConfig) {

    val jwtRealm= config.property("jwt.realm").getString()

    val jwtIssuer =  config.property("jwt.issuer").getString()

    val jwtSecret = config.property("jwt.secret").getString()

    val mongoConnectStr = config.property("mongo.connectStr").getString()

    val mongoDbName = config.property("mongo.databaseName").getString()

    companion object {

        private lateinit var instance: Config

        fun init(config: ApplicationConfig) {
            instance = Config(config)
        }

        fun get() = instance
    }
}