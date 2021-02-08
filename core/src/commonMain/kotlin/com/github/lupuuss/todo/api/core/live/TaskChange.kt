package com.github.lupuuss.todo.api.core.live

import com.github.lupuuss.todo.api.core.task.Task

data class TaskChange(
    val id: String,
    val type: Operation,
    val task: Task?
) {
}