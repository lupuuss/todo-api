package com.github.lupuuss.todo.api.rest.utils.mapping

import com.github.lupuuss.todo.api.core.task.Task
import com.github.lupuuss.todo.api.core.user.User
import com.github.lupuuss.todo.api.rest.repository.task.TaskData
import com.github.lupuuss.todo.api.rest.repository.user.UserData

fun UserData.mapToDomain() = User(
    _id!!,
    login,
    email,
    active,
    User.Role.valueOf(role.name)
)

fun TaskData.mapToDomain() = Task(
    _id!!,
    timestamp,
    userId,
    name,
    description,
    Task.Status.valueOf(status.name)
)

fun TaskData.Status.mapToDomain() = Task.Status.valueOf(name)

fun Task.Status.mapFromDomain() = TaskData.Status.valueOf(name)