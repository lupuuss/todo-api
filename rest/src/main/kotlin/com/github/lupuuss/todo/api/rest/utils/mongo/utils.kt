package com.github.lupuuss.todo.api.rest.utils.mongo

import com.mongodb.client.FindIterable

fun <T> FindIterable<T>.applyLimitsOptionally(skip: Int?, limit: Int?): FindIterable<T> {
    return this.let { if (skip == null) it else it.skip(skip) }
        .let { if (limit == null) it else it.limit(limit) }
}