package com.github.lupuuss.todo.api.rest.controller

import com.github.lupuuss.todo.api.core.user.NewUser
import com.github.lupuuss.todo.api.core.user.User
import com.github.lupuuss.todo.api.rest.auth.UserPrincipal
import com.github.lupuuss.todo.api.rest.services.UserService
import com.github.lupuuss.todo.api.rest.utils.ktor.parsePositiveIntParam
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import org.kodein.di.instance
import org.kodein.di.ktor.controller.AbstractDIController

class AdminController(application: Application) : AbstractDIController(application) {

    private val userService: UserService by instance()

    private fun PipelineContext<Unit, ApplicationCall>.currentUser(): User {
        val principal = call.principal<UserPrincipal>()!!
        return userService.getUser(principal.id)
    }

    override fun Route.getRoutes() {
        get { call.respond(currentUser()) }

        get("user") {
            val pageNumber = parsePositiveIntParam("pageNumber")
            val pageSize = parsePositiveIntParam("pageSize")

            call.respond(userService.getAllUsers(pageNumber, pageSize))
        }

        get("user/{id}") {
            val id = call.parameters["id"]!!

            call.respond(userService.getUser(id))
        }

        post("user") {
            val newUser = call.receive<NewUser>()
            val user = userService.createUser(newUser)
            call.respond(user)
        }
    }
}