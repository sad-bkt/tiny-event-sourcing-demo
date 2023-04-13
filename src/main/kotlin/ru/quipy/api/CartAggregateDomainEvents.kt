package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val CART_CREATE_EVENT = "CART_CREATE_EVENT"
const val CART_CHECK = "CART_CHECK"
const val CART_ADD = "CART_ADD"


// API
@DomainEvent(name = CART_CREATE_EVENT)
class CartCreatedEvent(
    val cartId: UUID,
    val productId: UUID,
    val count: Int
) : Event<CartAggregate>(
    name = CART_CREATE_EVENT,
)

@DomainEvent(name = CART_CHECK)
class CartCheckEvent(
    val cartId: UUID
) : Event<CartAggregate>(
    name = CART_CHECK,
)

@DomainEvent(name = CART_ADD)
class CartUpdateEvent(
    val cartId: UUID,
    val productId: UUID,
    val count: Int
) : Event<ProductAggregate>(
    name = CART_ADD,
)