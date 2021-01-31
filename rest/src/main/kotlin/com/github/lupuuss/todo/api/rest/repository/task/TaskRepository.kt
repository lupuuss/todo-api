package com.github.lupuuss.todo.api.rest.repository.task

interface TaskRepository {

    fun findTasksByUser(userId: String): List<TaskData>

    fun findTasksByUserAndStatus(userId: String, status: TaskData.Status): List<TaskData>

    fun saveTask(task: TaskData)

    fun deleteTask(id: String)
}