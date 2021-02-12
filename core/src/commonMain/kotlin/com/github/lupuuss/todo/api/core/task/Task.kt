package com.github.lupuuss.todo.api.core.task

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: String,
    val timestamp: Long,
    val userId: String,
    val name: String,
    val description: String?,
    val status: Status = Status.NOT_STARTED,
) {
    enum class Status {
        NOT_STARTED, IN_PROGRESS, DONE
    }
}