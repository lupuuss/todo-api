package com.github.lupuuss.todo.api.rest.controller.live

import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

class OutgoingQueue<T> {

    private val mutex = Mutex()
    private val queue = LinkedList<T>()
    private var paused = false

    fun pause() = synchronized(mutex) {
        paused = true
    }

    suspend fun send(outgoing: SendChannel<T>, item: T) = mutex.withLock {
        if (paused){
            queue.add(item)
            return
        }

        outgoing.send(item)
    }

    suspend fun unpauseAndFlush(outgoing: SendChannel<T>) = mutex.withLock {

        while (queue.isNotEmpty()) {
            outgoing.send(queue.poll())
        }

        paused = false
    }
}