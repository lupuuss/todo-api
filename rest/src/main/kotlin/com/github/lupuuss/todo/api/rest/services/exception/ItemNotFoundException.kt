package com.github.lupuuss.todo.api.rest.services.exception

class ItemNotFoundException(
    itemType: String, property: String, value: String
) : Exception("${itemType.capitalize()} cannot be found with $property = $value!")