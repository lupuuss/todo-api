package com.github.lupuuss.todo.api.rest.services.exception

class ItemNotFoundException(property: String, value: String) : Exception("Item cannot be found with $property = $value!")