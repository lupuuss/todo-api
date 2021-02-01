package com.github.lupuuss.todo.api.rest.repository.user

interface UserRepository {

    fun findAll(skip: Int? = null, limit: Int? = null): List<UserData>

    fun findUserByLoginContains(query: String, skip: Int? = null, limit: Int? = null): List<UserData>

    fun findUserByEmail(email: String): UserData?

    fun findUserByLogin(login: String): UserData?

    fun findUserById(id: String): UserData?

    fun saveUser(user: UserData)

    fun deleteUser(id: String): Long
}