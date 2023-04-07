package ru.quipy.logic

import com.itmo.microservices.demo.users.api.model.RegistrationRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import ru.quipy.api.UserCreatedEvent
import ru.quipy.api.UserDeletedEvent
import ru.quipy.entity.AppUser
import java.util.*

//fun UserAggregateState.findUser(username: String): AppUserModel? = userRepository
//    .findByIdOrNull(username)?.toModel()

fun UserAggregateState.registerUser(userData: AppUser): UserCreatedEvent {
//    val userEntity = userRepository.save(request.toEntity(passwordEncoder))
    return UserCreatedEvent(userData)
//    eventLogger.info(UserServiceNotableEvents.I_USER_CREATED, userEntity.username)
}

//fun UserAggregateState.getAccountData(requester: UserDetails): AppUserModel =
//    userRepository.findByIdOrNull(requester.username)?.toModel() ?:
//    throw Exception("User ${requester.username} not found")

fun UserAggregateState.deleteUser(user: UserDetails): UserDeletedEvent {
//    runCatching {
//        userRepository.deleteById(user.username)
//    }.onSuccess {
//        return UserDeletedEvent(user.username)
//        eventLogger.info(UserServiceNotableEvents.I_USER_DELETED, user.username)
//    }.onFailure {
//        throw Exception("User ${user.username} not found", it)
//    }
//    if (userRepository.findByIdOrNull(user.username) != null)
        return UserDeletedEvent(user.username)
//    throw Exception("User ${user.username} not found")
}
