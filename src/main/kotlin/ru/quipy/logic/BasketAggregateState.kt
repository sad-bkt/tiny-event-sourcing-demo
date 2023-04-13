package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*
import kotlin.collections.mutableMapOf


class BasketAggregateState : AggregateState<UUID, BasketAggregate> {
    private var basket: MutableMap<UUID, Int> = mutableMapOf()
    private lateinit var basketId: UUID
//    private var count: List<Int>

    fun getBasket() = basket

    @StateTransitionFunc
    fun basketCreate(event: BasketCreateEvent) {
        basketId = event.basketId
    }

    @StateTransitionFunc
    fun addProduct(event: BasketAddProductEvent) {
        basket[event.productId] = basket.getOrDefault(event.productId, 0) + event.count
    }

    @StateTransitionFunc
    fun changeCountProduct(event: BasketChangeCountProductEvent) {
        basket[event.productId] = event.count
    }


    @StateTransitionFunc
    fun deleteProduct(event: BasketDeleteProductEvent) {
        basket.remove(event.productId)
    }

    override fun getId() = basketId

}


/**
 * Demonstrates that the transition functions might be representer by "extension" functions, not only class members functions
 */