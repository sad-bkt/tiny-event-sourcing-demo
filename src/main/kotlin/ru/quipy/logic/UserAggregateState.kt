package ru.quipy.logic

import com.itmo.microservices.demo.users.api.model.AppUserModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.api.UserDeletedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.service.UserRepository
import java.util.*

@Component
class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private lateinit var userStateId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    var active: Boolean = true

//    lateinit var userRepository: UserRepository
//    @Autowired
//    lateinit var passwordEncoder: PasswordEncoder

    lateinit var userData : AppUserModel

//    var items = mutableMapOf<UUID, ...>()

//    lateinit var eventBus: EventBus

//    @InjectEventLogger
//        private lateinit var eventLogger: EventLogger

    override fun getId() = userStateId

    @StateTransitionFunc
    fun registerUserApply(event: UserCreatedEvent) {
        userStateId = event.user.aggregateId
//        val userEntity = userRepository.save(event.user)
        userData = event.user.toModel()
    }

    @StateTransitionFunc
    fun deleteUserApply(event: UserDeletedEvent) {
//        userRepository.deleteById(event.email)
        active = false
    }
}