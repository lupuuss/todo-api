package com.github.lupuuss.todo.api.rest.controller

import com.github.lupuuss.todo.api.rest.auth.JwtAuthManager
import com.github.lupuuss.todo.api.rest.auth.UserPrincipal
import com.github.lupuuss.todo.api.rest.services.TaskService
import com.github.lupuuss.todo.api.rest.ktor.logInfo
import com.github.lupuuss.todo.api.rest.ktor.logWarn
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.receiveOrNull
import kotlinx.coroutines.channels.sendBlocking
import org.kodein.di.instance
import org.kodein.di.ktor.controller.AbstractDIController
import org.litote.kmongo.json

class LiveController(application: Application) : AbstractDIController(application) {

    private val taskService: TaskService by instance()
    private val authManager: JwtAuthManager by instance()

    override fun Route.getRoutes() {

        webSocket {

            logInfo("Connection received! Waiting for token...")

            val principal = receiveAndVerifyToken()

            if (principal == null) {
                logWarn("Token verification failed!")
                return@webSocket closeUnauthorized()
            } else {
                logInfo("Token verification successful! User '${principal.login}' is listening!")
            }

            val disposer = taskService.addOnTaskChangedListener(principal.id) {

                if (!outgoing.isClosedForSend) outgoing.sendBlocking(Frame.Text(it.json))
            }

            disposer.use {
                while (true) {
                    incoming.receiveOrNull() ?: break
                }
            }
        }
    }

    private suspend fun DefaultWebSocketServerSession.closeUnauthorized() {
        close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "Unauthorized"))
    }

    private suspend fun DefaultWebSocketServerSession.receiveAndVerifyToken(): UserPrincipal? {

        return (incoming.receive() as? Frame.Text)
            ?.readText()
            ?.let { authManager.verifyJwt(it) }
    }
}