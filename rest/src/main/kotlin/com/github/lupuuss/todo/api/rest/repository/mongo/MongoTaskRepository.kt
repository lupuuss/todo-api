package com.github.lupuuss.todo.api.rest.repository.mongo

import com.github.lupuuss.todo.api.rest.repository.DataChange
import com.github.lupuuss.todo.api.rest.repository.task.TaskData
import com.github.lupuuss.todo.api.rest.repository.task.TaskRepository
import com.mongodb.BasicDBObject
import com.mongodb.client.MongoClient
import com.mongodb.client.model.changestream.OperationType
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
            .sort(descending(TaskData::timestamp))
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

    override fun deleteTask(id: String): Long {

        val task = collection.findOneById(id) ?: return 0

        collection.updateOneById(id, BasicDBObject("\$set", BasicDBObject("delete", task.userId)))

        return collection.deleteOneById(id).deletedCount
    }

    override fun addOnTaskChangeListener(userId: String, listener: suspend (DataChange<TaskData>) -> Unit): AutoCloseable {

        return collection.watch().listen {

            if (it.operationType == OperationType.DELETE) return@listen

            val deletion = it
                .updateDescription
                ?.updatedFields
                ?.get("delete")
                ?.asString()
                ?.value

            if (userId == it.fullDocument?.userId || userId == deletion) {

                var type = it.operationType.toDataChangeType() ?: return@listen

                if (deletion != null) {
                    type = DataChange.Type.DELETE
                }

                listener(DataChange(it.documentKey?.get("_id")?.asString()?.value, type, it.fullDocument))
            }
        }
    }
}