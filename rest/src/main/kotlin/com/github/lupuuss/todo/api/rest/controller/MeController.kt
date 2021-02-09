package com.github.lupuuss.todo.api.rest.controller

import com.github.lupuuss.todo.api.core.OperationResult
import com.github.lupuuss.todo.api.core.task.NewTask
import com.github.lupuuss.todo.api.core.task.PatchTask
import com.github.lupuuss.todo.api.core.task.Task
import com.github.lupuuss.todo.api.core.user.User
import com.github.lupuuss.todo.api.rest.auth.UserPrincipal
import com.github.lupuuss.todo.api.rest.controller.exception.BadParamsException
import com.github.lupuuss.todo.api.rest.services.TaskService
import com.github.lupuuss.todo.api.rest.services.UserService
import com.github.lupuuss.todo.api.rest.ktor.parsePositiveIntParam
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
        return userService.getUser(principal.id)
    }

    override fun Route.getRoutes() {

        get { call.respond(currentUser()) }

        get("/task") {
            val pageNumber = parsePositiveIntParam("pageNumber")
            val pageSize = parsePositiveIntParam("pageSize")
            val status = call.parameters["status"]?.let { Task.Status.valueOf(it) }

            val principal = call.principal<UserPrincipal>()!!

            call.respond(taskService.getTasksByUserId(principal.id, pageNumber, pageSize, status))
        }

        post("/task") {
            val newTask = call.receive<NewTask>()
            val principal = call.principal<UserPrincipal>()!!

            val task = taskService.createNewTaskForUser(principal.id, newTask)

            call.respond(task)
        }

        patch ("/task/{id}") {
            val patch = PatchTask(call.receive()).validated()

            val principal = call.principal<UserPrincipal>()!!
            val id = call.parameters["id"]!!

            if (!taskService.checkTaskBelongToUser(id, principal.id)) {

                call.respond(HttpStatusCode.Unauthorized)
                return@patch
            }

            taskService.patchTask(id, patch)
            call.respond(HttpStatusCode.NoContent)
        }

        delete ("/task/{id}") {
            val id = call.parameters["id"]!!
            val principal = call.principal<UserPrincipal>()!!

            if (!taskService.checkTaskBelongToUser(id, principal.id)) {
                call.respond(HttpStatusCode.Unauthorized)
                return@delete
            }

            val count = taskService.deleteTask(id)
            call.respond(OperationResult(count, OperationResult.Type.DELETE))
        }
    }

    private fun PatchTask.validated(): PatchTask {

        if (isExplicitSet(PatchTask::name) && name.isNullOrBlank()) throw BadParamsException("Name cannot be set to null!")

        try {
            status
        } catch (e: Exception) {
            throw BadParamsException("Bad enum constant!")
        }

        if (isExplicitSet(PatchTask::status) && status == null) throw BadParamsException("Status cannot be set to null!")

        return this
    }
}