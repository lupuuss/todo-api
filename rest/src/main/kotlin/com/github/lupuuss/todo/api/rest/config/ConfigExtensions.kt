package com.github.lupuuss.todo.api.rest.config

import com.auth0.jwt.algorithms.Algorithm
import com.github.lupuuss.todo.api.core.user.User
import com.github.lupuuss.todo.api.rest.auth.JwtAuthManager
import com.github.lupuuss.todo.api.rest.auth.UserPrincipal
import com.github.lupuuss.todo.api.rest.auth.hash.BCryptHashProvider
import com.github.lupuuss.todo.api.rest.auth.hash.HashProvider
import com.github.lupuuss.todo.api.rest.repository.task.TaskRepository
import com.github.lupuuss.todo.api.rest.repository.task.mongo.MongoTaskRepository
import com.github.lupuuss.todo.api.rest.repository.user.UserRepository
import com.github.lupuuss.todo.api.rest.repository.user.mongo.MongoUserRepository
import com.github.lupuuss.todo.api.rest.services.TaskService
import com.github.lupuuss.todo.api.rest.services.UserService
import com.github.lupuuss.todo.api.rest.utils.date.CommonDateProvider
import com.github.lupuuss.todo.api.rest.utils.date.DateProvider
import com.github.lupuuss.todo.api.rest.utils.ktor.RoleBasedAuthorization
import com.mongodb.client.MongoClient
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import org.kodein.di.*
import org.kodein.di.ktor.di
import org.litote.kmongo.KMongo

fun Application.configAuth() {

    val config = Config.get()

    install(Authentication) {
        jwt {
            val manager = di().direct.instance<JwtAuthManager>()

            realm = config.jwtRealm
            verifier(manager.verifier)

            validate { credentials ->

                val id = credentials.payload.claims["id"]?.asString() ?: return@validate null
                manager.validatePrincipal(id)
            }
        }
    }

    install(RoleBasedAuthorization) {
        enumBased(UserPrincipal::class, UserPrincipal::role)
    }
}

fun Application.configKodein() {

    di {
        val config = Config.get()

        val databaseName = config.mongoDbName

        bind<MongoClient>() with singleton { KMongo.createClient(config.mongoConnectStr) }
        bind<UserRepository>() with singleton { MongoUserRepository(instance(), databaseName) }
        bind<TaskRepository>() with singleton { MongoTaskRepository(instance(), databaseName) }

        bind<DateProvider>() with singleton { CommonDateProvider() }

        bind<HashProvider>() with singleton { BCryptHashProvider() }

        bind<UserService>() with singleton { UserService(instance(), instance()) }
        bind<TaskService>() with singleton { TaskService(instance(), instance(), instance()) }

        bind<JwtAuthManager>() with singleton {
            JwtAuthManager(
                hash = instance(),
                userRepository = instance(),
                algorithm = Algorithm.HMAC512(config.jwtSecret),
                issuer = config.jwtIssuer,
                realm = config.jwtRealm,
                expire = config.jwtExpire,
                refresh = config.jwtRefresh
            )
        }
    }
}