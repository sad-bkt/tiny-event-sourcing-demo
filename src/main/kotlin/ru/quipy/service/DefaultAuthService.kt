package ru.quipy.service

import com.itmo.microservices.demo.auth.api.model.AuthenticationRequest
import com.itmo.microservices.demo.auth.api.model.AuthenticationResult
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.webjars.NotFoundException

@Service
class DefaultAuthService(
    private val userRepository: UserRepository,
    private val tokenManager: JwtTokenManager,
    private val passwordEncoder: PasswordEncoder
) : AuthService {

    override fun authenticate(request: AuthenticationRequest): AuthenticationResult {
        var user = userRepository.findOneByEmail(request.email)?.toModel()
            ?: throw NotFoundException("User with username ${request.email} not found")

        if (!passwordEncoder.matches(request.password, user.password))
            throw AccessDeniedException("Invalid password")

        val accessToken = tokenManager.generateToken(user.userDetails())
        val refreshToken = tokenManager.generateRefreshToken(user.userDetails())
        return AuthenticationResult(accessToken, refreshToken)
    }

    override fun refresh(authentication: Authentication): AuthenticationResult {
        val refreshToken = authentication.credentials as String
        val principal = authentication.principal as UserDetails
        val accessToken = tokenManager.generateToken(principal)
        return AuthenticationResult(accessToken, refreshToken)
    }
}
