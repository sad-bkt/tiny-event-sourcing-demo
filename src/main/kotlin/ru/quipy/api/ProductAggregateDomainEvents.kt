package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val PRODUCT_CREATE_EVENT = "PRODUCT_CREATE_EVENT"
const val PRODUCT_UPDATE = "PRODUCT_UPDATE"
const val PRODUCT_DELETE = "PRODUCT_DELETE"

// API
@DomainEvent(name = PRODUCT_CREATE_EVENT)
class ProductCreatedEvent(
    val productId: UUID,
    val productName: String,
    val count: Int
) : Event<ProductAggregate>(
    name = PRODUCT_CREATE_EVENT,
)

@DomainEvent(name = PRODUCT_UPDATE)
class ProductUpdateEvent(
    val count: Int
) : Event<ProductAggregate>(
    name = PRODUCT_UPDATE,
)

@DomainEvent(name = PRODUCT_DELETE)
class ProductDeleteEvent(
    val productId: UUID,
) : Event<ProductAggregate>(
    name = PRODUCT_DELETE,
)