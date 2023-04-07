package ru.quipy.controller

import com.itmo.microservices.demo.users.api.model.AppUserModel
import com.itmo.microservices.demo.users.api.model.RegistrationRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.deleteUser
import ru.quipy.logic.registerUser
import ru.quipy.service.UserRepository
import ru.quipy.service.UserService
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder
) {
    @PostMapping
    @Operation(
        summary = "Register new user",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()])
        ]
    )
    fun register(@RequestBody request: RegistrationRequest) : ResponseEntity<Any> {
//        userService.registerUser(request)
        val event = userEsService.create { it.registerUser(request.toEntity(passwordEncoder)) }
        val resp = userRepository.save(event.user)
        return ResponseEntity<Any>(resp.toModel(), HttpStatus.CREATED)
    }

    @GetMapping("/me")
    @Operation(
        summary = "Get current user info",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "User not found", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getAccountData(@Parameter(hidden = true) @AuthenticationPrincipal user: UserDetails): ResponseEntity<Any> {
        val userData = userRepository.findOneByEmail(user.username)
        if (userData == null)
            return ResponseEntity<Any>(null, HttpStatus.NO_CONTENT)
        return ResponseEntity<Any>(userData.toModel(), HttpStatus.FOUND)
//        userService.getAccountData(user)
    }

    @DeleteMapping("/me")
    @Operation(
        summary = "Delete current user",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "User not found", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun deleteCurrentUser(@Parameter(hidden = true) @AuthenticationPrincipal user: UserDetails) =
            userEsService.create { it.deleteUser(user) }
}