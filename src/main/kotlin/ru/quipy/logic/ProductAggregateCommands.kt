package ru.quipy.logic

import ru.quipy.api.*
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProductAggregateState.create(id: UUID, name: String, cnt: Int): ProductCreatedEvent {
    return ProductCreatedEvent(
        productId = id,
        productName = name,
        count = cnt,
    )
}

fun ProductAggregateState.update(id: UUID, name: String, cnt: Int): ProductUpdateEvent {
    return ProductUpdateEvent(
        productId = id,
        productName = name,
        count = cnt,
    )
}
