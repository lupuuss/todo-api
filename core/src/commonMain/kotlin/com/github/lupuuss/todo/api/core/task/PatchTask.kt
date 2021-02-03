package com.github.lupuuss.todo.api.core.task

class PatchTask(map: Map<String, String?>? = null) {

    private val map: MutableMap<String, String?> = map?.toMutableMap() ?: mutableMapOf()

    var status: Task.Status?
    get() = map["status"]?.let { Task.Status.valueOf(it) }
    set(value) {
        map["status"] = value?.name
    }

    var name: String?
    get() = map["name"]
    set(value) {
        map["name"] = value
    }

    var description: String?
    get() = map["description"]
    set(value) {
        map["description"] = value
    }

    fun explicitSetStatus(): Boolean = map.containsKey("status")

    fun explicitSetName(): Boolean = map.containsKey("name")

    fun explicitSetDescription(): Boolean = map.containsKey("description")

    fun asJsonMap(): Map<String, String?> = map
}