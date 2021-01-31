package com.github.lupuuss.todo.api.rest.repository.task.mongo

import com.github.lupuuss.todo.api.rest.repository.task.TaskData
import com.github.lupuuss.todo.api.rest.repository.task.TaskRepository
import com.github.lupuuss.todo.api.rest.utils.mongo.applyLimitsOptionally
import com.mongodb.client.MongoClient
import org.litote.kmongo.*

class MongoTaskRepository(driver: MongoClient, databaseName: String): TaskRepository {
    private val collection = driver
        .getDatabase(databaseName)
        .getCollection<TaskData>("task")

    override fun findTasksByUser(
        userId: String, skip: Int?, limit: Int?
    ): List<TaskData> {
        return collection
            .find(TaskData::userId eq userId)
            .sort(descending(TaskData::date))
            .applyLimitsOptionally(skip, limit)
            .toList()
    }

    override fun findTasksByUserAndStatus(
        userId: String, status: TaskData.Status, skip: Int?, limit: Int?
    ): List<TaskData> {

        return collection
            .find(and(TaskData::userId eq userId, TaskData::status eq status))
            .applyLimitsOptionally(skip, limit)
            .toList()
    }

    override fun saveTask(task: TaskData) = collection.save(task)

    override fun deleteTask(id: String): Long = collection.deleteOneById(id).deletedCount

}