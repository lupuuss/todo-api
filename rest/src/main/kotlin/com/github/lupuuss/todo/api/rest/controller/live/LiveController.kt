package com.github.lupuuss.todo.api.rest.controller.live

import com.github.lupuuss.todo.api.core.live.Commands
import com.github.lupuuss.todo.api.core.user.User
import com.github.lupuuss.todo.api.rest.auth.JwtAuthManager
import com.github.lupuuss.todo.api.rest.auth.UserPrincipal
import com.github.lupuuss.todo.api.rest.services.TaskService
import com.github.lupuuss.todo.api.rest.ktor.logInfo
import com.github.lupuuss.todo.api.rest.services.UserService
import com.github.lupuuss.todo.api.rest.useBlock
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.receiveOrNull
import kotlinx.coroutines.withTimeoutOrNull
import org.kodein.di.instance
import org.kodein.di.ktor.controller.AbstractDIController
import org.litote.kmongo.json

@Suppress("EXPERIMENTAL_API_USAGE")
class LiveController(application: Application) : AbstractDIController(application) {

    private val authTimeoutMs: Long = 30_000
    private val taskService: TaskService by instance()
    private val userService: UserService by instance()
    private val authManager: JwtAuthManager by instance()

    private val outgoingQueue = OutgoingQueue<Frame>()

    override fun Route.getRoutes() {

        webSocket {

            logInfo("Connection received!")

            useBlock {
                // Every AutoCloseable called with .use() will be closed automatically at the end of this block.

                var (principal, tokenExpired) = auth() ?: return@webSocket closeUnauthorized()

                logInfo("User '${principal.login}' is listening!")

                taskService.addOnTaskChangedListener(principal.id) {
                    outgoing.safeSendText(Commands.taskIncoming)
                    outgoing.safeSendText(it.json)
                }.use()

                if (principal.isInRole(User.Role.ADMIN)) {

                    userService.addOnUserChangeListener {
                        outgoing.safeSendText(Commands.userIncoming)
                        outgoing.safeSendText(it.json)
                    }.use()
                }

                do {

                    logInfo("Token expires in: $tokenExpired ms")

                    val result = withTimeoutOrNull(tokenExpired) {
                        while (true) {
                            incoming.receiveOrNull() ?: break
                        }
                        return@withTimeoutOrNull 0
                    }

                    if (result != null) break

                    logInfo("Token refresh required...")

                    // pause outgoing queue to avoid notifications before re-auth
                    outgoingQueue.pause()

                    tokenExpired = auth()?.second ?: return@webSocket closeUnauthorized()

                    // sends notifications, that have been created in auth time.
                    outgoingQueue.unpauseAndFlush(outgoing)

                } while (!incoming.isClosedForReceive)
            }

            logInfo("Connection closed!")
        }
    }

    private fun String.framed(): Frame.Text {
        return Frame.Text(this)
    }

    /**
     * Sends text frames using [outgoingQueue].
     */
    private suspend fun SendChannel<Frame>.safeSendText(text: String) {
        if (isClosedForSend) return

        outgoingQueue.send(this, Frame.Text(text))
    }

    private suspend fun DefaultWebSocketServerSession.closeUnauthorized() {
        logInfo("Connection closed unauthorized!")
        close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "Unauthorized"))
    }

    private suspend fun DefaultWebSocketServerSession.askForToken(): String? {

        outgoing.send(Frame.Text(Commands.authExpected))

        return withTimeoutOrNull(authTimeoutMs) {
            (incoming.receiveOrNull() as? Frame.Text)?.readText()
        }
    }

    private suspend fun DefaultWebSocketServerSession.auth(): Pair<UserPrincipal, Long>? {

        logInfo("Waiting for token...")
        val token = askForToken()
        val principal = token?.let { authManager.verifyJwt(it) } ?: return null

        logInfo("Token OK!")

        val tokenExpired = authManager.expiresInMs(token)

        return principal to tokenExpired
    }
}