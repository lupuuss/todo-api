package com.github.lupuuss.todo.api.rest

import io.ktor.application.call
import io.ktor.response.*
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        routing {
            get("/") {
                call.respond("Hello world!")
            }
        }
    }.start(wait = true)
}