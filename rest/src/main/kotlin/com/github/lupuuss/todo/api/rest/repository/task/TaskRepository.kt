package com.github.lupuuss.todo.api.rest.repository.task

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

    fun insertTask(task: TaskData): String?

    fun deleteTask(id: String): Long
}