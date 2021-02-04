package com.github.lupuuss.todo.api.rest.controller

import com.github.lupuuss.todo.api.core.user.Credentials
import com.github.lupuuss.todo.api.rest.auth.AuthManager
import com.github.lupuuss.todo.api.rest.auth.JwtAuthManager
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.controller.AbstractDIController

class AuthController(application: Application) : AbstractDIController(application) {

    private val authManager: JwtAuthManager by instance()

    override fun Route.getRoutes() {

        post("/login") {

            val credential = call.receive<Credentials>()

            val token = authManager.loginJwt(credential) ?: return@post call.respond(HttpStatusCode.Unauthorized)

            call.respond(token)
        }

        post("/token") {

            val split = call
                .request
                .headers["Authorization"]
                ?.split(" ")
                ?: return@post call.respond(HttpStatusCode.Unauthorized)

            if (split.size < 2) return@post call.respond(HttpStatusCode.Unauthorized)

            val (type, value) = split

            if (type != "Bearer") return@post call.respond(HttpStatusCode.Unauthorized)

            val token = authManager.refreshToken(value) ?: return@post call.respond(HttpStatusCode.Unauthorized)

            call.respond(token)
        }

    }
}