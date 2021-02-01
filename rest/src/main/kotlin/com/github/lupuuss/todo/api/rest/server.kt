package com.github.lupuuss.todo.api.rest

import com.github.lupuuss.todo.api.rest.config.Config
import com.github.lupuuss.todo.api.rest.config.authConfig
import com.github.lupuuss.todo.api.rest.config.configKodein
import com.github.lupuuss.todo.api.rest.controller.AuthController
import io.ktor.application.call
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.kodein.di.instance
import org.kodein.di.ktor.controller.controller

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        routing {

            Config.init(environment.config)

            configKodein()

            authConfig()

            get("/") { call.respond("TO-DO List API!") }

            controller("/auth") { AuthController(instance()) }
        }
    }.start(wait = true)
}