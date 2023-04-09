package ru.quipy.service


import com.itmo.microservices.demo.users.api.model.AppUserModel
import com.itmo.microservices.demo.users.api.model.RegistrationRequest
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserDeletedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.deleteUser
import ru.quipy.logic.registerUser
import java.util.*

@Service
class DefaultUserService(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder
) : UserService {

    override fun register(request: RegistrationRequest): AppUserModel {
        val userData = userRepository.findOneByEmail(request.email)
        if (userData != null) {
            throw Exception("User already exists: $userData")
        }
        val event = userEsService.create { it.registerUser(request.toEntity(passwordEncoder)) }
        val resp = userRepository.save(event.user)
        return resp.toModel()
    }

    override fun getAccountData(user: UserDetails): AppUserModel? {
        val userData = userRepository.findOneByEmail(user.username)
        if (userData == null || userEsService.getState(userData.aggregateId)?.active == false)
            return null
        return userData.toModel()
    }

    override fun deleteUser(email: String): UserDeletedEvent? {

        val userData = userRepository.findOneByEmail(email)
        if (userData == null || userEsService.getState(userData.aggregateId)?.active == false) {
            if (userData != null)
                userRepository.delete(userData)
            return null
        }
        val res = userEsService.update(userData.aggregateId) { it.deleteUser(email) }
        userRepository.delete(userData)
        return res
    }
}