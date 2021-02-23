package com.github.lupuuss.todo.api.rest.controller

import com.github.lupuuss.todo.api.core.user.NewUser
import com.github.lupuuss.todo.api.core.user.PatchUser
import com.github.lupuuss.todo.api.core.user.User
import com.github.lupuuss.todo.api.rest.auth.UserPrincipal
import com.github.lupuuss.todo.api.rest.controller.exception.BadParamsException
import com.github.lupuuss.todo.api.rest.services.UserService
import com.github.lupuuss.todo.api.rest.ktor.parsePositiveIntParam
import com.github.lupuuss.todo.api.rest.ktor.validation.isNotValidEmail
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
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
            val newUser = call.receive<NewUser>().validated()
            val user = userService.createUser(newUser)
            call.respond(user)
        }

        patch("user/{id}") {
            val id = call.parameters["id"]!!

            val patch = PatchUser(call.receive()).validated()

            userService.patchUser(id, patch)

            call.respond(HttpStatusCode.NoContent)
        }
    }
}

private fun NewUser.validated(): NewUser {

    if (login.isBlank()) throw BadParamsException("Login cannot be empty!")

    if (email.isNotValidEmail()) throw BadParamsException("Email is invalid")

    if (password.isBlank()) throw BadParamsException("Password cannot be empty!")

    return this
}


private fun PatchUser.validated(): PatchUser {

    if (isExplicitSet(PatchUser::login) && login.isNullOrBlank()) throw BadParamsException("Login cannot be empty!")

    if (isExplicitSet(PatchUser::email) && email.isNotValidEmail()) throw BadParamsException("Email is invalid")

    if (isExplicitSet(PatchUser::password) && password.isNullOrBlank()) throw BadParamsException("Passwrod cannot be empty!")

    if (isExplicitSet(PatchUser::active) && active == null) throw BadParamsException("Active cannot be null!")

    if (isExplicitSet(PatchUser::role)) {
        try {
            role
        } catch (e: Exception) {
            throw BadParamsException("Bad enum at field 'role'!")
        }

        if (role == null) {
            throw BadParamsException("Role cannot be null!")
        }
    }

    return this
}
