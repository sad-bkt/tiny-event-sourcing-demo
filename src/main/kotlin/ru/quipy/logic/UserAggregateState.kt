package ru.quipy.logic

import com.itmo.microservices.demo.users.api.model.AppUserModel
import org.springframework.stereotype.Component
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreateBasket
import ru.quipy.api.UserCreatedEvent
import ru.quipy.api.UserDeletedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

@Component
class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private lateinit var userStateId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()
    var deliveryIds = ArrayList<UUID>()
    private lateinit var basketId: UUID

    var active: Boolean = true

    lateinit var userData: AppUserModel

//    var items = mutableMapOf<UUID, ...>()

    override fun getId() = userStateId
    fun getDeliveries() = deliveryIds

    fun addDelivery(uuid: UUID) {
        deliveryIds.add(uuid)
    }
    fun getBasketId() = basketId

    fun existBasket(): Boolean {
        return this::basketId.isInitialized
    }
    @StateTransitionFunc
    fun registerUserApply(event: UserCreatedEvent) {
        userStateId = event.user.aggregateId
        userData = event.user.toModel()
    }

    @StateTransitionFunc
    fun deleteUserApply(event: UserDeletedEvent) {
        active = false
        updatedAt = System.currentTimeMillis()
    }

    @StateTransitionFunc
    fun createBasketUserApply(event: UserCreateBasket) {
        basketId = event.basketId
    }
}