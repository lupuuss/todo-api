package com.github.lupuuss.todo.api.rest.controller

import com.github.lupuuss.todo.api.core.user.User
import com.github.lupuuss.todo.api.rest.auth.JwtAuthManager
import com.github.lupuuss.todo.api.rest.auth.UserPrincipal
import com.github.lupuuss.todo.api.rest.services.TaskService
import com.github.lupuuss.todo.api.rest.ktor.logInfo
import com.github.lupuuss.todo.api.rest.ktor.logWarn
import com.github.lupuuss.todo.api.rest.services.UserService
import com.github.lupuuss.todo.api.rest.useBlock
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.receiveOrNull
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.delay
import org.kodein.di.instance
import org.kodein.di.ktor.controller.AbstractDIController
import org.litote.kmongo.json
import org.litote.kmongo.out

class LiveController(application: Application) : AbstractDIController(application) {

    private val taskService: TaskService by instance()
    private val userService: UserService by instance()
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

            useBlock {

                taskService.addOnTaskChangedListener(principal.id) {
                    outgoing.safeSendBlocking(Frame.Text("TASK"))
                    outgoing.safeSendBlocking(Frame.Text(it.json))
                }.use()

                if (principal.isInRole(User.Role.ADMIN)) {

                    userService.addOnUserChangeListener {
                        outgoing.safeSendBlocking(Frame.Text("USER"))
                        outgoing.safeSendBlocking(Frame.Text(it.json))
                    }.use()
                }

                while (true) {

                    incoming.receiveOrNull() ?: break
                }
            }
        }
    }

    private fun <T> SendChannel<T>.safeSendBlocking(frame: T) {
        if (isClosedForSend) return

        sendBlocking(frame)
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