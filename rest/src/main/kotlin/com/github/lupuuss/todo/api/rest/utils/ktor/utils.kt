package com.github.lupuuss.todo.api.rest.utils.ktor

import io.ktor.application.*
import io.ktor.util.pipeline.*
import io.ktor.websocket.*

fun PipelineContext<Unit, ApplicationCall>.logInfo(message: String) = call.application.environment.log.info(message)

fun PipelineContext<Unit, ApplicationCall>.logWarn(message: String) = call.application.environment.log.warn(message)

fun PipelineContext<Unit, ApplicationCall>.logError(message: String) = call.application.environment.log.error(message)

fun PipelineContext<Unit, ApplicationCall>.logError(throwable: Throwable) = call.application.environment.log.error(throwable.message)

fun WebSocketServerSession.logInfo(message: String) = call.application.environment.log.info(message)

fun WebSocketServerSession.logWarn(message: String) = call.application.environment.log.warn(message)

fun WebSocketServerSession.logError(message: String) = call.application.environment.log.error(message)

fun WebSocketServerSession.logError(throwable: Throwable) = call.application.environment.log.error(throwable.message)
