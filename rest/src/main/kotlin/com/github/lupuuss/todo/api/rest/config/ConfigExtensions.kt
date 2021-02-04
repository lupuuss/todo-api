package com.github.lupuuss.todo.api.rest.config

import com.github.lupuuss.todo.api.rest.auth.AuthManager
import com.github.lupuuss.todo.api.rest.auth.JWTConfig
import com.github.lupuuss.todo.api.rest.auth.hash.BCryptHashProvider
import com.github.lupuuss.todo.api.rest.repository.task.TaskRepository
import com.github.lupuuss.todo.api.rest.repository.task.mongo.MongoTaskRepository
import com.github.lupuuss.todo.api.rest.repository.user.UserRepository
import com.github.lupuuss.todo.api.rest.repository.user.mongo.MongoUserRepository
import com.github.lupuuss.todo.api.rest.services.TaskService
import com.github.lupuuss.todo.api.rest.services.UserService
import com.github.lupuuss.todo.api.rest.utils.date.CommonDateProvider
import com.github.lupuuss.todo.api.rest.utils.date.DateProvider
import com.mongodb.client.MongoClient
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import org.kodein.di.*
import org.kodein.di.ktor.di
import org.litote.kmongo.KMongo

fun Application.configAuth() {

    val config = Config.get()

    JWTConfig.init(config.jwtSecret, config.jwtIssuer, config.jwtRealm)

    install(Authentication) {
        jwt {
            realm = config.jwtRealm
            verifier(JWTConfig.buildVerifier())
            validate { credentials ->
                val login = credentials.payload.claims["login"]?.asString() ?: return@validate null
                di().direct.instance<AuthManager>().validatePrincipal(login)
            }
        }
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

        bind<UserService>() with singleton { UserService(instance()) }
        bind<TaskService>() with singleton { TaskService(instance(), instance(), instance()) }

        bind<AuthManager>() with provider { AuthManager(BCryptHashProvider(), instance()) }
    }
}