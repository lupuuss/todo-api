package com.github.lupuuss.todo.api.rest.repository.task.mongo

import com.github.lupuuss.todo.api.rest.repository.task.TaskData
import com.github.lupuuss.todo.api.rest.repository.task.TaskDataChange
import com.github.lupuuss.todo.api.rest.repository.task.TaskRepository
import com.github.lupuuss.todo.api.rest.utils.mongo.applyLimitsOptionally
import com.mongodb.client.MongoClient
import com.mongodb.client.model.Filters
import com.mongodb.client.model.changestream.ChangeStreamDocument
import com.mongodb.client.model.changestream.OperationType
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import org.litote.kmongo.*

class MongoTaskRepository(driver: MongoClient, databaseName: String): TaskRepository {
    private val collection = driver
        .getDatabase(databaseName)
        .getCollection<TaskData>("task")

    override fun findTaskById(id: String): TaskData? {
        return collection.findOneById(id)
    }

    override fun findTasksByUser(
        userId: String, skip: Int?, limit: Int?
    ): List<TaskData> {
        return collection
            .find(TaskData::userId eq userId)
            .sort(descending(TaskData::timestamp))
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

    override fun replaceTask(task: TaskData) {
        collection.replaceOne(task)
    }

    override fun insertTask(task: TaskData): String {

        val id = ObjectId.get().toHexString()
        task._id = id

        collection.insertOne(task)

        return id
    }

    override fun deleteTask(id: String): Long = collection.deleteOneById(id).deletedCount

    override fun streamUserTaskChanges(userId: String): Sequence<TaskDataChange> {

        return collection
            .watch()
            .cursor()
            .asSequence()
            .filter { userId == it?.fullDocument?.userId }
            .map {

                val type = when (it.operationType) {
                    OperationType.INSERT -> TaskDataChange.Type.INSERT
                    OperationType.UPDATE -> TaskDataChange.Type.UPDATE
                    OperationType.REPLACE -> TaskDataChange.Type.UPDATE
                    OperationType.DELETE -> TaskDataChange.Type.DELETE
                    else -> throw IllegalStateException("Other types should be filtered before!")
                }

                TaskDataChange(it.documentKey!!["_id"]!!.asString().value, type, it.fullDocument)
            }
    }
}