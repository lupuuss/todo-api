package com.github.lupuuss.todo.api.rest.ktor.config

import io.ktor.config.*
import java.time.Duration
import java.time.temporal.ChronoUnit

class Config private constructor(config: ApplicationConfig) {

    val jwtExpire = parseTimeDuration(config.property("jwt.expire").getString())

    val jwtRefresh = parseTimeDuration(config.property("jwt.refresh").getString())

    val jwtRealm= config.property("jwt.realm").getString()

    val jwtIssuer =  config.property("jwt.issuer").getString()

    val jwtSecret = config.property("jwt.secret").getString()

    val mongoConnectStr = config.property("mongo.connectStr").getString()

    val mongoDbName = config.property("mongo.databaseName").getString()

    private fun parseTimeDuration(str: String): Duration {
        val (amount, unit) = str.split(":")

        return ChronoUnit.valueOf(unit.toUpperCase()).duration.multipliedBy(amount.toLong())
    }


    companion object {

        private lateinit var instance: Config

        fun init(config: ApplicationConfig) {
            instance = Config(config)
        }

        fun get() = instance
    }
}