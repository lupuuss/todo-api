package com.github.lupuuss.todo.api.rest.repository.user.mongo

import com.github.lupuuss.todo.api.rest.repository.user.UserData
import com.github.lupuuss.todo.api.rest.repository.user.UserRepository
import com.github.lupuuss.todo.api.rest.utils.mongo.applyLimitsOptionally
import com.mongodb.client.MongoClient
import org.litote.kmongo.*

class MongoUserRepository(driver: MongoClient, database: String) : UserRepository {

    private val collection = driver
        .getDatabase(database)
        .getCollection<UserData>("user")

    override fun findAll(skip: Int?, limit: Int?): List<UserData> {
        return collection
            .find()
            .applyLimitsOptionally(skip, limit)
            .toList()
    }

    override fun findUserByLoginContains(query: String, skip: Int?, limit: Int?): List<UserData> {
        return collection
            .find(UserData::login regex "$query*")
            .applyLimitsOptionally(skip, limit)
            .toList()
    }

    override fun findUserByEmail(email: String): UserData? = collection.findOne(UserData::email eq email)

    override fun findUserByLogin(login: String): UserData? = collection.findOne(UserData::login eq login)

    override fun findUserById(id: String): UserData? = collection.findOneById(id)

    override fun saveUser(user: UserData) = collection.save(user)

    override fun deleteUser(id: String): Long = collection.deleteOneById(id).deletedCount
}