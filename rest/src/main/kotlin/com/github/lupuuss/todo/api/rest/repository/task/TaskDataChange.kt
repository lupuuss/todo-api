package com.github.lupuuss.todo.api.rest.repository.task

data class TaskDataChange(
    val _id: String,
    val type: Type,
    val task: TaskData?
) {
    enum class Type {
        DELETE, INSERT, UPDATE
    }
}