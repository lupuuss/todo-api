package com.github.lupuuss.todo.api.rest.repository.mongo

import com.github.lupuuss.todo.api.rest.repository.DataChange
import com.mongodb.client.ChangeStreamIterable
import com.mongodb.client.FindIterable
import com.mongodb.client.model.changestream.ChangeStreamDocument
import com.mongodb.client.model.changestream.OperationType
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

fun <T> FindIterable<T>.applyLimitsOptionally(skip: Int?, limit: Int?): FindIterable<T> {
    return this.let { if (skip == null) it else it.skip(skip) }
        .let { if (limit == null) it else it.limit(limit) }
}

private class CursorDisposer(
    val lock: Lock
) : AutoCloseable {

    var isClosed: AtomicBoolean = AtomicBoolean(false)

    override fun close() {

        isClosed.set(true)

        lock.lock()
        lock.unlock()

        println("Listener closed!")
    }
}

fun <T> ChangeStreamIterable<T>.listen(listener: (ChangeStreamDocument<T>) -> Unit): AutoCloseable {

    val executor = Executors.newSingleThreadExecutor()

    val cursor = maxAwaitTime(1000, TimeUnit.MILLISECONDS).iterator()
    val disposer = CursorDisposer(ReentrantLock())

    executor.submit {

        disposer.lock.withLock {
            cursor.use {
                while (!disposer.isClosed.get()) {
                    it.tryNext()?.let(listener)
                }
            }
        }
    }

    return disposer
}

fun OperationType.toDataChangeType(): DataChange.Type? = when(this) {
    OperationType.INSERT -> DataChange.Type.INSERT
    OperationType.UPDATE, OperationType.REPLACE -> DataChange.Type.UPDATE
    OperationType.DELETE -> DataChange.Type.DELETE
    OperationType.DROP, OperationType.DROP_DATABASE -> DataChange.Type.DELETE_ALL
    else -> null
}