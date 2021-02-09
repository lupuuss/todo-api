package com.github.lupuuss.todo.api.rest.repository.mongo

import com.github.lupuuss.todo.api.rest.repository.DataChange
import com.mongodb.client.model.changestream.OperationType

fun OperationType.toDataChangeType(): DataChange.Type? = when(this) {
    OperationType.INSERT -> DataChange.Type.INSERT
    OperationType.UPDATE, OperationType.REPLACE -> DataChange.Type.UPDATE
    OperationType.DELETE -> DataChange.Type.DELETE
    OperationType.DROP, OperationType.DROP_DATABASE -> DataChange.Type.DELETE_ALL
    else -> null
}