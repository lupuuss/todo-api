package com.github.lupuuss.todo.api.rest.repository.mongo

import com.github.lupuuss.todo.api.rest.repository.DataChange
import com.mongodb.client.ChangeStreamIterable
import com.mongodb.client.FindIterable
import com.mongodb.client.model.changestream.ChangeStreamDocument
import com.mongodb.client.model.changestream.OperationType
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.TimeUnit

fun <T> FindIterable<T>.applyLimitsOptionally(skip: Int?, limit: Int?): FindIterable<T> {
    return this.let { if (skip == null) it else it.skip(skip) }
        .let { if (limit == null) it else it.limit(limit) }
}

private class CursorDisposer(
    val lock: Mutex,
    private val asyncJob: Deferred<*>,
    private val scope: CoroutineScope
) : AutoCloseable {

    override fun close() = runBlocking {
        asyncJob.cancel()

        lock.lock()
        lock.unlock()

        scope.cancel()
    }
}

fun <T> ChangeStreamIterable<T>.listen(listener: suspend (ChangeStreamDocument<T>) -> Unit): AutoCloseable {

    val cursor = maxAwaitTime(1000, TimeUnit.MILLISECONDS).iterator()

    val lock = Mutex()

    @Suppress("EXPERIMENTAL_API_USAGE")
    val newScope = CoroutineScope(newSingleThreadContext("MongoListener"))

    val asyncJob = newScope.async {

        lock.withLock {
            cursor.use { c ->
                while (isActive) {
                    c.tryNext()?.let { listener(it) }
                }
            }
        }
    }

    return CursorDisposer(lock, asyncJob, newScope)
}

fun OperationType.toDataChangeType(): DataChange.Type? = when(this) {
    OperationType.INSERT -> DataChange.Type.INSERT
    OperationType.UPDATE, OperationType.REPLACE -> DataChange.Type.UPDATE
    OperationType.DELETE -> DataChange.Type.DELETE
    OperationType.DROP, OperationType.DROP_DATABASE -> DataChange.Type.DELETE_ALL
    else -> null
}