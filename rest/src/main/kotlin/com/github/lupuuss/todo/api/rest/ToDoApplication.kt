package com.github.lupuuss.todo.api.rest

import com.github.lupuuss.todo.api.rest.config.Config
import com.github.lupuuss.todo.api.rest.config.configAuth
import com.github.lupuuss.todo.api.rest.config.configKodein
import com.github.lupuuss.todo.api.rest.controller.AuthController
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.controller.controller

fun Application.main() {
    routing {

        install(CallLogging)
        install(ContentNegotiation) { gson() }

        Config.init(environment.config)

        configKodein()
        configAuth()

        get("/") { call.respond("TO-DO List API!") }

        controller("/auth") { AuthController(instance()) }
    }
}