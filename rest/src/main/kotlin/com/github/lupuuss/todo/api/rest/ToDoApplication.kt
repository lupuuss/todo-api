package com.github.lupuuss.todo.api.rest

import com.github.lupuuss.todo.api.core.Message
import com.github.lupuuss.todo.api.core.user.User
import com.github.lupuuss.todo.api.rest.config.Config
import com.github.lupuuss.todo.api.rest.config.configAuth
import com.github.lupuuss.todo.api.rest.config.configKodein
import com.github.lupuuss.todo.api.rest.controller.AdminController
import com.github.lupuuss.todo.api.rest.controller.AuthController
import com.github.lupuuss.todo.api.rest.controller.LiveController
import com.github.lupuuss.todo.api.rest.controller.MeController
import com.github.lupuuss.todo.api.rest.controller.exception.BadParamsException
import com.github.lupuuss.todo.api.rest.services.exception.ItemAlreadyExistsException
import com.github.lupuuss.todo.api.rest.services.exception.ItemNotFoundException
import com.github.lupuuss.todo.api.rest.utils.ktor.authenticateWithRole
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*
import org.kodein.di.instance
import org.kodein.di.ktor.controller.controller
import org.slf4j.event.Level

fun Application.main() {
    routing {

        install(CallLogging) {
            level = Level.INFO
        }
        install(ContentNegotiation) { gson() }
        install(WebSockets)

        Config.init(environment.config)

        configKodein()
        configAuth()

        install(StatusPages) {

            exception<ItemAlreadyExistsException> { cause ->
                call.respond(HttpStatusCode.Conflict, Message(cause.message ?: ""))
            }

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

        authenticateWithRole(User.Role.ADMIN) {
            controller("/admin") { AdminController(instance()) }
        }

        controller("/me/live") { LiveController(instance()) }
    }
}