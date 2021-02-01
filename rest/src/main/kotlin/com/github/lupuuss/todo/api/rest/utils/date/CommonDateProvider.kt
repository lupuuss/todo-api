package com.github.lupuuss.todo.api.rest.utils.date

class CommonDateProvider : DateProvider {
    override fun timestamp(): Long = System.currentTimeMillis()
}