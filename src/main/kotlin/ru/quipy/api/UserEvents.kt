package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import ru.quipy.entity.AppUser
import java.util.*

const val I_USER_CREATED_EVENT = "I_USER_CREATED_EVENT"
const val I_USER_DELETED_EVENT = "I_USER_DELETED_EVENT"
const val USER_CREATE_BASKET = "USER_CREATE_BASKET"

@DomainEvent(name = I_USER_CREATED_EVENT)
class UserCreatedEvent(
    val user: AppUser,
    createdAt: Long = System.currentTimeMillis()
) : Event<UserAggregate>(
    name = I_USER_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = I_USER_DELETED_EVENT)
class UserDeletedEvent(
    val email: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<UserAggregate>(
    name = I_USER_DELETED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = USER_CREATE_BASKET)
class UserCreateBasket(
    val basketId: UUID,
) : Event<UserAggregate>(
    name = USER_CREATE_BASKET
)
