package ru.quipy.logic

import ru.quipy.api.BasketAddProductEvent
import ru.quipy.api.BasketChangeCountProductEvent
import ru.quipy.api.BasketCreateEvent
import ru.quipy.api.BasketDeleteProductEvent
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun BasketAggregateState.create(basketId: UUID): BasketCreateEvent {
    return BasketCreateEvent(
        basketId = basketId
    )
}

fun BasketAggregateState.addProduct(productId: UUID, count: Int): BasketAddProductEvent {
    return BasketAddProductEvent(
        productId = productId,
        count = count
    )
}

fun BasketAggregateState.changeCount(productId: UUID, count: Int): BasketChangeCountProductEvent {
    return BasketChangeCountProductEvent(
        productId = productId,
        count = count
    )
}

fun BasketAggregateState.delete(productId: UUID): BasketDeleteProductEvent {
    return BasketDeleteProductEvent(
        productId = productId,
    )
}
