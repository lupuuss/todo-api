package com.github.lupuuss.todo.api.core.task

data class NewTask(
    val name: String,
    val description: String,
    val status: Task.Status = Task.Status.NOT_STARTED,
)