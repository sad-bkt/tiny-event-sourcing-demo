package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.entity.AppUser
import java.util.*

fun UserAggregateState.registerUser(userData: AppUser): UserCreatedEvent {
    return UserCreatedEvent(userData)
}

fun UserAggregateState.deleteUser(email: String): UserDeletedEvent {
    return UserDeletedEvent(email)
}

fun UserAggregateState.createBasket(basketId: UUID): UserCreateBasket{
    return UserCreateBasket(basketId)
}

fun UserAggregateState.addDelivery(deliveryId: UUID): UserDeliveryCreatedEvent {
    return UserDeliveryCreatedEvent(deliveryId)
}

fun UserAggregateState.deleteUserBasket(): UserDeleteBasket {
    return UserDeleteBasket()
}

