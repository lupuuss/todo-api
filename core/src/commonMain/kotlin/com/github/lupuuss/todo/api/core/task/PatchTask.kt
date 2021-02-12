package com.github.lupuuss.todo.api.core.task

import kotlin.reflect.KProperty1

class PatchTask(initMap: Map<String, String?>? = null) {

    private val map: MutableMap<String, String?> = initMap?.toMutableMap() ?: mutableMapOf()

    var status: Task.Status?
    get() = map["status"]?.let { Task.Status.valueOf(it) }
    set(value) {
        map["status"] = value?.name
    }

    var name: String? by map

    var description: String? by map

    fun <T> isExplicitSet(kProperty: KProperty1<PatchTask, T>) = map.containsKey(kProperty.name)

    fun asJsonMap(): Map<String, String?> = map
}