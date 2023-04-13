package ru.quipy.service

import com.itmo.microservices.demo.users.api.model.AppUserModel
import com.itmo.microservices.demo.users.api.model.RegistrationRequest
import org.springframework.security.core.userdetails.UserDetails
import ru.quipy.api.UserDeletedEvent

interface UserService {
    fun register(request: RegistrationRequest): AppUserModel

    fun getAccountData(user: UserDetails): AppUserModel?

    fun deleteUser(email: String): UserDeletedEvent?
}