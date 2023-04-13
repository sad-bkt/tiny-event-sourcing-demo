package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val BASKET_CREATE_EVENT = "PRODUCT_CREATE_EVENT"
const val BASKET_ADD_PRODUCT = "BASKET_ADD_PRODUCT"
const val BASKET_DELETE_PRODUCT = "BASKET_DELETE_PRODUCT"
const val BASKET_CHANGE_COUNT_PRODUCT = "BASKET_CHANGE_COUNT_PRODUCT"

// API
@DomainEvent(name = BASKET_CREATE_EVENT)
class BasketCreateEvent(
    val basketId: UUID,
) : Event<BasketAggregate>(
    name = BASKET_CREATE_EVENT,
)

@DomainEvent(name = BASKET_ADD_PRODUCT)
class BasketAddProductEvent(
    val productId: UUID,
    val count: Int,
) : Event<BasketAggregate>(
    name = BASKET_ADD_PRODUCT,
)

@DomainEvent(name = BASKET_CHANGE_COUNT_PRODUCT)
class BasketChangeCountProductEvent(
    val productId: UUID,
    val count: Int,
) : Event<BasketAggregate>(
    name = BASKET_CHANGE_COUNT_PRODUCT,
)

@DomainEvent(name = BASKET_DELETE_PRODUCT)
class BasketDeleteProductEvent(
    val productId: UUID,
) : Event<BasketAggregate>(
    name = BASKET_DELETE_PRODUCT,
)