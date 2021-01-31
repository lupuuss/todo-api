package com.github.lupuuss.todo.api.rest.repository.user

interface UserRepository {

    fun findUserByLogin(login: String): UserData?

    fun findUserById(id: String): UserData?
}