package com.github.lupuuss.todo.api.rest.repository.task

data class TaskData(
    var _id: String?,
    val timestamp: Long,
    val userId: String,
    var name: String,
    var description: String?,
    var status: Status,
) {

    constructor(
        timestamp: Long,
        userId: String,
        name: String,
        description: String?,
        status: Status
    ) : this(null, timestamp, userId, name, description, status)

    enum class Status {
        NOT_STARTED, IN_PROGRESS, DONE
    }
}