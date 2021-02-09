package com.github.lupuuss.todo.api.core.live

object Commands {
    const val prefix = "!"

    const val authExpected = "${prefix}AUTH"
    const val authOk = "${prefix}AUTH_OK"

    const val authBad = "${prefix}AUTH_BAD"

    const val taskIncoming = "${prefix}INCOMING_TASK"
    const val userIncoming = "${prefix}INCOMING_USER"
}