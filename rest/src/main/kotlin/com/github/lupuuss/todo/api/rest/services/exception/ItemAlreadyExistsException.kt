package com.github.lupuuss.todo.api.rest.services.exception

class ItemAlreadyExistsException(
    itemType: String, property: String, value: String
) : Exception("${itemType.capitalize()} with $property = $value already exists!")