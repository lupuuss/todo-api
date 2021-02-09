package com.github.lupuuss.todo.api.rest.services

import com.github.lupuuss.todo.api.core.Page
import com.github.lupuuss.todo.api.core.live.ItemChange
import com.github.lupuuss.todo.api.core.live.Operation
import com.github.lupuuss.todo.api.core.user.NewUser
import com.github.lupuuss.todo.api.core.user.PatchUser
import com.github.lupuuss.todo.api.core.user.User
import com.github.lupuuss.todo.api.rest.auth.hash.HashProvider
import com.github.lupuuss.todo.api.rest.repository.user.UserData
import com.github.lupuuss.todo.api.rest.repository.user.UserRepository
import com.github.lupuuss.todo.api.rest.services.exception.ItemAlreadyExistsException
import com.github.lupuuss.todo.api.rest.services.exception.ItemNotFoundException
import kotlin.reflect.KMutableProperty1

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

    fun patchUser(id: String, patch: PatchUser) {
        val user = repository.findUserById(id) ?: throw ItemNotFoundException("User", "id", id)

        val userData = patch.fillUserData(user)

        if (patch.isExplicitSet(PatchUser::password)) {
            userData.password = hash.generate(patch.password!!)
        }

        repository.replaceUser(userData)
    }

    fun addOnUserChangeListener(listener: suspend (ItemChange<User>) -> Unit): AutoCloseable {

        return repository.addOnUserChangeListener {
            listener(ItemChange(
                it._id, 
                Operation.valueOf(it.type.name),
                it.data?.mapToDomain()
            ))
        }
    }
}

private fun PatchUser.fillUserData(user: UserData): UserData {

    val propertiesMapping = mapOf<KMutableProperty1<PatchUser, *>, UserData.(Any) -> Unit>(
        PatchUser::login  to { arg -> login = arg as String },
        PatchUser::email to { arg -> email = arg as String },
        PatchUser::active to { arg -> active = arg as Boolean },
        PatchUser::role to { arg -> role = UserData.Role.valueOf((arg as User.Role).name) },
    )

    for ((patchProp, setter) in propertiesMapping) {

        if (this.isExplicitSet(patchProp)) {
            setter.invoke(user, patchProp.get(this)!!)
        }
    }

    return user
}
