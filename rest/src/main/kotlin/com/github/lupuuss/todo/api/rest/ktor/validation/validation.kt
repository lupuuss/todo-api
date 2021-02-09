package com.github.lupuuss.todo.api.rest.ktor.validation

fun String?.isValidEmail(): Boolean{

    if (isNullOrBlank()) return false

    return Regex("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$").matches(this)
}

fun String?.isNotValidEmail() = !isValidEmail()