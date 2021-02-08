package com.github.lupuuss.todo.api.rest.services

import com.github.lupuuss.todo.api.core.Page
import com.github.lupuuss.todo.api.core.user.NewUser
import com.github.lupuuss.todo.api.core.user.User
import com.github.lupuuss.todo.api.rest.auth.hash.HashProvider
import com.github.lupuuss.todo.api.rest.repository.user.UserData
import com.github.lupuuss.todo.api.rest.repository.user.UserRepository
import com.github.lupuuss.todo.api.rest.services.exception.ItemAlreadyExistsException
import com.github.lupuuss.todo.api.rest.services.exception.ItemNotFoundException
import com.github.lupuuss.todo.api.rest.utils.mapping.mapToDomain

class UserService(
    private val repository: UserRepository,
    private val hash: HashProvider
) {

    fun getUser(id: String): User {

        return repository
            .findUserById(id)
            ?.mapToDomain()
            ?: throw ItemNotFoundException("User", "id", id)
    }

    fun createUser(user: NewUser): User {

        val hashedPassword = hash.generate(user.password)

        if (repository.findUserByLogin(user.login) != null) {
            throw ItemAlreadyExistsException("User", "login", user.login)
        }

        if (repository.findUserByEmail(user.email) != null) {
            throw ItemAlreadyExistsException("User", "email", user.email)
        }

        val userData = UserData(
            null,
            user.login,
            user.email,
            user.active,
            UserData.Role.valueOf(user.role.name),
            hashedPassword
        )

        val id = repository.insertUser(userData)

        return repository.findUserById(id)!!.mapToDomain()
    }

    fun getAllUsers(
        pageNumber: Int,
        pageSize: Int
    ): Page<User> {

        return Pager.page(pageNumber, pageSize) { skip, limit ->
            repository.findAll(skip, limit).map { it.mapToDomain() }
        }
    }
}