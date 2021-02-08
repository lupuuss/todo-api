package com.github.lupuuss.todo.api.rest.services

import com.github.lupuuss.todo.api.core.Page
import com.github.lupuuss.todo.api.core.live.Operation
import com.github.lupuuss.todo.api.core.live.TaskChange
import com.github.lupuuss.todo.api.core.task.NewTask
import com.github.lupuuss.todo.api.core.task.PatchTask
import com.github.lupuuss.todo.api.core.task.Task
import com.github.lupuuss.todo.api.rest.repository.task.TaskData
import com.github.lupuuss.todo.api.rest.repository.task.TaskRepository
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

    fun getTasksByUserId(
        userId: String,
        pageNumber: Int,
        pageSize: Int,
        status: Task.Status? = null
    ): Page<Task> {

        if (status == null) {
            return Pager.page(pageNumber, pageSize) { skip, limit ->
                taskRepository.findTasksByUser(userId, skip, limit).map { it.mapToDomain() }
            }
        }

        val taskStatus = status.mapFromDomain()

        return Pager.page(pageNumber, pageSize) { skip, limit ->
            taskRepository.findTasksByUserAndStatus(userId, taskStatus, skip, limit).map { it.mapToDomain() }
        }
    }

    fun createNewTaskForUser(userId: String, newTask: NewTask): Task {

        if (userRepository.userNotExists(userId)) throw ItemNotFoundException("User", "id", userId)

        val data = TaskData(
            dateProvider.timestamp(),
            userId,
            newTask.name,
            newTask.description,
            newTask.status.mapFromDomain(),
        )

        val id = taskRepository.insertTask(data)

        return taskRepository.findTaskById(id)!!.mapToDomain()
    }

    fun patchTask(id: String, patch: PatchTask) {

        val task = taskRepository.findTaskById(id) ?: throw ItemNotFoundException("Task", "id", id)

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

    fun checkTaskBelongToUser(id: String, userId: String): Boolean {

        if (userRepository.userNotExists(userId)) return false

        val task = taskRepository.findTaskById(id) ?: throw ItemNotFoundException("Task", "id", id)

        return userId == task.userId
    }

    fun deleteTask(id: String): Long = taskRepository.deleteTask(id)

    fun addOnTaskChangedListener(userId: String, listener: (TaskChange) -> Unit): AutoCloseable {

        if (userRepository.userNotExists(userId)) throw ItemNotFoundException("User", "id", userId)

        return taskRepository.addOnTaskChangeListener(userId) {
            val taskChange = TaskChange(
                it._id,
                Operation.valueOf(it.type.name),
                it.task?.mapToDomain()
            )
            listener(taskChange)
        }
    }
}