package com.itmo.microservices.demo.users.api.model

import org.springframework.security.crypto.password.PasswordEncoder
import ru.quipy.entity.AppUser
import java.util.*

data class RegistrationRequest(
//        val username: String,
        val name: String,
        val surname: String,
        val email: String,
        val password: String
) {

    fun toEntity(passwordEncoder: PasswordEncoder, userId: UUID = UUID.randomUUID()): AppUser =
        AppUser( // username = this.username,
            name = this.name,
            surname = this.surname,
            email = this.email,
            password = passwordEncoder.encode(this.password),
            aggregateId = userId
        )

}