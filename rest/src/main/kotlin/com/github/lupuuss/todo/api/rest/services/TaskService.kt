package com.github.lupuuss.todo.api.rest.services

import com.github.lupuuss.todo.api.core.Page
import com.github.lupuuss.todo.api.core.task.NewTask
import com.github.lupuuss.todo.api.core.task.PatchTask
import com.github.lupuuss.todo.api.core.task.Task
import com.github.lupuuss.todo.api.rest.repository.task.TaskData
import com.github.lupuuss.todo.api.rest.repository.task.TaskRepository
import com.github.lupuuss.todo.api.rest.repository.user.UserData
import com.github.lupuuss.todo.api.rest.repository.user.UserRepository
import com.github.lupuuss.todo.api.rest.services.exception.ItemNotFoundException
import com.github.lupuuss.todo.api.rest.utils.date.DateProvider
import com.github.lupuuss.todo.api.rest.utils.mapping.mapFromDomain
import com.github.lupuuss.todo.api.rest.utils.mapping.mapToDomain

class TaskService(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val dateProvider: DateProvider
    ) {

    private fun getUser(login: String): UserData {
        return userRepository.findUserByLogin(login) ?: throw ItemNotFoundException("login", login)
    }

    fun getTasksByUserLogin(
        login: String,
        pageNumber: Int,
        pageSize: Int,
        status: Task.Status? = null
    ): Page<Task> {
        val user = getUser(login)

        if (status == null) {
            return Pager.page(pageNumber, pageSize) { skip, limit ->
                taskRepository.findTasksByUser(user._id!!, skip, limit).map { it.mapToDomain() }
            }
        }

        val taskStatus = status.mapFromDomain()

        return Pager.page(pageNumber, pageSize) { skip, limit ->
            taskRepository.findTasksByUserAndStatus(user._id!!, taskStatus, skip, limit).map { it.mapToDomain() }
        }
    }

    fun createNewTaskForUser(login: String, newTask: NewTask): TaskData {

        val user = getUser(login)

        val data = TaskData(
            dateProvider.timestamp(),
            user._id!!,
            newTask.name,
            newTask.description,
            newTask.status.mapFromDomain(),
        )

        val id = taskRepository.insertTask(data)!!

        return taskRepository.findTaskById(id)!!
    }

    fun patchTask(id: String, patch: PatchTask) {

        val task = taskRepository.findTaskById(id) ?: throw ItemNotFoundException("id", id)

        if (patch.explicitSetStatus()) {
            task.status = patch.status?.mapFromDomain()!!
        }

        if (patch.explicitSetName()) {
            task.name = patch.name!!
        }

        if (patch.explicitSetDescription()) {
            task.description = patch.description
        }

        taskRepository.replaceTask(task)
    }

    fun checkTaskBelongToUser(id: String, login: String): Boolean {

        val user = userRepository.findUserByLogin(login) ?: return false
        val task = taskRepository.findTaskById(id) ?: throw ItemNotFoundException("id", id)

        return user._id!! == task.userId
    }

    fun deleteTask(id: String): Long = taskRepository.deleteTask(id)
}