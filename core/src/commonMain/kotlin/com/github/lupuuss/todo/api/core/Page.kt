package com.github.lupuuss.todo.api.core

class Page<T>(
    val elements: List<T>,
    val pageNumber: Int,
    val pageSize: Int,
    isLast: Boolean
    ) {

    val nextPage: Int? = if (isLast) null else pageNumber + 1
}