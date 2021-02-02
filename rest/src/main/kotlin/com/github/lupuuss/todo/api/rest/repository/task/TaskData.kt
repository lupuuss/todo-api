package com.github.lupuuss.todo.api.rest.repository.task

data class TaskData(
    var timestamp: Long,
    var userId: String,
    var name: String,
    var description: String?,
    var status: Status,
    var _id: String? = null,
) {
    enum class Status {
        NOT_STARTED, IN_PROGRESS, DONE
    }
}