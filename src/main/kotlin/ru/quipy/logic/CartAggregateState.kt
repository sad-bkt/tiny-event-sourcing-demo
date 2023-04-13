package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*


class CartAggregateState : AggregateState<UUID, CartAggregate> {
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
        productId = event.productId
        productName = event.productName
        count = event.count
    }
}


/**
 * Demonstrates that the transition functions might be representer by "extension" functions, not only class members functions
 */