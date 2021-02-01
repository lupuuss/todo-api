package com.github.lupuuss.todo.api.rest.config

import io.ktor.config.*

class Config private constructor(config: ApplicationConfig) {

    val jwtRealm= config.property("jwt.realm").toString()

    val jwtIssuer =  config.property("jwt.issuer").getString()

    val jwtSecret = config.property("jwt.secret").toString()

    val mongoConnectStr = config.property("mongo.connectStr").toString()

    val mongoDbName = config.property("mongo.databaseName").toString()

    companion object {

        private lateinit var instance: Config

        fun init(config: ApplicationConfig) {
            instance = Config(config)
        }

        fun get() = instance
    }
}