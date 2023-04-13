package ru.quipy.logic

import ru.quipy.api.UserCreateBasket
import ru.quipy.api.UserCreatedEvent
import ru.quipy.api.UserDeletedEvent
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