package com.github.lupuuss.todo.api.rest.controller

import com.github.lupuuss.todo.api.rest.auth.AuthManager
import com.github.lupuuss.todo.api.rest.auth.JWTConfig
import com.github.lupuuss.todo.api.rest.auth.UserPrincipal
import com.github.lupuuss.todo.api.rest.services.TaskService
import com.github.lupuuss.todo.api.rest.utils.ktor.logError
import com.github.lupuuss.todo.api.rest.utils.ktor.logInfo
import com.github.lupuuss.todo.api.rest.utils.ktor.logWarn
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import org.kodein.di.instance
import org.kodein.di.ktor.controller.AbstractDIController
import org.litote.kmongo.json

class LiveController(application: Application) : AbstractDIController(application) {

    private val taskService: TaskService by instance()
    private val authManager: AuthManager by instance()
    private val verifier = JWTConfig.buildVerifier()

    override fun Route.getRoutes() {

        webSocket {

            logInfo("Connection received! Waiting for token...")

            val principal = authorize()

            if (principal == null) {
                logWarn("Token verification failed!")
                return@webSocket closeUnauthorized()
            } else {
                logInfo("Token verification successful! User '${principal.login}' is listening!")
            }

            taskService.streamUserTasksChange(principal.login).forEach {
                send(Frame.Text(it.json))
            }
        }
    }

    private suspend fun DefaultWebSocketServerSession.closeUnauthorized() {
        close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "Unauthorized"))
    }

    private suspend fun DefaultWebSocketServerSession.authorize(): UserPrincipal? {
        val principal = (incoming.receive() as? Frame.Text)
            ?.readText()
            ?.let { try { verifier.verify(it) } catch (e: Exception) { logError(e); null } }
            ?.claims
            ?.get("login")
            ?.asString()
            ?.let { authManager.validatePrincipal(it) }
            as? UserPrincipal

        return principal
    }
}