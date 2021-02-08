package com.github.lupuuss.todo.api.rest.utils.ktor

import com.github.lupuuss.todo.api.rest.auth.UserPrincipal
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import java.lang.IllegalArgumentException
import kotlin.reflect.KClass
import kotlin.reflect.cast

class RoleBasedAuthorization(configure: Configuration<*>) {
    val roles: Set<*> = configure.roles
    val roleExtractor: Principal.() -> Any by lazy { requireNotNull(configure.extractor) }

    class Configuration<T : Any> {
        var roles: Set<T> = emptySet()
        var extractor: (Principal.() -> T)? = null

        var typedOverwrite: Configuration<*>? = null

        fun <T : Any> typed(configuration: Configuration<T>.() -> Unit) {
            typedOverwrite = Configuration<T>().apply(configuration)
        }

        inline fun <reified T : Enum<T>, P : Principal> enumBased(cls: KClass<P>, crossinline extractor: P.() -> T) {
            typedOverwrite = Configuration<T>().apply {
                roles = enumValues<T>().toSet()
                this.extractor = { cls.cast(this).let(extractor) }
            }
        }
    }

    fun interceptPipeline(pipeline: ApplicationCallPipeline, roles: Set<Any>) {

        val phase = PipelinePhase("RoleAuthorization")

        pipeline.insertPhaseBefore(ApplicationCallPipeline.Call, phase)

        pipeline.intercept(phase) {

            val principal = call.principal<UserPrincipal>()!!
            val principalRole = principal.let(roleExtractor)
            if (principalRole !in roles) {

                logWarn("No permission for '${principal.login}'! Expected one of roles: $roles! Actual: $principalRole")

                call.respond(HttpStatusCode.Unauthorized)
                return@intercept finish()
            }
        }
    }

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration<Any>, RoleBasedAuthorization> {
        override val key: AttributeKey<RoleBasedAuthorization> = AttributeKey("RoleBasedAuthorization")

        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: Configuration<Any>.() -> Unit
        ): RoleBasedAuthorization {

            val baseConfig = Configuration<Any>().apply(configure)
            val config = baseConfig.typedOverwrite ?: baseConfig
            return RoleBasedAuthorization(config)
        }
    }
}

private class RoleRouteSelector : RouteSelector(RouteSelectorEvaluation.qualityConstant) {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
        return RouteSelectorEvaluation.Constant
    }
}

fun Route.withRoles(roles: Set<Any>, callback: Route.() -> Unit): Route {

    val feature = application.feature(RoleBasedAuthorization)

    if (!roles.all { feature.roles.contains(it) }) {
        throw IllegalArgumentException("$roles don't match roles defined in config! Defined roles: ${feature.roles}")
    }

    val roleRoute = createChild(RoleRouteSelector())

    feature.interceptPipeline(roleRoute, roles)

    callback(roleRoute)

    return roleRoute
}

fun Route.withRole(role: Any, callback: Route.() -> Unit): Route = withRoles(setOf(role), callback)

fun Route.authenticateWithRoles(roles: Set<Any>, callback: Route.() -> Unit) {
    authenticate {
        withRoles(roles, callback)
    }
}

fun Route.authenticateWithRole(role: Any, callback: Route.() -> Unit) = authenticateWithRoles(setOf(role), callback)