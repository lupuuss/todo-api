package com.github.lupuuss.todo.api.rest.repository.task

import com.github.lupuuss.todo.api.rest.repository.DataChange

interface TaskRepository {

    fun findTaskById(id: String): TaskData?

    fun findTasksByUser(userId: String, skip: Int? = null, limit: Int? = null): List<TaskData>

    fun findTasksByUserAndStatus(
        userId: String,
        status: TaskData.Status,
        skip: Int? = null,
        limit: Int? = null
    ): List<TaskData>

    fun replaceTask(task: TaskData)

    fun insertTask(task: TaskData): String

    fun deleteTask(id: String): Long

    fun addOnTaskChangeListener(userId: String, listener: suspend (DataChange<TaskData>) -> Unit): AutoCloseable
}