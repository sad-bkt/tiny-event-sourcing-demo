package com.itmo.microservices.demo.users.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import ru.quipy.logic.Delivery
import java.util.*

data class AppUserModel(
        val email: String,
        val name: String,
        val surname: String,
        val aggregateId: UUID,
        val role: String,
        @JsonIgnore
        val password: String) {

        fun userDetails(): UserDetails = User(email, password, Collections.singleton(SimpleGrantedAuthority(role)))
}
