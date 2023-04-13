package ru.quipy.logic

import ru.quipy.api.ProductCreatedEvent
import ru.quipy.api.ProductDeleteEvent
import ru.quipy.api.ProductUpdateEvent
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

fun ProductAggregateState.updateCount(cnt: Int): ProductUpdateEvent {
    return ProductUpdateEvent(
        count = cnt,
    )
}

fun ProductAggregateState.delete(productId: UUID): ProductDeleteEvent {
    return ProductDeleteEvent(
        productId = productId,
    )
}
