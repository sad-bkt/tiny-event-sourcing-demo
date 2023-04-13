package ru.quipy.logic

import org.springframework.security.core.userdetails.UserDetails
import ru.quipy.api.UserCreatedEvent
import ru.quipy.api.UserDeletedEvent
import ru.quipy.entity.AppUser

fun UserAggregateState.registerUser(userData: AppUser): UserCreatedEvent {
    return UserCreatedEvent(userData)
}

fun UserAggregateState.deleteUser(email: String): UserDeletedEvent {
        return UserDeletedEvent(email)
}
