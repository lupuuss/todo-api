package com.github.lupuuss.todo.api.rest.repository.task

interface TaskRepository {

    fun findTasksByUser(userId: String, skip: Int? = null, limit: Int? = null): List<TaskData>

    fun findTasksByUserAndStatus(
        userId: String,
        status: TaskData.Status,
        skip: Int? = null,
        limit: Int? = null
    ): List<TaskData>

    fun saveTask(task: TaskData)

    fun deleteTask(id: String): Long
}