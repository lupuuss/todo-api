package com.github.lupuuss.todo.api.rest.services

import com.github.lupuuss.todo.api.core.Page

object Pager {

    fun <T> page(pageNumber: Int, pageSize: Int, caller: (Int, Int) -> List<T>): Page<T> {

        val offset = pageNumber * pageSize
        val values = caller(offset, pageSize)

        val isLast = caller(offset + pageSize + 1, 1).isEmpty()

        return Page(values, pageNumber, pageSize, isLast)
    }
}