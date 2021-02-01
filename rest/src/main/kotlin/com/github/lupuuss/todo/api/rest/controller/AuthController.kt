package com.github.lupuuss.todo.api.rest.controller

import com.github.lupuuss.todo.api.core.user.Credentials
import com.github.lupuuss.todo.api.rest.auth.AuthManager
import com.github.lupuuss.todo.api.rest.auth.JWTConfig
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.controller.AbstractDIController

class AuthController(application: Application) : AbstractDIController(application) {

    private val authManager: AuthManager by instance()

    override fun Route.getRoutes() {

        post("/login") {

            val credential = call.receive<Credentials>()

            val user = authManager.login(credential) ?: return@post call.respond(HttpStatusCode.Unauthorized)

            val token = JWTConfig.makeToken(user) ?: return@post call.respond(HttpStatusCode.Unauthorized)

            call.respond(token)
        }

    }
}