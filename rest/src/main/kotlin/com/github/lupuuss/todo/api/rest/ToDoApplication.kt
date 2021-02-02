package com.github.lupuuss.todo.api.rest

import com.github.lupuuss.todo.api.core.Message
import com.github.lupuuss.todo.api.rest.config.Config
import com.github.lupuuss.todo.api.rest.config.configAuth
import com.github.lupuuss.todo.api.rest.config.configKodein
import com.github.lupuuss.todo.api.rest.controller.AuthController
import com.github.lupuuss.todo.api.rest.controller.MeController
import com.github.lupuuss.todo.api.rest.controller.exception.BadParamsException
import com.github.lupuuss.todo.api.rest.services.exception.ItemNotFoundException
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
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

        install(StatusPages) {
            exception<ItemNotFoundException> { cause ->
                call.respond(HttpStatusCode.NotFound, Message(cause.message ?: ""))
            }

            exception<BadParamsException> { cause ->
                call.respond(HttpStatusCode.BadRequest, Message(cause.message ?: ""))
            }
        }

        get("/") { call.respond("TO-DO List API!") }

        controller("/auth") { AuthController(instance()) }

        authenticate {
            controller("/me") {  MeController(instance()) }
        }
    }
}