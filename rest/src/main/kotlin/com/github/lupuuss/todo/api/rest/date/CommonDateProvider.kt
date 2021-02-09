package com.github.lupuuss.todo.api.rest.date

class CommonDateProvider : DateProvider {
    override fun timestamp(): Long = System.currentTimeMillis()
}