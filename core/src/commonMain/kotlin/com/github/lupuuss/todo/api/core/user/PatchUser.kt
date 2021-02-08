package com.github.lupuuss.todo.api.core.user

import kotlin.reflect.KProperty1

class PatchUser(initMap: Map<String, Any?>? = null) {

    private val map: MutableMap<String, Any?> = initMap?.toMutableMap() ?: mutableMapOf()

    var login: String? by map

    var email: String? by map

    var password: String? by map

    var active: Boolean? by map

    var role: User.Role?

    get() = map["role"]?.let { User.Role.valueOf(it as String) }
    set(value) {
        map["role"] = value?.name
    }

    fun <T> isExplicitSet(kProperty: KProperty1<PatchUser, T>) = map.containsKey(kProperty.name)

    fun asJsonMap() = map
}