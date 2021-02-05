package com.github.lupuuss.todo.api.rest.utils.mongo

import com.mongodb.client.ChangeStreamIterable
import com.mongodb.client.FindIterable
import com.mongodb.client.model.changestream.ChangeStreamDocument
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