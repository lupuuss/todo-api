package com.github.lupuuss.todo.api.rest.repository.task

data class TaskData(
    val timestamp: Long,
    val userId: String,
    val name: String,
    val description: String,
    val status: Status,
    val _id: String? = null,
) {
    enum class Status {
        NOT_STARTED, IN_PROGRESS, DONE
    }
}