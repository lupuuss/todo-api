package com.github.lupuuss.todo.api.rest.controller

import com.github.lupuuss.todo.api.rest.controller.exception.BadParamsException
import io.ktor.application.*
import io.ktor.util.pipeline.*

fun  PipelineContext<*, ApplicationCall>.parsePositiveIntParam(name: String): Int {
    val value = call.parameters[name]
    return value
        ?.toInt()
        ?.takeIf { it >= 0 }
        ?: throw BadParamsException("Expected param '$name' to be positive integer; Received: '$value'")
}