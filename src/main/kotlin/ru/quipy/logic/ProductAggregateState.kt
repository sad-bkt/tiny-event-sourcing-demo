package ru.quipy.logic

import ru.quipy.api.ProductAggregate
import ru.quipy.api.ProductCreatedEvent
import ru.quipy.api.ProductDeleteEvent
import ru.quipy.api.ProductUpdateEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*


class ProductAggregateState : AggregateState<UUID, ProductAggregate> {
    private lateinit var productId: UUID
    private var productName: String = ""
    private var count: Int = 0


    override fun getId() = productId

    fun getProductName() = productName

    fun getProductCount() = count

    @StateTransitionFunc
    fun productCreate(event: ProductCreatedEvent) {
        productId = event.productId
        productName = event.productName
        count = event.count
    }

    @StateTransitionFunc
    fun productUpdate(event: ProductUpdateEvent) {
        count = event.count
    }

    @StateTransitionFunc
    fun productDelete(event: ProductDeleteEvent) {
        productId = event.productId
    }
}


/**
 * Demonstrates that the transition functions might be representer by "extension" functions, not only class members functions
 */