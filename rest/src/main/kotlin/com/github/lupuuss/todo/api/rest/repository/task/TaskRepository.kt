package com.github.lupuuss.todo.api.rest.repository.task

interface TaskRepository {

    fun findTasksByUser(userId: String)

    fun findTasksByUserAndStatus(userId: String, status: TaskData.Status)

    fun saveTask(task: TaskData)

    fun deleteTask(id: String)
}