package com.github.lupuuss.todo.api.rest

/**
 * Helper class for [useBlock].
 * Collects [AutoCloseable] objects and close each of them in a safe way in [Resources.close] function.
 */
class Resources : AutoCloseable {

    private val resources = mutableListOf<AutoCloseable>()

    fun <T : AutoCloseable> T.use(): T {
        resources += this
        return this
    }

    override fun close() {

        var exception: Exception? = null

        for (resource in resources.reversed()) {

            try {
                resource.close()
            } catch (closeException: Exception) {

                if (exception == null) {
                    exception = closeException
                } else {
                    exception.addSuppressed(closeException)
                }
            }
        }

        if (exception != null) throw exception
    }
}

/**
 * Every [AutoCloseable] followed by [Resources.use] call, will be closed in order reversed to [Resources.use] calling
 * order, at the end of this block. It's a substitute for Java's try-with-resources, as Kotlin lacks this
 * feature for multiple resources.
 */
inline fun useBlock(usage: Resources.() -> Unit): Unit = Resources().use(usage)