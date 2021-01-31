package com.github.lupuuss.todo.api.rest.repository.user

interface UserRepository {

    fun findAll(): List<UserData>

    fun findUserByLoginContains(query: String): List<UserData>

    fun findUserByLogin(login: String): UserData?

    fun findUserById(id: String): UserData?

    fun saveUser(user: UserData)

    fun deleteUser(id: String)
}