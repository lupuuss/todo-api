package com.github.lupuuss.todo.api.rest.controller

import com.github.lupuuss.todo.api.core.task.NewTask
import com.github.lupuuss.todo.api.core.user.User
import com.github.lupuuss.todo.api.rest.auth.UserPrincipal
import com.github.lupuuss.todo.api.rest.services.TaskService
import com.github.lupuuss.todo.api.rest.services.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import org.kodein.di.instance
import org.kodein.di.ktor.controller.AbstractDIController

class MeController(application: Application) : AbstractDIController(application) {

    private val taskService: TaskService by instance()
    private val userService: UserService by instance()

    private fun PipelineContext<Unit, ApplicationCall>.currentUser(): User {
        val principal = call.principal<UserPrincipal>()!!
        return userService.getUser(principal.login)
    }

    override fun Route.getRoutes() {

        get { call.respond(currentUser()) }

        get("/task") {
            val pageNumber = parsePositiveIntParam("pageNumber")
            val pageSize = parsePositiveIntParam("pageSize")

            val principal = call.principal<UserPrincipal>()!!

            call.respond(taskService.getTasksByUserLogin(principal.login, pageNumber, pageSize))
        }

        post("/task") {
            val newTask = call.receive<NewTask>()
            val principal = call.principal<UserPrincipal>()!!

            taskService.createNewTaskForUser(principal.login, newTask)

            call.respond(HttpStatusCode.OK)
        }
    }
}