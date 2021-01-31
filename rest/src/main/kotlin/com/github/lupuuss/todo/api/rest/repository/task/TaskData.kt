package com.github.lupuuss.todo.api.rest.repository.task

data class TaskData(
    val id: String? = null,
    val date: String,
    val userId: String,
    val name: String,
    val description: String,
    val status: Status,
) {
    enum class Status {
        NOT_STARTED, IN_PROGRESS, DONE
    }
}